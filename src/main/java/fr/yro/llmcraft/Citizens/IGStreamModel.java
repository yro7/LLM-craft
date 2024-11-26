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

import java.util.concurrent.CompletableFuture;

import static fr.yro.llmcraft.Citizens.HologramTalkingCitizen.printHolo;

public class IGStreamModel extends IGModel  {


    public Assistant assistant;
    public Hologram hologram;
    interface Assistant {

        TokenStream chat(String message);
    }

    public IGStreamModel(IGModelType type, String identifier, String systemAppend, Hologram hologram){
        super(type, identifier, systemAppend);
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.hologram = hologram;
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
        // TODO : replace with Bukkit Async scheduler!
        CompletableFuture.runAsync(() -> {
            // Start the token stream
            StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                    .apiKey(Config.openAI)
                    .modelName("gpt-3.5-turbo")
                    .build();

            Assistant assistant = AiServices.create(Assistant.class, model);
            TokenStream tokenStream = assistant.chat(prompt);
            System.out.print("debug : this hologram =" + this.hologram);

            // Clear the previous message
            this.clearHologram();

            // Configure stream handling
            tokenStream
                    .onNext(token -> {
                        try {
                            // Wait a bit, so that the response looks more like natural speak
                            // TODO implement a "int getTimeToSleep()" function to have pauses in the generation,
                            //  when the model puts a "," or a "." for example.
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        // Ensure thread-safe sending of messages to Bukkit
                        Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                            this.updateHologram(token);
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

    private void clearHologram() {
        this.hologram.getPage(0).getLine(0).setContent("");
    }

    private void updateHologram(String token) {
        Hologram hologram = this.hologram;
        printHolo(hologram);
        String actual = hologram.getPage(0).getLine(0).getContent();
        System.out.print("debug update hologram : ");
        System.out.print("actual : " + actual + "    token : " + token);

        hologram.getPage(0).setLine(0,actual + token);

    }

}
