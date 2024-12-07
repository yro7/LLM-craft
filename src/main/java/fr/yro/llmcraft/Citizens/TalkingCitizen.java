package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Citizens.Hologram.HologramTalkingCitizen;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelParameters;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.*;

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
        this.parameters = parameters;
    }

    public abstract IGModel createModel(CommandSender sender);



    public void chat(String s, CommandSender commandSender){
        this.getModel(commandSender).chat(s,commandSender, this.getRange());
    }

    /**
     *
     * @param commandSender the Player (or Console)
     * @return
     */
    public IGModel getModel(CommandSender commandSender){
        IGModel res;
        String name = switch(this.getParameters().modelType.parameters.visibility) {
            case PERSONAL -> commandSender.getName();
            case SHARED -> "";

        };
        if(!models.containsKey(name)){
            res = this.createModel(commandSender);
            models.put(name,res);
        }
        return models.get(name);
    }



    protected TalkingCitizenParameters getParameters() {
        return this.parameters;
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
                + ". Model-Type : " + this.getParameters().modelType + ". Shared: " + this.getParameters().modelType.parameters.visibility +
                "\nCurrent number of conversations hold: " + this.models.size();
    }

    private String getTalkingType() {
        if(this instanceof ChatTalkingCitizen) return "Chat-Talking";
        else return "Hologram-Talking";
    }


    public static boolean isTalkingCitizen(NPC npc){
        return talkingCitizens.containsKey(npc.getId());
    }

    public static TalkingCitizen getTalkingFromNPC(NPC npc){
        return talkingCitizens.get(npc.getId());
    }

    public Range getRange(){
        return this.getParameters().range;
    }



    protected String getIdentifier(CommandSender commandSender) {
        return "npc-"+this.getParameters().name+"-"+commandSender.getName();
    }

    public IGModelParameters.Visibility getVisibility(){
        return this.parameters.modelType.parameters.visibility;
    }

    public boolean isPrivate(){
        return this.getVisibility().equals(IGModelParameters.Visibility.PERSONAL);
    }
}
