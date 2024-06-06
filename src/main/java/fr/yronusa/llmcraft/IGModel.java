package fr.yronusa.llmcraft;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Represents an instance of a {@link IGModelTypes}.
 * Using instances allows you to have similar models but with a different context.
 */

public class IGModel {



    public interface Assistant {
        String chat(String message);
    }

    public static HashMap<String,IGModel> activeModels;

    public IGModelTypes modelType;
    public String identifier;
    public Assistant assistant;

    public IGModel(IGModelTypes type, String identifier){

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.identifier = identifier;
        this.modelType = type;

        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(type.model)
                .chatMemory(chatMemory)
                .systemMessageProvider(chatMemoryId -> type.parameters.systemPrompt)
                .build();

        System.out.println(this.assistant.chat("hello, how are you ?"));
    }


    /**
     *
     * @param prompt The initial prompt
     * @param sender The commandSender (Player or Console) who triggered the action.
     * An {@link IGModel} always takes a "triggering user" for chatting.
     * If you want to force generating an answer, you can use {@link #forceChat}.
     */
    public void chat(String prompt, CommandSender sender){
        IGModel model = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                String answer = model.modelType.parameters.prefix + model.assistant.chat(prompt);
                sender.sendMessage(answer);
                if(!model.isPrivate()) Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(answer));
            }
        }.runTaskAsynchronously(LLM_craft.getInstance());
    }

    /**
     *
     * @return The generated answer by the IGModel.
     */
    public String forceChat(String prompt){
        return this.modelType.parameters.prefix + this.assistant.chat(prompt);
    }

    public String getPrefix() {
        return this.modelType.parameters.prefix;
    }

    public static void createModel(IGModelTypes type, String identifier){
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


}
