package fr.yronusa.llmcraft;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerEvent(BlockBreakEvent e) throws Exception {

        Player p = e.getPlayer();
        Block b = e.getBlock();
        PythonIntegration.test();

    }
}
