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

import static fr.yro.llmcraft.Citizens.TalkingCitizen.models;


/**
 * Represents Citizen's NPC talking to you through one of your models.
 * Each NPC can either be shared, i.e. the NPC will remember what previous players said,
 * or personal, i.e. each player interacts with a different model on its own.
 */
public class TalkingCitizenParameters  {

    public enum Privacy {
        PERSONAL,
        SHARED
    }

    public String name;
    public int npcID;
    public IGModelType modelType;
    public String systemAppend;
    public Privacy type;
    public Range range;
    public boolean messageOnlyInRange;


    public NPC getNPC(){
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        return registry.getById(this.npcID);
    }


    /**
     * If the player is in range of the NPC : when the player sends a message, should the message only be sent to players in the radius of the NPC, or should everyone should be able to hear it?
     *
     */
    public boolean messageOnlyInRange() {
        return this.messageOnlyInRange;
    }


    public Location getLocation(){
        return this.getNPC().getEntity().getLocation();
    }

    public String  toString(){
        return "Talking NPC " + this.name
                + ". Model-Type : " + this.modelType + ". Shared: " + this.type +
                "\nCurrent number of conversations hold: " + models.size();
    }

    private boolean isShared() {
        return this.type == Privacy.SHARED;
    }


    public Range getRange(){
        return this.range;
    }

    public Privacy getPrivacy(){
        return this.type;
    }

    protected String getName() {
        return this.name;
    }

    public IGModelType getModelType(){
        return this.modelType;
    }

    public String getSystemAppend(){
        return this.systemAppend;
    }

}
