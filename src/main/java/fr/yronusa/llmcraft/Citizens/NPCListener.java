package fr.yronusa.llmcraft.Citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NPCListener implements Listener {


    @EventHandler
    public void onNPCCLick(NPCRightClickEvent e){
        NPC npc = e.getNPC();
        if(TalkingCitizen.isTalkingCitizen(npc)){
            TalkingCitizen tc = TalkingCitizen.getTalkingFromNPC(npc);
            tc.chatChat(" ", e.getClicker());

        }
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e){
        Location loc = e.getPlayer().getLocation();
        for(TalkingCitizen tc : TalkingCitizen.talkingCitizens.values()){
            int radius = tc.range.range;
            if(tc.getLocation().distance(loc) < radius) {
                tc.chat(e.getMessage(), e.getPlayer());
                if (tc.messageOnlyInRange()) {
                    e.getRecipients().removeIf(p -> p.getLocation().distance(loc) > radius);
                }
            }
        }
    }


}
