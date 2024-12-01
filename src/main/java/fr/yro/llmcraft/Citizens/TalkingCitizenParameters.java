package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;

/**
 * Container object for {@link TalkingCitizen} parameters.
 */
public class TalkingCitizenParameters  {

    public String name;
    public int npcID;
    public IGModelType modelType;
    public String systemAppend;
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


}
