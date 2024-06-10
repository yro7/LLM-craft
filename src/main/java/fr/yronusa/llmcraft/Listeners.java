package fr.yronusa.llmcraft;

import fr.yronusa.llmcraft.Model.IGModel;
import fr.yronusa.llmcraft.Model.ListeningModel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e){
        ListeningModel.getModelsListening(e.getPlayer())
                .forEach(model -> model.chat(e.getMessage(), e.getPlayer()));
    }

}
