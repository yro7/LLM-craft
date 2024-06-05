package fr.yronusa.llmcraft;

import fr.yronusa.llmcraft.Commands.ChatCommand;
import fr.yronusa.llmcraft.Commands.Listen;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class LLM_craft extends JavaPlugin {

    public static LLM_craft instance;

    public static LLM_craft getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        ModelListener.modelListeners = new ArrayList<>();
        fr.yronusa.llmcraft.Config.load();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        this.getCommand("ask").setExecutor(new ChatCommand());
        this.getCommand("listen").setExecutor(new Listen());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
