package fr.yronusa.llmcraft;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ModelManager {


    public static void execute(IGModelTypes.MODEL modelType, CommandSender commandSender, String prompt){
        new BukkitRunnable() {
            @Override
            public void run() {
                ChatLanguageModel model = OpenAiChatModel.withApiKey(Config.openAI);
                String answer;

                switch(modelType){
                    case OPENAI:
                        answer = model.generate(prompt);
                        commandSender.sendMessage("§6§lChatGPT > §e" + answer);
                        break;
                    case TROLL:
                        String systemPrompt = "Tu es une IA Minecraft maléfique ! Moque toi " +
                                "(gentillement) de ce que  vient de dire le joueur:";
                        answer = model.generate(systemPrompt+prompt);
                        commandSender.sendMessage("§6§lDevilGPT > §e" + answer);
                }
            }
        }.runTaskAsynchronously(LLM_craft.getInstance());
    }

}
