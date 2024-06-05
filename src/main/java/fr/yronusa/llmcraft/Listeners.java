package fr.yronusa.llmcraft;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Listeners implements Listener {


    @EventHandler
    public void onPlayerEvent(BlockBreakEvent e) throws Exception {

        Player p = e.getPlayer();
        Block b = e.getBlock();
        //ModelManager.test2(p);
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        ModelListener ml = ModelListener.getListener(p);
        if(ml != null){
            ModelManager.execute(ml.modelType,ml.player,e.getMessage());
            ml.numberOfListen--;
            if(ml.numberOfListen <= 0){
                ModelListener.modelListeners.remove(ml);
            }
        }
    }

}
