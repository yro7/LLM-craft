package fr.yro.llmcraft.Commands;

import fr.yro.llmcraft.Model.IGModel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Model implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(strings.length == 0){
            commandSender.sendMessage("§cUsage: /model <model>");
            commandSender.sendMessage("§aAvailable models: " + IGModel.activeModels.keySet().toString());
            return true;
        }

        String model = strings[0];

        if(!IGModel.isModel(model)){
            commandSender.sendMessage("§7 §cModel not recognized.");
            commandSender.sendMessage("§aAvailable models: " + IGModel.activeModels.keySet().toString());
            return true;
        }

        IGModel igModel = IGModel.getModel(model);
        commandSender.sendMessage(igModel.toString());
        commandSender.sendMessage("Use /modeltype " + igModel.modelType.name + " for information of it's model type.");

        return true;
    }
}
