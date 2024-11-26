package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.*;

import static fr.yro.llmcraft.Citizens.TalkingCitizenParameters.Privacy.SHARED;

/**
 * Represents Citizen's NPC talking to you through one of your models.
 * Each NPC can either be shared, i.e. the NPC will remember what previous players said,
 * or personal, i.e. each player interacts with a different model on its own.
 */
public abstract class TalkingCitizen  {

    /**
     * The integer corresponds to the citizens ID of the NPC linked to the Talking Citizen.
     * Created one-time when the plugin starts, by {@link TalkingCitizenFactory#getTalkingCitizensFromConfig()}.
     */
    public static HashMap<Integer,TalkingCitizen> talkingCitizens;

    /**
     * Each Player (or console) must have its own {@link IGModel} to allow personal models to be created.
     *
     * Identifies individual {@link IGModel} for each {@link CommandSender}. The string is generally player's displayname or "Console".
     * If the NPC is Shared then the map will only contain one model identified by "".
     */
    public HashMap<String,IGModel> models;

    TalkingCitizenParameters parameters;

    public TalkingCitizen(TalkingCitizenParameters parameters) {
        this.models = new HashMap<>();
        // Creates a "global" model identified with the empty string in models.
        // It is the model that will be used if the TalkingCitizen is shared between all players.
        IGModel model = new IGModel(parameters.modelType,"npc-"+parameters.name+"-global", parameters.systemAppend);
        this.models.put("", model);

        this.parameters = parameters;
    }

    public abstract void chat(String s, CommandSender commandSender);

    /**
     *
     * @param commandSender the Player (or Console)
     * @return
     */
    public IGModel getModel(CommandSender commandSender){
        IGModel res = null;
        switch(this.getParameters().type){
            case PERSONAL:

                String identifier = this.getIdentifier(commandSender);
                String name = commandSender.getName();

                if(!models.containsKey(name)){
                    res = new IGModel(this.getParameters().modelType,
                            identifier, this.getParameters().systemAppend);
                    models.put(name,res);
                }
                res = this.models.get(commandSender.getName());
                break;

            case SHARED:
                res = this.models.get("");
                break;
        }
        
        return res;
    }



    TalkingCitizenParameters getParameters() {
        return this.parameters;
    }

    /**
     * Tries to retrieve a NPC from {@link NPCRegistry}.
     */
    public NPC getNPC(){
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        return registry.getById(this.getParameters().npcID);
    }


    /**
     * If the player is in range of the NPC : when the player sends a message, should the message only be sent to players in the radius of the NPC, or should everyone should be able to hear it?
     *
     */
    public boolean messageOnlyInRange() {
        return this.getParameters().messageOnlyInRange;
    }

    /**
     * Used for Holograms {@link TalkingCitizen}.
     */
    public void chatHologram(String s, CommandSender commandSender){

    }

    /**
     * /!\ Only use this function if the server has fully started; or #getNPC() will throw an exception.
     * @return The location of the CitizenNPC linked to the TalkingCitizen
     */
    public Location getLocation(){
        return this.getParameters().getNPC().getEntity().getLocation();
    }

    public String  toString(){
        return "Talking NPC " + this.getParameters().name + " Talking-Type:" + this.getTalkingType()
                + ". Model-Type : " + this.getParameters().modelType + ". Shared: " + this.getParameters().type +
                "\nCurrent number of conversations hold: " + this.models.size();
    }

    private String getTalkingType() {
        if(this instanceof ChatTalkingCitizen) return "Chat-Talking";
        else return "Hologram-Talking";
    }

    private boolean isShared() {
        return this.getParameters().type == SHARED;
    }

    public static boolean isTalkingCitizen(NPC npc){
        return talkingCitizens.containsKey(npc);
    }

    public static TalkingCitizen getTalkingFromNPC(NPC npc){
        return talkingCitizens.get(npc);
    }

    public Range getRange(){
        return this.getParameters().range;
    }

    public TalkingCitizenParameters.Privacy getPrivacy(){
        return this.getParameters().type;
    }

    protected String getName() {
        return this.getParameters().name;
    }

    public IGModelType getModelType(){
        return this.getParameters().modelType;
    }

    public String getSystemAppend(){
        return this.getParameters().systemAppend;
    }

    protected String getIdentifier(CommandSender commandSender) {
        return "npc-"+this.getParameters().name+"-"+commandSender.getName();
    }
}
