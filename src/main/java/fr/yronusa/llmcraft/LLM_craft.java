package fr.yronusa.llmcraft;

import fr.yronusa.llmcraft.Commands.ChatCommand;
import fr.yronusa.llmcraft.Commands.CreateInstance;
import fr.yronusa.llmcraft.Commands.Listen;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

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
        Config.load();

        IGModelTypes.modelsTypes = new HashMap<>();
        IGModel.activeModels = new HashMap<>();

        IGModelTypes.configSection = Config.config.getConfigurationSection("models");
        IGModelTypes.modelsTypes = IGModelTypes.getIGModelsFromConfig();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        this.getCommand("ask").setExecutor(new ChatCommand());
        this.getCommand("listen").setExecutor(new Listen());
        this.getCommand("instance").setExecutor(new CreateInstance());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
