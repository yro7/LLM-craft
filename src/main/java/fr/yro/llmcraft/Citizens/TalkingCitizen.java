package fr.yro.llmcraft.Citizens;

import com.google.common.collect.Iterators;
import fr.yro.llmcraft.*;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;


/**
 * Represents Citizen's NPC talking to you through one of your models.
 * Each NPC can either be shared, i.e. the NPC will remember what previous players said,
 * or personal, i.e. each player interacts with a different model on its own.
 */
public class TalkingCitizen  {

    /**
     * The integer corresponds to the citizens ID of the NPC linked to the Talking Citizen.
     */
    public static HashMap<Integer,TalkingCitizen> talkingCitizens;
    public static ConfigurationSection configSection;

    public enum Type {
        PERSONAL,
        SHARED
    }

    public enum Talking {
        CHAT,
        HOLOGRAM,
    }

    public String name;
    /**
     * Identifies individual {@link IGModel} for each {@link CommandSender}. The string is generally player's displayname or "Console".
     * If the NPC is {@link Type#SHARED}, then the map will only contain one model identified by "".
     */
    public HashMap<String,IGModel> models;
    public int npcID;
    public IGModelType modelType;
    public String systemAppend;
    public Type type;
    public Talking talkingType;
    public Range range;
    public boolean messageOnlyInRange;

    public TalkingCitizen(String s) {
        Logger.log(Level.CONFIG, "Initializing new TalkingCitizen " + s);
        this.name = s;
        this.modelType = IGModelType.modelsTypes.get(configSection.getString(s+".model"));
        if(this.modelType == null){
            Logger.log(Level.SEVERE, "Model type " + configSection.getString(s+".model") + " not found or not initialized. " +
                    "Maybe check your API Key or the name of the model in config.yml ?");
            return;
        }
        this.type = Type.valueOf(configSection.getString(s+".type").toUpperCase());
        this.npcID = configSection.getInt(s+".citizen-id");
        this.talkingType = Talking.valueOf(configSection.getString(s+".talking").toUpperCase());
        this.models = new HashMap<>();
        this.messageOnlyInRange = configSection.getBoolean(s+".message-only-in-range");
        this.systemAppend = configSection.getString(s+".system-append");
        int range = configSection.getInt(s+".range");
        this.range = new Range(Range.Type.WORLD, range);
        // Creates a "global" model identified with the empty string in models
        IGModel model = new IGModel(this.modelType,"npc-"+this.name+"-global", this.systemAppend);
        models.put("", model);

    }


    public static void initialize(){
        configSection = Config.config.getConfigurationSection("npcs");
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Citizens");
        if (plugin == null){
            Logger.log(Level.WARNING, "Citizens soft-depend not found.");
            return;
        }
        talkingCitizens = new HashMap<>();
        TalkingCitizen.configSection = Config.config.getConfigurationSection("npcs");
        talkingCitizens = getTalkingCitizensFromConfig();

    }

    private static HashMap<Integer,TalkingCitizen> getTalkingCitizensFromConfig() {
        HashMap<Integer,TalkingCitizen> res = new HashMap<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define NPCS in config.yml.");
        }
        Set<String> modelsPath = configSection.getKeys(false);
        modelsPath.forEach(s -> {
            TalkingCitizen tc = new TalkingCitizen(s);
            res.put(tc.npcID,tc);
        });
        return res;
    }


    public void chat(String s, CommandSender commandSender){
        switch(this.talkingType){
            case CHAT -> chatChat(s, commandSender);
            case HOLOGRAM -> chatHologram(s,commandSender);
        }
    }

    /**
     * Used for {@link Talking#CHAT} {@link TalkingCitizen}.
     */
    public void chatChat(String s, CommandSender commandSender){
        switch(this.type){
            case PERSONAL:
                String name = commandSender.getName();
                if(!models.containsKey(name)){
                    IGModel newConversationModel = new IGModel(this.modelType,
                            "npc-"+this.name+"-"+name, this.systemAppend);
                    this.models.put(name,newConversationModel);
                }
                this.models.get(name).chat(s, commandSender,this.range);
                break;
            case SHARED:
                models.get("").chat(s, commandSender, this.range);
                break;
        }
    }

    /**
     * Tries to retrieve a NPC from {@link NPCRegistry}.
     */
    public NPC getNPC(){
        TalkingCitizen tc = this;
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        return registry.getById(this.npcID);
    }


    /**
     * If the player is in range of the NPC : when the player sends a message, should the message only be sent to players in the radius of the NPC, or should everyone should be able to hear it?
     *
     */
    public boolean messageOnlyInRange() {
        return this.messageOnlyInRange;
    }

    /**
     * Used for {@link Talking#HOLOGRAM} {@link TalkingCitizen}.
     */
    public void chatHologram(String s, CommandSender commandSender){

    }

    public Location getLocation(){
        return this.getNPC().getEntity().getLocation();
    }

    public String  toString(){
        return "Talking NPC " + this.name + " Talking-Type:" + this.talkingType
                + ". Model-Type : " + this.modelType + ". Shared: " + this.type +
                "\nCurrent number of conversations hold: " + this.models.size();
    }

    private boolean isShared() {
        return this.type == Type.SHARED;
    }

    public static boolean isTalkingCitizen(NPC npc){
        return talkingCitizens.containsKey(npc);
    }

    public static TalkingCitizen getTalkingFromNPC(NPC npc){
        return talkingCitizens.get(npc);
    }

}
