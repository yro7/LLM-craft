package fr.yro.llmcraft.Commands;

import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CreateInstance implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission("llmcraft.instance")) {
            commandSender.sendMessage("§7* §cYou don't have the permission to do that.");
            return true;
        }

        // strings[0] is the model name
        if(strings.length != 2){
            commandSender.sendMessage("§7 §cUsage: /instance <model type> <identifier>");
            commandSender.sendMessage("§aAvailable models types: " + IGModelType.modelTypes().toString());
            return true;
        }

        String modelType = strings[0];
        if(!IGModelType.isModelType(modelType)){
            commandSender.sendMessage("§7* §cSorry, this model type has not been recognized");
            commandSender.sendMessage("§aAvailable models types: " + IGModelType.modelTypes().toString());
            return true;
        }

        IGModelType type = IGModelType.modelsTypes.get(modelType);
        String identifier = strings[1];

        if(IGModel.activeModels.containsKey(identifier)){
            commandSender.sendMessage("§7* §cSorry, a model already has the same name.");
            return true;
        }

        IGModel.createModel(type,identifier);
        commandSender.sendMessage("§7* §aSuccessfully instanced the new model ! You can chat with him using /ask " + identifier + " <message>.");

        return true;
    }
}
