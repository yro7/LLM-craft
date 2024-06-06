package fr.yronusa.llmcraft.Commands;

import fr.yronusa.llmcraft.IGModelTypes;
import fr.yronusa.llmcraft.ModelListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Listen implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        // Usage: /listen <player> <model> <number of times>

        if(strings.length != 3){
            commandSender.sendMessage("§7* §cUsage: /listen <player> <model> <number of times>");
        }

        ModelListener ml = new ModelListener(Bukkit.getPlayer(strings[0]),
                                             IGModelTypes.MODEL.valueOf(strings[1].toUpperCase()),
                                             Integer.parseInt(strings[2]));
        ModelListener ml2 = ModelListener.getListener(Bukkit.getPlayer((strings[0])));
        if(ml2 != null) commandSender.sendMessage("§7* §cWarning: the player was already listened.");
        ModelListener.modelListeners.add(ml);


        return true;
    }
}
