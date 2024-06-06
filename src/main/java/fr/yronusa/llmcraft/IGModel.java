package fr.yronusa.llmcraft;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.util.List;

/**
 * Represents an instance of a {@link IGModelTypes}.
 * Using instances allows you to have similar models but with a different context.
 */
public class IGModel {

    interface Assistant {

        @SystemMessage(String message);
        String chat(String message);
    }

    public static List<IGModel> activeModels;

    public IGModelTypes modelType;
    public Assistant assistant;
    public String identifier;

    public IGModel instance(IGModelTypes type, String identifier){

        IGModel instance = AiServices.builder(IGModel.class)
                .chatLanguageModel(this.modelType.model)
                .systemMessageProvider(chatMemoryId -> this.modelType.parameters.systemPrompt)
                .build();

        instance.identifier = identifier;
        instance.modelType = type;

        activeModels.add(instance);
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        SystemMessage systemMessage = SystemMessage.from(this.modelType.parameters.systemPrompt);
        chatMemory.add(systemMessage);

        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(this.modelType.model)
                .chatMemory(chatMemory)
                .build();

        String answer = assistant.chat("Hello! My name is Klaus.");
        System.out.println(answer); // Hello Klaus! How can I assist you today?

        String answerWithName = assistant.chat("What is my name?");
        System.out.println(answerWithName); // Your name is Klaus.

        return instance;



    }

}
