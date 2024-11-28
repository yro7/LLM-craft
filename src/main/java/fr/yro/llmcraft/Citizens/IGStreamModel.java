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
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


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

        System.out.println("building new ig stream model : ");
        System.out.println("system prompt : " + type.parameters.systemPrompt);
        System.out.println("system append : " + systemAppend);
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(type.model)
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
                    StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                            .apiKey(Config.openAI)
                            .modelName("gpt-3.5-turbo")
                            .build();
                    Assistant assistant = AiServices.create(Assistant.class, model);
                    TokenStream tokenStream = assistant.chat(prompt);
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
            System.out.println("lines is empty, adding one with first token :");
            DHAPI.addHologramLine(page, token);
        }

        HologramLine line = page.getLines().getLast();
        String lineContent = line.getContent();
        System.out.println("line content: " + lineContent);
        String newContent = lineContent + token;
        if(newContent.length() > 50){
            System.out.println("added a line bcs to long");
            DHAPI.addHologramLine(this.hologram, token);
        } else{
            DHAPI.setHologramLine(line, newContent);
        }
        System.out.println("last line content after adding token: " + lineContent);
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
