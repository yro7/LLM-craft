package fr.yronusa.llmcraft.Commands;

import fr.yronusa.llmcraft.Citizens.TalkingCitizen;
import fr.yronusa.llmcraft.Config;
import fr.yronusa.llmcraft.LLM_craft;
import fr.yronusa.llmcraft.Logger;
import fr.yronusa.llmcraft.Model.IGModel;
import fr.yronusa.llmcraft.Model.IGModelType;
import fr.yronusa.llmcraft.Model.ListeningModel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Reload
{
    public static void reload(CommandSender sender){
        Config.load();
        IGModel.activeModels = new HashMap<>();
        ListeningModel.listeningModels = new HashMap<>();

        IGModelType.initialize();
        TalkingCitizen.initialize();



        sender.sendMessage("§7* §aLLMCraft successfully reloaded.");

    }
}
