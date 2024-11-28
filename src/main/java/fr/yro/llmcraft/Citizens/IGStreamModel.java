package fr.yro.llmcraft.Citizens;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.LLM_craft;
import fr.yro.llmcraft.Model.ChatLanguageModelBuilder;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


public class IGStreamModel extends IGModel  {


    interface Assistant {
        TokenStream chat(String message);
    }

    public Assistant assistant;
    public Hologram hologram;
    public StreamingChatLanguageModel model;



    public IGStreamModel(IGModelType type, String identifier, String systemAppend, Hologram hologram){
        super(type, identifier, systemAppend);
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.hologram = hologram;
        this.identifier = identifier;
        this.modelType = type;
        this.model = ChatLanguageModelBuilder.buildStream(this.modelType);
        this.assistant = AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(this.model)
                .chatMemory(chatMemory)
                .systemMessageProvider(chatMemoryId -> type.parameters.systemPrompt + " " + systemAppend)
                .build();


        activeModels.put(identifier,this);
    }

    @Override
    public void chat(String prompt, CommandSender sender){
        streamChat(prompt, sender);
    }

    public void streamChat(String prompt, CommandSender sender) {
        IGStreamModel thisModel = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                    // Start the token stream
                    TokenStream tokenStream = thisModel.assistant.chat(prompt);
                    // Clear the previous message
                    thisModel.clearHologram();
                    // Configure stream handling
                    tokenStream
                            .onNext(token -> {
                                // Ensure thread-safe sending of messages to Bukkit
                                Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                                    thisModel.updateHologram(token);
                                });

                                try {
                                    // Wait a bit, so that the response flow looks more natural
                                    Thread.sleep(getTimeToWait(token));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                            })
                            .onError(error -> {
                                // Error handling
                                Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                                    sender.sendMessage("Error occurred: " + error.getMessage());
                                });
                            })
                            .start();
                };
        }.runTaskAsynchronously(LLM_craft.getInstance());
    }

    public void clearHologram() {
        this.hologram.removePage(0);
        this.hologram.addPage();
        this.hologram.getPage(0).setLine(1, "");
    }

    // TODO : every x lines, move the hologram up so that lines dont get into the ground/npc.
    public void updateHologram(String token) {

        HologramPage page = this.hologram.getPage(0);
        List<HologramLine> lines = page.getLines();

        if (lines.isEmpty()) {
            DHAPI.addHologramLine(page, token);
        }

        HologramLine line = page.getLines().getLast();
        String lineContent = line.getContent();
        String newContent = lineContent + token;
        if(newContent.length() > 50){
            DHAPI.addHologramLine(this.hologram, token);
        } else{
            DHAPI.setHologramLine(line, newContent);
        }
    }


    // Change the delay between 2 tokens in function of the token.
    public int getTimeToWait(String token){
        return (int) (Config.hologramSpeed*switch(token){
                    case ".", "?", "!" -> 850;
                    case ";", "," -> 700;
                    default -> 100;
                });
    }
}
