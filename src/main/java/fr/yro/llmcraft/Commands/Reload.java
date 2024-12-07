package fr.yro.llmcraft.Commands;

import fr.yro.llmcraft.Citizens.TalkingCitizen;
import fr.yro.llmcraft.Citizens.TalkingCitizenFactory;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.LLM_craft;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import fr.yro.llmcraft.Model.Limiter;
import fr.yro.llmcraft.Model.ListeningModel;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Reload
{
    public static void reload(CommandSender sender){
        new BukkitRunnable() {
            @Override
            public void run() {
                LLM_craft.removeHolograms();
                Config.load();
                Limiter.initialize();

                IGModel.activeModels = new HashMap<>();
                ListeningModel.listeningModels = new HashMap<>();
                IGModelType.initialize();
                TalkingCitizenFactory.initialize();
                sender.sendMessage("§7* §aLLMCraft successfully reloaded.");
            }
        }.runTaskAsynchronously(LLM_craft.getInstance());

    }
}
