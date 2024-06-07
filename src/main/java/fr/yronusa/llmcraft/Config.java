package fr.yronusa.llmcraft;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Config {

    public static FileConfiguration config;
    public static String openAI;

    public static List<IGModelType.Provider> availableProviders;
    public static boolean provideUsername = false;


    public static void load() {
        availableProviders = new ArrayList<>();
        config = LLM_craft.getInstance().getConfig();
        openAI = config.getString("openAI");

        if(openAI != null && !openAI.equals("null")){
            try{
                OpenAiChatModel model = OpenAiChatModel.withApiKey(openAI);
                availableProviders.add(IGModelType.Provider.OPENAI);
            } catch(Exception e){
                Logger.log(Level.WARNING,"§c Your OpenAI API-key seems unvalid ! Please check the config.");
            }
        }

    }

}