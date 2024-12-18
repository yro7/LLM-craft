package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Citizens.Hologram.HologramTalkingCitizen;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.LLM_craft;
import fr.yro.llmcraft.Logger;
import fr.yro.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import static fr.yro.llmcraft.LLM_craft.premium;

public class TalkingCitizenFactory {

    public static ConfigurationSection configSection;

    public static void initialize(){
        TalkingCitizenFactory.configSection = Config.config.getConfigurationSection("npcs");
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Citizens");
        if (plugin == null){
            Logger.log(Level.WARNING, "Citizens soft-depend not found.");
            return;
        }
        TalkingCitizen.talkingCitizens = new HashMap<>();
        TalkingCitizen.talkingCitizens = getTalkingCitizensFromConfig();

    }

    protected static HashMap<Integer,TalkingCitizen> getTalkingCitizensFromConfig() {
        HashMap<Integer,TalkingCitizen> res = new HashMap<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define NPCS in config.yml.");
        }
        Set<String> modelsPath = configSection.getKeys(false);
        modelsPath.forEach(s -> {
            TalkingCitizen tc = TalkingCitizenFactory.create(s);
            res.put(tc.getParameters().npcID, tc);
        });
        return res;
    }


    public static TalkingCitizen create(String s) {
        if(!LLM_craft.citizensPresent){
            Logger.log(Level.SEVERE, "Luckperms is not enabled on your server, skipping NPC " + s + " initialization.");
            return null;
        }
        Logger.log(Level.CONFIG, "Initializing new TalkingCitizen " + s);

        TalkingCitizenParameters parameters = new TalkingCitizenParameters();
        parameters.name = s;
        parameters.modelType = IGModelType.modelsTypes.get(configSection.getString(s+".model"));
        if(parameters.modelType == null){
            Logger.log(Level.SEVERE, "Model type " + configSection.getString(s+".model") + " not found or not initialized. " +
                    "Maybe check your API Key or the name of the model in config.yml ?");
            return null;
        }

        parameters.npcID = configSection.getInt(s+".citizen-id");
        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if(registry.getById(parameters.npcID) == null){
            Logger.log(Level.WARNING, "NPC n°" + parameters.npcID + " not found. Please check config.yml.");
        }
        if(TalkingCitizen.talkingCitizens.containsKey(parameters.npcID)){
            Logger.log(Level.WARNING, "Duplicate npc-id found in config.yml, the last one will override the other(s).");
        }

        parameters.messageOnlyInRange = configSection.getBoolean(s+".message-only-in-range");
        parameters.systemAppend = configSection.getString(s+".system-append");
        int range = configSection.getInt(s+".range");
        parameters.range = new Range(Range.Type.WORLD, range);

        String type = (configSection.getString(s+".talking").toUpperCase());

        // If non-premium version, disable holographic display
        if(!premium) return new ChatTalkingCitizen(parameters);

        return switch(type){
            case "HOLOGRAM" -> new HologramTalkingCitizen(parameters);
            default -> new ChatTalkingCitizen(parameters);
        };
    }

}
