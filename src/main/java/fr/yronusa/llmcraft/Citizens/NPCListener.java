package fr.yronusa.llmcraft.Citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.http.WebSocket;

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
        System.out.println("AAAAAAA");
        for(TalkingCitizen tc : TalkingCitizen.talkingCitizens.values()){
            System.out.println("range: " + tc.range.range);
            System.out.println("distance:" + tc.getLocation().distance(loc));
            if(tc.getLocation().distance(loc) < tc.range.range){
                tc.chat(e.getMessage(),e.getPlayer());
            }

        }
    }


}
