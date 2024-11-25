package fr.yro.llmcraft.Citizens;

import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.Logger;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

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

    private static HashMap<Integer,TalkingCitizen> getTalkingCitizensFromConfig() {
        HashMap<Integer,TalkingCitizen> res = new HashMap<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define NPCS in config.yml.");
        }
        Set<String> modelsPath = configSection.getKeys(false);
        modelsPath.forEach(s -> {
            TalkingCitizen tc = TalkingCitizenFactory.create(s);
            res.put(tc.getNPC().getId(), tc);
        });
        return res;
    }


    public static TalkingCitizen create(String s) {
        Logger.log(Level.CONFIG, "Initializing new TalkingCitizen " + s);


        TalkingCitizenParameters parameters = new TalkingCitizenParameters();
        parameters.name = s;
        parameters.modelType = IGModelType.modelsTypes.get(configSection.getString(s+".model"));
        if(parameters.modelType == null){
            Logger.log(Level.SEVERE, "Model type " + configSection.getString(s+".model") + " not found or not initialized. " +
                    "Maybe check your API Key or the name of the model in config.yml ?");
            return null;
        }
        parameters.type = TalkingCitizenParameters.Privacy.valueOf(configSection.getString(s+".type").toUpperCase());
        parameters.npcID = configSection.getInt(s+".citizen-id");
        TalkingCitizen.models = new HashMap<>();
        parameters.messageOnlyInRange = configSection.getBoolean(s+".message-only-in-range");
        parameters.systemAppend = configSection.getString(s+".system-append");
        int range = configSection.getInt(s+".range");
        parameters.range = new Range(Range.Type.WORLD, range);
        // Creates a "global" model identified with the empty string in models
        IGModel model = new IGModel(parameters.modelType,"npc-"+parameters.name+"-global", parameters.systemAppend);
        TalkingCitizen.models.put("", model);

        String type = (configSection.getString(s+".talking").toUpperCase());

        return switch(type){
            case "HOLOGRAM" -> new HologramTalkingCitizen(parameters);
            default -> new ChatTalkingCitizen(parameters);
        };
    }

}
