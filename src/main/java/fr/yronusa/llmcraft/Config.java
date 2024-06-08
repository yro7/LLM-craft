package fr.yronusa.llmcraft;

import dev.langchain4j.model.openai.OpenAiChatModel;
import fr.yronusa.llmcraft.Model.IGModelType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

    public static FileConfiguration config;
    public static String openAI;

    public static List<IGModelType.Provider> availableProviders;
    public static boolean provideUsername = true;


    public static void load() {
        availableProviders = new ArrayList<>();
        config = LLM_craft.getInstance().getConfig();
        openAI = config.getString("openAI");

        System.out.println("§c"+openAI);

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