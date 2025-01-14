package fr.yro.llmcraft;

import dev.langchain4j.service.OnCompleteOrOnError;
import dev.langchain4j.service.TokenStream;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import fr.yro.llmcraft.Citizens.Hologram.HologramTalkingCitizen;
import fr.yro.llmcraft.Citizens.Hologram.IGStreamModel;
import fr.yro.llmcraft.Citizens.NPCListener;
import fr.yro.llmcraft.Citizens.TalkingCitizen;
import fr.yro.llmcraft.Citizens.TalkingCitizenFactory;
import fr.yro.llmcraft.Commands.*;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import fr.yro.llmcraft.Model.Limiter;
import fr.yro.llmcraft.Model.ListeningModel;
import net.citizensnpcs.api.CitizensAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class LLM_craft extends JavaPlugin {

    public static LLM_craft instance;
    public static LLM_craft getInstance(){
        return instance;
    }
    public static LuckPerms luckPermsAPI;

    public static boolean luckpermsPresent;
    public static boolean dhPresent;
    public static boolean citizensPresent;

    public static final boolean premium = true;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Logger.logger = this.getLogger();
        resolveDependencies();
        saveDefaultConfig();
        Config.load();
        Limiter.initialize();
        IGModel.activeModels = new HashMap<>();
        ListeningModel.listeningModels = new HashMap<>();
        IGModelType.initialize();
        TalkingCitizenFactory.initialize();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new NPCListener(), this);

        bStatsEnable();
        registerCommands();
        if(premium) Logger.log(Level.SEVERE, "Premium version enabled, thanks you for your support!");
    }

    private void resolveDependencies() {
        try{
            luckPermsAPI = LuckPermsProvider.get();
            luckpermsPresent = true;
        } catch(NoClassDefFoundError e){
            Logger.log(Level.SEVERE, "Luckperms dependency not found, Limiters won't work !");
            luckpermsPresent = false;
        }

        try {
            CitizensAPI.getNPCRegistry();
            citizensPresent = true;
        } catch (IllegalStateException e) {
            Logger.log(Level.SEVERE, "Citizens not enabled, Talking Citizens won't work !");
            citizensPresent = false;
        }

        try {
            DecentHologramsAPI.get();
            dhPresent = true;
        } catch (NoClassDefFoundError e) {
            Logger.log(Level.SEVERE, "Decent Holograms not enabled, Hologram-Talking Citizens won't work !");
            dhPresent = false;
        }

    }

    @Override
    public void onDisable() {
        removeHolograms();
    }


    public void bStatsEnable(){
        int pluginId = 24003;
        Metrics metrics = new Metrics(this, pluginId);

    }

    public void registerCommands(){
        this.getCommand("ask").setExecutor(new ChatCommand());
        this.getCommand("listen").setExecutor(new Listen());
        this.getCommand("instance").setExecutor(new CreateInstance());
        this.getCommand("llmcraft").setExecutor(new LlmCraft());
        this.getCommand("model").setExecutor(new Model());
        this.getCommand("models").setExecutor(new Model());
        this.getCommand("modeltype").setExecutor(new ModelType());
        this.getCommand("modeltypes").setExecutor(new ModelType());
        this.getCommand("limits").setExecutor(new Limits());
        this.getCommand("limiter").setExecutor(new Limits());
        this.getCommand("limiters").setExecutor(new Limits());
        this.getCommand("limit").setExecutor(new Limits());
    }


    public static void removeHolograms(){
        TalkingCitizen.talkingCitizens
                .values()
                .stream()
                .filter(tc -> tc instanceof HologramTalkingCitizen)
                .forEach(tc -> {
                    // Remove the remaining holograms
                    HologramTalkingCitizen htc = (HologramTalkingCitizen) tc;
                    htc.holograms.values().forEach(Hologram::delete);

                    // Replace all TokenStreams by "do nothing" streams to stop generation
                    htc.models.values().stream()
                            .filter(model -> model instanceof IGStreamModel)
                            .forEach(igModel -> ((IGStreamModel) igModel).lastTokenStream.set(new TokenStream() {
                                @Override
                                public OnCompleteOrOnError onNext(Consumer<String> consumer) {
                                    return null;
                                }
                            }));

                });
    }
}
