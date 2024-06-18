package fr.yro.llmcraft.Commands;

import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ModelType implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(strings.length == 0){
            commandSender.sendMessage("§cUsage: /modeltype <model type>");
            commandSender.sendMessage("§aAvailable models types: " + IGModelType.modelTypes().toString());
            return true;
        }

        String model = strings[0];

        if(!IGModelType.isModelType(model)){
            commandSender.sendMessage("§7 §cModel type not recognized.");
            commandSender.sendMessage("§aAvailable models types: " + IGModelType.modelTypes().toString());
            return true;
        }

        IGModelType modeltype = IGModelType.modelsTypes.get(model);
        commandSender.sendMessage(modeltype.toString());

        return true;
    }
}
