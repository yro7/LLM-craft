package fr.yronusa.llmcraft;

import org.bukkit.plugin.java.JavaPlugin;

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
        fr.yronusa.llmcraft.Config.load();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
