package fr.yronusa.llmcraft.Commands;

import fr.yronusa.llmcraft.Config;
import fr.yronusa.llmcraft.IGModel;
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

        String modelIdentifier = strings[0];
        if(!IGModel.isModel(modelIdentifier)){
            commandSender.sendMessage("§7* §cThis model hasn't been recognized.");
            commandSender.sendMessage("§aAvailable models types: " + IGModel.modelTypes().toString());
        }

        IGModel model = IGModel.getModel(modelIdentifier);
        String prompt = concatenateWithoutFirst(strings);
        String answer = model.getPrefix() + model.chat(prompt);
        commandSender.sendMessage(answer);





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
