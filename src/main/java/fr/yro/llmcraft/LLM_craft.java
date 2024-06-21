package fr.yro.llmcraft;

import fr.yro.llmcraft.Citizens.NPCListener;
import fr.yro.llmcraft.Citizens.TalkingCitizen;
import fr.yro.llmcraft.Commands.*;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import fr.yro.llmcraft.Model.Limiter;
import fr.yro.llmcraft.Model.ListeningModel;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class LLM_craft extends JavaPlugin {

    public static LLM_craft instance;

    public static LLM_craft getInstance(){
        return instance;
    }
    public static LuckPerms luckPermsAPI;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Logger.logger = this.getLogger();
        saveDefaultConfig();
        Config.load();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        luckPermsAPI = LuckPermsProvider.get();
        Limiter.initialize();

        IGModel.activeModels = new HashMap<>();
        ListeningModel.listeningModels = new HashMap<>();
        IGModelType.initialize();
        TalkingCitizen.initialize();
        System.out.println("TESSSSt");
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
