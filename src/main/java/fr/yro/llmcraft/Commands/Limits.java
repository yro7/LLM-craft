package fr.yro.llmcraft.Commands;

import fr.yro.llmcraft.Model.IGModelType;
import fr.yro.llmcraft.Model.Limiter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Limits implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(strings.length == 0){
            commandSender.sendMessage("§cUsage: /limits <model type>");
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
        Optional<Limiter> limiterOptional = Optional.ofNullable(modeltype.limits);
        limiterOptional.ifPresentOrElse(limiter ->  commandSender.sendMessage(limiter.toString()),
                () -> commandSender.sendMessage("§4No usage limits found for this model. Beware, API abuse is possible."));

        return true;
    }
}
