package fr.yronusa.llmcraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LlmCraft implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        commandSender.sendMessage("§2§l§m-|--- §a§l LLM-Craft §2§l§m----|-");
        commandSender.sendMessage("§aCreated by yro");
        commandSender.sendMessage("");
        commandSender.sendMessage("§aCommands:");
        commandSender.sendMessage("§a- /ask <model> <prompt>");
        commandSender.sendMessage("§a- /listen <player> <model> <number of times>");
        commandSender.sendMessage("§a- /instance <model type> <name of the new model>");

        return true;
    }
}
