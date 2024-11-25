package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Commands.ModelType;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.*;

import static fr.yro.llmcraft.Citizens.TalkingCitizenParameters.Privacy.SHARED;
import static fr.yro.llmcraft.Citizens.TalkingCitizenParameters.Talking.CHAT;
import static fr.yro.llmcraft.Citizens.TalkingCitizenParameters.Talking.HOLOGRAM;


/**
 * Represents Citizen's NPC talking to you through one of your models.
 * Each NPC can either be shared, i.e. the NPC will remember what previous players said,
 * or personal, i.e. each player interacts with a different model on its own.
 */
public abstract class TalkingCitizen  {

    /**
     * The integer corresponds to the citizens ID of the NPC linked to the Talking Citizen.
     */
    public static HashMap<Integer,TalkingCitizen> talkingCitizens;

    /**
     * Identifies individual {@link IGModel} for each {@link CommandSender}. The string is generally player's displayname or "Console".
     * If the NPC is {@link SHARED}, then the map will only contain one model identified by "".
     */
    public static HashMap<String,IGModel> models;


    TalkingCitizenParameters parameters;


    public void chat(String s, CommandSender commandSender){
        switch(this.getParameters().talkingType){
            case CHAT -> chatChat(s, commandSender);
            case HOLOGRAM -> chatHologram(s,commandSender);
        }
    }

    /**
     * Used for {@link TalkingCitizenParameters.Talking.CHAT} {@link TalkingCitizen}.
     */
    public void chatChat(String s, CommandSender commandSender){
        switch(this.getParameters().type){
            case PERSONAL:
                String name = commandSender.getName();
                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGModel(this.getParameters().modelType,
                            "npc-"+this.getParameters().name+"-"+name, this.getParameters().systemAppend);
                    this.getParameters().models.put(name,newConversationModel);
                }
                this.getParameters().models.get(name).chat(s, commandSender,this.getParameters().range);
                break;
            case SHARED:
                models.get("").chat(s, commandSender, this.getParameters().range);
                break;
        }
    }

    private TalkingCitizenParameters getParameters() {
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

    public Location getLocation(){
        return this.getParameters().getNPC().getEntity().getLocation();
    }

    public String  toString(){
        return "Talking NPC " + this.getParameters().name + " Talking-Type:" + this.getParameters().talkingType
                + ". Model-Type : " + this.getParameters().modelType + ". Shared: " + this.getParameters().type +
                "\nCurrent number of conversations hold: " + this.getParameters().models.size();
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

}
