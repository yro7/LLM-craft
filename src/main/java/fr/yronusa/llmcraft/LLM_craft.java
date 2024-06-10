package fr.yronusa.llmcraft;

import fr.yronusa.llmcraft.Citizens.NPCListener;
import fr.yronusa.llmcraft.Citizens.TalkingCitizen;
import fr.yronusa.llmcraft.Commands.*;
import fr.yronusa.llmcraft.Model.IGModel;
import fr.yronusa.llmcraft.Model.IGModelType;
import fr.yronusa.llmcraft.Model.ListeningModel;
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
        Logger.logger = this.getLogger();
        saveDefaultConfig();
        Config.load();



        IGModel.activeModels = new HashMap<>();
        ListeningModel.listeningModels = new HashMap<>();
        IGModelType.initialize();
        TalkingCitizen.initialize();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new NPCListener(), this);
        this.getCommand("ask").setExecutor(new ChatCommand());
        this.getCommand("listen").setExecutor(new Listen());
        this.getCommand("instance").setExecutor(new CreateInstance());
        this.getCommand("llmcraft").setExecutor(new LlmCraft());
        this.getCommand("model").setExecutor(new Model());
        this.getCommand("models").setExecutor(new Model());
        this.getCommand("modeltype").setExecutor(new ModelType());
        this.getCommand("modeltypes").setExecutor(new ModelType());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
