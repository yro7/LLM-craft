package fr.yronusa.llmcraft.Citizens;

import fr.yronusa.llmcraft.*;
import fr.yronusa.llmcraft.Model.IGModel;
import fr.yronusa.llmcraft.Model.IGModelType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
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
public class TalkingCitizen  {

    public static HashMap<NPC,TalkingCitizen> talkingCitizens;
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
    public NPC npc;
    public IGModelType modelType;
    public Type type;
    public Talking talkingType;
    public Range range;

    public TalkingCitizen(String s) {
        Logger.log(Level.CONFIG, "Initializing new TalkingCitizen " + s);
        this.name = s;
        this.modelType = IGModelType.modelsTypes.get(configSection.getString(s+".model"));
        System.out.println("model type :" + this.modelType);
        System.out.println(configSection.getString(s+".model"));
        System.out.println(s+".model");
        System.out.println(IGModelType.modelsTypes.keySet());
        System.out.println(IGModelType.modelsTypes.values());

        int id = configSection.getInt(s+".citizen-id");
        System.out.println("id : " + id);
        this.npc = CitizensAPI.getNPCRegistry().getById(configSection.getInt(s+".citizen-id"));
        
        this.type = Type.valueOf(configSection.getString(s+".type").toUpperCase());
        this.talkingType = Talking.valueOf(configSection.getString(s+".talking").toUpperCase());
        this.models = new HashMap<>();

        int range = configSection.getInt(s+".range");
        this.range = new Range(Range.Type.WORLD, range);
        // Creates a "global" model identified with the empty string in models
        IGModel model = new IGModel(this.modelType,"npc-"+this.name+"-global");
        models.put("", model);

    }


    public static void initialize(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Citizens");
        if (plugin == null){
            Logger.log(Level.WARNING, "Citizens soft-depend not found.");
            return;
        }
        talkingCitizens = new HashMap<>();
        TalkingCitizen.configSection = Config.config.getConfigurationSection("npcs");
        talkingCitizens = getTalkingCitizensFromConfig();

        System.out.println("§cDEBUG :");
        talkingCitizens.values().forEach(tc -> System.out.println("§c"+tc.toString()));
    }

    private static HashMap<NPC,TalkingCitizen> getTalkingCitizensFromConfig() {
        configSection = Config.config.getConfigurationSection("npcs");
        HashMap<NPC,TalkingCitizen> res = new HashMap<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define NPCS in config.yml.");
        }
        Set<String> modelsPath = configSection.getKeys(false);
        System.out.println("aaa" + Arrays.toString(modelsPath.toArray()));
        modelsPath.forEach(s -> {
            TalkingCitizen tc = new TalkingCitizen(s);
            System.out.println("bbb" + tc.toString());
            res.put(tc.npc,tc);
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
                            "npc-"+this.name+"-"+name);
                    this.models.put(name,newConversationModel);
                }
                this.models.get(name).chat(s, commandSender,this.range);
                break;
            case SHARED:
                models.get("").chat(s, commandSender, this.range);
                break;
        }
    }

    public void chatHologram(String s, CommandSender commandSender){

    }

    public Location getLocation(){
        return this.npc.getEntity().getLocation();
    }

    public String  toString(){
        return "Talking NPC " + this.name + " ID " + this.npc.getId() + " Talking-Type:" + this.talkingType
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
