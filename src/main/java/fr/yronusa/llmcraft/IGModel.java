package fr.yronusa.llmcraft;

import dev.langchain4j.service.AiServices;

import java.util.List;

/**
 * Represents an instance of a {@link IGModelTypes}.
 * Using instances allows you to have similar models but with a different context.
 */
public class IGModel implements IGModelInterface {

    public static List<IGModel> activeModels;

    public IGModelTypes modelType;
    public String identifier;

    public IGModel(IGModelTypes type, String identifier){
        this.identifier = identifier;
        this.modelType = type;

        IGModel friend = AiServices.builder(IGModelInterface.class)
                .chatLanguageModel(type.model)
                .systemMessageProvider(chatMemoryId -> "You are a good friend of mine. Answer using slang.")
                .build();
    }


    @Override
    public String chat(String userMessage) {
        return "";
    }
}
