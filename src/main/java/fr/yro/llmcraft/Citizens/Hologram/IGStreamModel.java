package fr.yro.llmcraft.Citizens.Hologram;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.LLM_craft;
import fr.yro.llmcraft.Logger;
import fr.yro.llmcraft.Model.ChatLanguageModelBuilder;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import kotlin.text.Regex;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;


public class IGStreamModel extends IGModel  {


    interface Assistant {
        TokenStream chat(String message);
    }

    public Assistant assistant;
    public Hologram hologram;
    public StreamingChatLanguageModel model;
    public String color;
    public final AtomicReference<TokenStream> lastTokenStream = new AtomicReference<>(null);
    public double baseY;




    public IGStreamModel(IGModelType type, String identifier, String systemAppend, Hologram hologram, String color, double y){
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
        this.color = color;
        this.baseY = y;
        activeModels.put(identifier,this);
    }

    @Override
    public void chat(String prompt, CommandSender sender) {

        IGStreamModel thisModel = this;
        String answer = "";

        if(!this.canUse(sender)){
            answer = this.modelType.parameters.prefix + this.getDenyMessage();
            sender.sendMessage(answer);
            return;

        }

        else{
            this.use(sender);
        }

        if(Config.provideUsername) {
            if (sender instanceof Entity e) prompt = e.getName() + ": " + prompt;
            else prompt =  "Console : " + prompt;
        }


        String finalPrompt = prompt;
        new BukkitRunnable() {
            @Override
            public void run() {
                    // Start the token stream
                    TokenStream tokenStream = thisModel.assistant.chat(finalPrompt);
                    thisModel.lastTokenStream.set(tokenStream);
                    // Clear the previous message
                    thisModel.clearHologram();
                    // Configure stream handling
                    tokenStream
                            .onNext(token -> {
                                // Ensure thread-safe sending of messages to Bukkit
                                Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                                    // If the token stream is not the last one, then do nothing
                                    // (it would concur the last token stream and make a mess)
                                    if(lastTokenStream.get() != tokenStream) return;
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
                                    Logger.log(Level.SEVERE, "Error occured when completing a token stream : " + error.getMessage());
                                });
                            })
                            .start();
                };
        }.runTaskAsynchronously(LLM_craft.getInstance());
    }

    /** Clear the hologram and put it at base location (see {@link #moveHologramIfNeeded}).
     */
    public void clearHologram() {
        this.hologram.removePage(0);
        this.hologram.addPage();
        this.hologram.getPage(0).setLine(1, "");
        Location loc = this.hologram.getLocation();
        loc.setY(baseY);
    }

    public void updateHologram(String token) {
        // Replace non-printable characters by a space
        token = token.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", " ");
        HologramPage page = this.hologram.getPage(0);
        List<HologramLine> lines = page.getLines();
        if (lines.isEmpty()) {
            DHAPI.addHologramLine(page, this.color + token);
        }

        HologramLine line = page.getLines().getLast();
        String lineContent = line.getContent();
        String newContent = lineContent + token;
        if(newContent.length() > 50){
            moveHologramIfNeeded(this.hologram);
            DHAPI.addHologramLine(this.hologram, this.color + token);
        } else{
            DHAPI.setHologramLine(line, newContent);
        }

    }

    private void moveHologramIfNeeded(Hologram hologram) {
        List<HologramLine> lines = hologram.getPage(0).getLines();
        // 4 lines is the distance between the top of the hologram and NPC's name
        // (roughly 1.2 blocks)
        if(lines.size() > 1 && lines.size() % 4 == 0){
            Location loc = this.hologram.getLocation();
            loc.setY(loc.getY()+1.2);
        }
    }




    // Change the delay between 2 tokens in function of the token.
    public int getTimeToWait(String token){
        return (int) (Config.hologramSpeed*switch(token){
                    case ".", "?", "!" -> 850;
                    case ";", "," -> 600;
                    default -> 90;
                });
    }

}
