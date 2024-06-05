package fr.yronusa.llmcraft;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ModelManager {

    public static void test2(Player p){
        // Create an instance of a model
      //  ChatLanguageModel model = OpenAiChatModel.withApiKey(Config.openAI);


        new BukkitRunnable() {
            @Override
            public void run() {
                ChatLanguageModel model = OpenAiChatModel.withApiKey("sk-proj-unH9pwyH3C7zrwXDncrST3BlbkFJR4UM7nNJYyO0lWTHuZPE");
                String answer = model.generate("Hello world!");
                System.out.println(p + "generated this answer:" + answer); // Hello! How can I assist you today?
                p.sendMessage(answer);
            }
        }.runTaskAsynchronously(LLM_craft.getInstance());
        // Start interacting

    }

}
