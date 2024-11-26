package fr.yro.llmcraft.Citizens;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.LLM_craft;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IGStreamModel extends IGModel  {


    public Assistant assistant;
    public Hologram hologram;
    interface Assistant {

        TokenStream chat(String message);
    }

    public IGStreamModel(IGModelType type, String identifier, String systemAppend){
        super(type, identifier, systemAppend);

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.identifier = identifier;
        this.modelType = type;

        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .apiKey(Config.openAI)
                .modelName("gpt-3.5-turbo")
                .build();

        this.assistant = AiServices.create(Assistant.class, model);
    }

    @Override
    public void chat(String prompt, CommandSender sender){
        streamChat(prompt, sender);
    }

    public void streamChat(String prompt, CommandSender sender) {
        CompletableFuture.runAsync(() -> {
            // Start the token stream
            StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                    .apiKey(Config.openAI)
                    .modelName("gpt-3.5-turbo")
                    .build();

            Assistant assistant = AiServices.create(Assistant.class, model);
            TokenStream tokenStream = assistant.chat(prompt);

            // Configure stream handling
            tokenStream
                    .onNext(token -> {
                        // Ensure thread-safe sending of messages to Bukkit
                        Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                            sender.sendMessage(token);
                        });
                    })
                    .onError(error -> {
                        // Error handling
                        Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                            sender.sendMessage("Error occurred: " + error.getMessage());
                        });
                    })
                    .start();
        });
    }

}
