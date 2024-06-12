package fr.yronusa.llmcraft.Model;

import fr.yronusa.llmcraft.Config;
import fr.yronusa.llmcraft.LLM_craft;
import fr.yronusa.llmcraft.Logger;
import net.luckperms.api.model.group.Group;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Level;

/**
 * A {@link Limiter} allows to save how many times can a player use a {@link IGModelType}.
 * Limiters  do not distinguish {@link IGModel} usages individually, only {@link IGModelType}.

 * If for any reasons the Limiter had an error during initialization, throw {@link LimiterInitializationException}.
 * This avoids that an unspotted error leads players to over-use an IGModelType which could leads
 * to expensive API cost.
 */
public class Limiter {

    public static ConfigurationSection configSection;
    public static HashMap<String,Limiter> limiters;

    public String modelName;
    public String denyMessage;
    public HashMap<Group,Integer> limits;
    public HashMap<CommandSender,Integer> usages;

    public Limiter(String modelName, String denyMessage, HashMap<Group, Integer> limits, HashMap<CommandSender, Integer> usages) {
        this.modelName = modelName;
        this.denyMessage = denyMessage;
        this.limits = limits;
        this.usages = usages;
    }

    public Limiter(String s) throws LimiterInitializationException {
        this.modelName = s;
        this.limits = new HashMap<>();
        this.usages = new HashMap<>();
        // Used to prevent two limiters for having the same name, to avoid admin errors
        // when setting up limits (such as copy/paste)
        this.denyMessage = configSection.getString(s+".deny-message");

        if(limiters.containsKey(s)){
            Logger.log(Level.SEVERE, "Two limiters with the same name have been found. Please, check your config.yml.");
            throw new LimiterInitializationException(LimiterInitializationException.Reason.NAME_CONFLICT, this);
        }

        ConfigurationSection groupConfigSection = configSection.getConfigurationSection(s+".groups");
        for(String section : groupConfigSection.getKeys(false)){
            Group group = LLM_craft.luckPermsAPI.getGroupManager().getGroup(section);
            if(group == null) {
                Logger.log(Level.SEVERE, "Group '" + section +"' not found in limiter " + this.modelName + ". Please, check your config.yml");
                throw new LimiterInitializationException(LimiterInitializationException.Reason.GROUP_NOT_FOUND, this);
            }
            else {
                int limit = configSection.getInt(section);
                this.limits.put(group, limit);
            }
        }
    }

    public void use(CommandSender sender){
        usages.compute(sender, (key, value) -> (value == null) ? 0 : value + 1);
    }

    public static void initialize(){
        Limiter.limiters = new HashMap<>();
        Limiter.configSection = Config.config.getConfigurationSection("usage-limits");


        if(configSection == null){
            Logger.log(Level.WARNING, "You don't have any limiter in your config.yml. Beware of the mighty API prices !");
            return;
        }

        Limiter.limiters = Limiter.getLimitersFromConfig();

        limiters.values().forEach(limiter -> System.out.print(limiter.toString()));
    }

    public static HashMap<String, Limiter> getLimitersFromConfig() {
        HashMap<String, Limiter> res = new HashMap<>();
        Set<String> limitersPath = configSection.getKeys(false);
        for(String limiterPath : limitersPath){

            try {
                Limiter limiter = new Limiter(limiterPath);
                res.put(limiterPath, limiter);
            } catch (LimiterInitializationException e) {
                Logger.log(Level.SEVERE, "ERROR: Limiter " + e.limiter.modelName + " had a problem during initialization: " +
                        e.reason + ". ModelType '" + e.limiter.modelName + "' will be locked for players to avoid API abuse.");
                res.put(limiterPath, new Limiter("NULL",e.getDenyMessage(), null, null));
            }
        }
        return res;
    }


    /**
     * Player limit is set as the max limit of all their group's limits.
     */
    public int maxUsage(CommandSender sender){
        int res = -1;
        for(Group group : this.limits.keySet()){
            if(sender.hasPermission("group."+group.getName())){
                int limitOfGroup = this.getLimits().get(group);
                if(res < limitOfGroup){
                    res = limitOfGroup;
                }
            }
        }

        return res;
    }


    public boolean canUse(CommandSender sender){
        // The console is exempt of limitations
        if(sender instanceof ConsoleCommandSender){
            return true;
        }

        //-
        if(this.modelName.equals("NULL")) return false;


        int usages = this.usages.get(sender);
        int maxUsage = this.maxUsage(sender);

        // If maxUsages == -1, then the sender is not limited.
        return (maxUsage != -1) && (usages < this.maxUsage(sender));


    }

    public static class LimiterInitializationException extends Throwable {

        public String getDenyMessage() {
            return this.limiter.denyMessage;
        }

        public enum Reason {
            NAME_CONFLICT,
            GROUP_NOT_FOUND,
        }

        public Reason reason;
        public Limiter limiter;

        public LimiterInitializationException(Reason reason, Limiter limiter) {
            this.reason = reason;
            this.limiter = limiter;
        }
    }

    public HashMap<Group, Integer> getLimits(){
        return this.limits;
    }

    public String toString(){
        return "Limiter " + this.modelName + ". "
                + "\n Deny message : " + this.denyMessage
                + "\n Limits: " + this.limits
                + "\n Usages: " + this.usages;
    }
}
