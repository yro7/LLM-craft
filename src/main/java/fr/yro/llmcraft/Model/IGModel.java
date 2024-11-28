package fr.yro.llmcraft.Model;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import fr.yro.llmcraft.*;
import fr.yro.llmcraft.Citizens.Range;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

import static fr.yro.llmcraft.Citizens.Range.Type.GLOBAL;

/**
 * Represents an instance of a {@link IGModelType}.
 * Using instances allows you to have similar models but with a different context (e.g one per player)
 */

public class IGModel {

    public interface Assistant {
        String chat(String message);
    }

    /**
     * Used to store all active models, to be able to monitor how many there are instanced on the server.
     */
    public static HashMap<String,IGModel> activeModels;

    public IGModelType modelType;
    public String identifier;
    public Assistant assistant;
    public ChatLanguageModel model;


    public IGModel(IGModelType type, String identifier){
        this(type,identifier,"");
    }

    public IGModel(IGModelType type, String identifier, String systemAppend){
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.identifier = identifier;
        this.modelType = type;
        try {
            this.model = ChatLanguageModelBuilder.build(this.modelType);
            this.assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(this.model)
                    .chatMemory(chatMemory)
                    .systemMessageProvider(chatMemoryId -> type.parameters.systemPrompt + " " + systemAppend)
                    .build();
        } catch (ChatLanguageModelBuilder.ProviderUnavailableException e) {
            Logger.log(Level.SEVERE, "ERROR: Provider " + e.getProvider() + " unavailable." +
                    " Maybe check your api-key in the config ?");
        }

        activeModels.put(identifier,this);
    }


    /**
     * Handles all LLM's generating procedures.
     * An {@link IGModel} always consumes a {@link CommandSender} for chatting.
     * If you want to get answer's String, you can use {@link #answer}.
     * @param prompt The initial prompt
     * @param sender The commandSender (Player or Console) who triggered the action.
     */

    public void chat(String prompt, CommandSender sender){
        chat(prompt, sender, Range.GLOBAL);
    }

    public void chat(String prompt, CommandSender sender, Range range) {
        IGModel model = this;
        String user;

        if(Config.provideUsername) {
            if (sender instanceof Entity e) user = e.getName();
            else user = "Console Administrator";
            prompt = user + ": " + prompt;
        }

        String finalPrompt = prompt;

        Predicate<Player> rangeManager = player -> {
            if(range.type == GLOBAL) return true;
            if(!(sender instanceof Player p)) return false;
            else return(player.getLocation().distance(p.getLocation()) < range.range);
        };

        new BukkitRunnable() {
            @Override
            public void run() {
                Logger.log(Level.INFO, "Generating answer for prompt " + finalPrompt);
                String answer;
                if(!model.canUse(sender)){
                    answer = model.modelType.parameters.prefix + model.getDenyMessage();

                }
                else{
                    model.use(sender);
                    answer = model.modelType.parameters.prefix + model.assistant.chat(finalPrompt);
                }
                sender.sendMessage(answer);
                if(!model.isPrivate()) Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(p -> !p.equals(sender)) // Avoids to send the message 2x for the commandSender
                        .filter(rangeManager)
                        .forEach(p -> p.sendMessage(answer));
            }
        }.runTaskAsynchronously(LLM_craft.getInstance());

    }


    /**
     *
     * @return The generated answer by the IGModel.
     */
    public String answer(String prompt){
        IGModel model = this;
        final String[] answer = new String[1];
        new BukkitRunnable(){

            @Override
            public void run() {
                answer[0] =  model.modelType.parameters.prefix + model.assistant.chat(prompt);
            }
        }.runTaskAsynchronously(LLM_craft.getInstance());

        return answer[0];
    }

    public String getPrefix() {
        return this.modelType.parameters.prefix;
    }

    public static void createModel(IGModelType type, String identifier){
        activeModels.put(identifier, new IGModel(type,identifier));
    }

    public static boolean isModel(String s){
        return activeModels.containsKey(s);
    }

    public static Set<String> modelTypes(){
        return activeModels.keySet();
    }

    public static IGModel getModel(String identifier){
        return activeModels.get(identifier);
    }


    public boolean equals(IGModel model){
        return this.identifier.equals(model.identifier);
    }

    public boolean isPrivate(){
        return this.modelType.parameters.visibility.equals(IGModelParameters.Visibility.PRIVATE);
    }

    public boolean canUse(CommandSender sender){
        Limiter limiter = this.modelType.limits;
        if(limiter == null){
            return true;

        }
        return this.modelType.limits.canUse(sender);
    }

    public void use(CommandSender sender){
        Limiter limiter = this.modelType.limits;
        if(limiter != null) this.modelType.limits.use(sender);
    }

    public String getDenyMessage(){
        return this.modelType.limits.denyMessage;
    }

    public String toString(){
        return "Model " + this.identifier + " : type " + this.modelType.name;
    }


}
