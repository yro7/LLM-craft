package fr.yro.llmcraft;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

    public static FileConfiguration config;
    public static String openAI;
    public static String anthropicAPI;

    public static List<IGModelType.Provider> availableProviders;
    public static boolean provideUsername = true;


    public static void load() {
        availableProviders = new ArrayList<>();
        LLM_craft.getInstance().reloadConfig();
        config = LLM_craft.getInstance().getConfig();

        openAI = config.getString("openAI");
        anthropicAPI = config.getString("anthropic");

        verifyProviders();
    }

    public static void verifyProviders(){
        if(openAI != null && !openAI.equals("your key here")){
            try{
                OpenAiChatModel.withApiKey(openAI);
                availableProviders.add(IGModelType.Provider.OPENAI);
            } catch(Exception e){
                Logger.log(Level.WARNING,"§c Your OpenAI API-key seems unvalid ! Please check config.yml.");
            }
        }

        if(anthropicAPI != null && !anthropicAPI.equals("your key here")){
            try{
                AnthropicChatModel.withApiKey(openAI);
                availableProviders.add(IGModelType.Provider.OPENAI);
            } catch(Exception e){
                Logger.log(Level.WARNING,"§c Your Anthropic API-key seems unvalid ! Please check config.yml.");
            }
        }

    }

}