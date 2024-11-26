package fr.yro.llmcraft.Citizens;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.LLM_craft;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

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

        System.out.print("created new ig stream model with hologram " + this.hologram.getName());
        printHolo(this.hologram);

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
        System.out.print("using stremachat with hologram ::" + this.hologram.getName());
        printHolo(this.hologram);
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
                    System.out.print("debug : this hologram =");
                    printHolo(thisModel.hologram);
                    // Clear the previous message
                    thisModel.clearHologram();

                    // Configure stream handling
                    tokenStream
                            .onNext(token -> {
                                try {
                                    // Wait a bit, so that the response flow looks more natural
                                    Thread.sleep(getTimeToWait(token));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                // Ensure thread-safe sending of messages to Bukkit
                                Bukkit.getScheduler().runTask(LLM_craft.getInstance(), () -> {
                                    thisModel.updateHologram(token);
                                });
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
        this.hologram.getPage(0).getLine(0).setContent(" ");
    }

    public void updateHologram(String token) {
        Hologram hologram = this.hologram;
        String actualContent = hologram.getPage(0).getLine(0).getContent();
        String newContent = actualContent + token;

        HologramPage newPage = justifyText(newContent, hologram);
        hologram.getPages().set(0, newPage);
        System.out.println("printing hologram:::");

        printHolo(hologram);
    }

    // Change the delay between 2 tokens in function of the token.
    public int getTimeToWait(String token){
        return (int) (Config.hologramSpeed*switch(token){
                    case ".", ";", ",", "?", "!" -> 100;
                    default -> 15;
                });
    }

    /**
     * Justify text for a hologram, ensuring no words are cut off and each line is max 50 characters
     *
     * @param text The input text to be justified
     * @param hologram The hologram to add lines to
     * @return A hologram page with justified text
     */
    public static HologramPage justifyText(String text, Hologram hologram) {
        // Create a new page for the hologram
        HologramPage newPage = new HologramPage(hologram, 0);

        int MAX_LINE_LENGTH = 50;
        // Split the text into words
        String[] words = text.split("\\s+");

        // Current line being built
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            // If the word itself is longer than MAX_LINE_LENGTH, it needs special handling
            if (word.length() > MAX_LINE_LENGTH) {
                // If there's something in the current line, add it first
                if (!currentLine.isEmpty()) {
                    newPage.addLine(new HologramLine(newPage, hologram.getLocation(), currentLine.toString().trim()));
                    currentLine = new StringBuilder();
                }

                // Add long words character by character, breaking at MAX_LINE_LENGTH
                for (int i = 0; i < word.length(); i += MAX_LINE_LENGTH) {
                    int end = Math.min(i + MAX_LINE_LENGTH, word.length());
                    newPage.addLine(new HologramLine(newPage, hologram.getLocation(), word.substring(i, end)));
                }

                continue;
            }

            // Check if adding this word would exceed MAX_LINE_LENGTH
            if (currentLine.length() + word.length() + 1 > MAX_LINE_LENGTH) {
                // Add the current line and start a new one
                newPage.addLine(new HologramLine(newPage, hologram.getLocation(), currentLine.toString().trim()));
                currentLine = new StringBuilder();
            }

            // Add word to current line
            if (!currentLine.isEmpty()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        // Add the last line if not empty
        if (!currentLine.isEmpty()) {
            newPage.addLine(new HologramLine(newPage, hologram.getLocation(), currentLine.toString().trim()));
        }

        return newPage;
    }
}
