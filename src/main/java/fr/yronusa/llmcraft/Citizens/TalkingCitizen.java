package fr.yronusa.llmcraft.Citizens;

import fr.yronusa.llmcraft.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

/**
 * Represents Citizen's NPC talking to you through one of your models.
 * Each NPC can either be shared, i.e. the NPC will remember what previous players said,
 * or personal, i.e. each player interacts with a different model on its own.
 */
public class TalkingCitizen   {

    public static List<TalkingCitizen> talkingCitizens;
    public static ConfigurationSection configSection;

    public enum Type {
        PERSONAL,
        SHARED
    }

    public enum Talking {
        CHAT,
        H0LOGRAM,
    }

    public String name;
    /**
     * Identifies individual {@link IGModel} for each {@link CommandSender}. The string is generally player's displayname or "Console".
     * If the NPC is {@link Type#SHARED}, then the map will only contain one model identified by "".
     */
    public HashMap<String,IGModel> models;
    public NPC npc;
    public IGModelType modelType;
    public Type type;
    public Talking talkingType;

    public TalkingCitizen(String s) {
        Logger.log(Level.CONFIG, "Initializing new TalkingCitizen " + s);
        this.name = s;
        this.modelType = IGModelType.modelsTypes.get(configSection.getString(s+".model"));
        this.npc = CitizensAPI.getNPCRegistry().getById(configSection.getInt(s+".citizen-id"));
        this.type = Type.valueOf(configSection.getString(s+".type"));
        this.talkingType = Talking.valueOf(configSection.getString(s+".talking"));
    }

    public static void initialize(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Citizens");
        if (plugin == null){
            Logger.log(Level.WARNING, "Citizens soft-depend not found.");
            return;
        }
        talkingCitizens = new ArrayList<>();
        TalkingCitizen.configSection = Config.config.getConfigurationSection("npcs");
        talkingCitizens = getTalkingCitizensFromConfig();
    }

    private static List<TalkingCitizen> getTalkingCitizensFromConfig() {
        configSection = Config.config.getConfigurationSection("npcs");
        List<TalkingCitizen> res = new ArrayList<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define NPCS in config.yml.");
        }
        Set<String> modelsPath = configSection.getKeys(false);
        modelsPath.forEach(s -> res.add(new TalkingCitizen(s)));
        return res;
    }


    /**
     * Used for {@link Talking#CHAT} {@link TalkingCitizen}.
     */
    public void chatChat(String s, CommandSender commandSender, int range){
        NPC npc = this.npc;
        if(this.type.equals(Type.PERSONAL)){

            models.get(commandSender.getName()).chat(s,commandSender);
        }

        else{
            models.get("").chat(s, commandSender);
        }
    }


    public String  toString(){
        return "Talking NPC " + this.name + " ID " + this.npc.getId() + " Talking-Type:" + this.talkingType
                + ". Model-Type : " + this.modelType + ". Shared: " + this.type +
                "\nCurrent number of conversations hold: " + this.models.size();
    }


}
