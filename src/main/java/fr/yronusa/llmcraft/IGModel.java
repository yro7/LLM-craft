package fr.yronusa.llmcraft;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.bukkit.Bukkit;

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



    public String chat(String s){
        return this.assistant.chat(s);
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






}
