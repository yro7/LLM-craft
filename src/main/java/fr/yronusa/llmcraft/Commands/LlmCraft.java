package fr.yronusa.llmcraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LlmCraft implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(strings.length == 1 && strings[0].equals("reload")){
            Reload.reload(commandSender);
            return true;
        }
        commandSender.sendMessage("§2§l§m-|--- §a§l LLM-Craft §2§l§m----|-");
        commandSender.sendMessage("§aCreated by yro for Vikicraft (GNU GPL v3)");
        commandSender.sendMessage("");
        commandSender.sendMessage("§aCommands:");
        commandSender.sendMessage("§a- /model <model> | Get informations on a model");
        commandSender.sendMessage("§a- /ask <model> <prompt>");
        commandSender.sendMessage("§a- /listen <player> <model> <number of times> ");
        commandSender.sendMessage("§a- /instance <model type> <name of the new model> | Creates a new model from a modeltype");
        commandSender.sendMessage("§a- /llm reload  |  reload config.yml");


        return true;
    }
}
