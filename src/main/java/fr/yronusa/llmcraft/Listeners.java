package fr.yronusa.llmcraft;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Listeners implements Listener {


    @EventHandler
    public void onPlayerEvent(BlockBreakEvent e) throws Exception {

        IGModel model = IGModel.getModel("spy1");
        model.chat(e.getEventName() + " " + e.getBlock(), e.getPlayer());
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e){
        ListeningModel.getModelsListening(e.getPlayer())
                .forEach(model -> model.chat(e.getMessage(), e.getPlayer()));
    }

}
