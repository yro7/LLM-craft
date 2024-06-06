package fr.yronusa.llmcraft.Commands;

import fr.yronusa.llmcraft.Config;
import fr.yronusa.llmcraft.IGModelTypes;
import fr.yronusa.llmcraft.ModelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission("llmcraft.ask")) {
            commandSender.sendMessage("§7* §cYou don't have the permission to do that.");
            return true;
        }

        // strings[0] is the model name
        if(strings.length <= 1){
            commandSender.sendMessage("§7 §cUsage: /ask <model> <prompt>");
            return true;
        }

        try{
            IGModelTypes.MODEL m = IGModelTypes.MODEL.valueOf(strings[0].toUpperCase());
            if(!Config.availablesModels.contains(m)){
                commandSender.sendMessage("§7* §cSorry, this model is not available.");
                commandSender.sendMessage("§aAvailable models: " + Config.availablesModels.toString());
            }

            String prompt = concatenateWithoutFirst(strings);

            ModelManager.execute(m, commandSender, prompt);

        } catch(IllegalArgumentException e){
            commandSender.sendMessage("§7* §cSorry, this model has not been recognized.");
            commandSender.sendMessage("§aAvailable models: " + Config.availablesModels.toString());
        }

        return true;
    }


    public static String concatenateWithoutFirst(String[] strings) {
        if (strings == null || strings.length <= 1) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < strings.length; i++) {
            sb.append(strings[i]);
            sb.append((" "));
        }

        return sb.toString();
    }
}
