package fr.yro.llmcraft.Commands;

import fr.yro.llmcraft.Citizens.TalkingCitizen;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import fr.yro.llmcraft.Model.Limiter;
import fr.yro.llmcraft.Model.ListeningModel;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class Reload
{
    public static void reload(CommandSender sender){
        Config.load();
        IGModel.activeModels = new HashMap<>();
        ListeningModel.listeningModels = new HashMap<>();
        Limiter.limiters = new HashMap<>();

        IGModelType.initialize();
        TalkingCitizen.initialize();
        Limiter.initialize();


        sender.sendMessage("§7* §aLLMCraft successfully reloaded.");

    }
}
