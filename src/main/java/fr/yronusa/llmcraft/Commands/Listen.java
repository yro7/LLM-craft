package fr.yronusa.llmcraft.Commands;

import fr.yronusa.llmcraft.IGModel;
import fr.yronusa.llmcraft.IGModelTypes;
import fr.yronusa.llmcraft.ListeningModel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class Listen implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        // Usage: /listen <player> <model> <number of times>


        if(strings.length != 3) {
            commandSender.sendMessage("§cUsage:");
            commandSender.sendMessage("§c/listen <player> <model> <number of times to listen>");
            return true;
        }

        Player p = Bukkit.getPlayer(strings[0]);

        if(p == null){
            commandSender.sendMessage("§7* §cPlease select an online Player.");
            return true;
        }

        String modelIdentifier = strings[1];

        if(!IGModel.isModel(modelIdentifier)){
            commandSender.sendMessage("§7* §cThis model hasn't been recognized.");
            commandSender.sendMessage("§aAvailable models: " + IGModel.modelTypes().toString());
            return true;
        }

        IGModel model = IGModel.getModel(modelIdentifier);

        ListeningModel listeningModel = new ListeningModel(model, List.of(p));

        return true;
    }

}