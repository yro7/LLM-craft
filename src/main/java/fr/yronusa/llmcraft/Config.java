package fr.yronusa.llmcraft;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static FileConfiguration config;
    public static String openAI;

    public static List<Model.MODEL> availablesModels;



    public static void load() {
        availablesModels = new ArrayList<>();
        config = LLM_craft.getInstance().getConfig();
        openAI = config.getString("openAI");
        if(openAI != null && !openAI.equals("null")) availablesModels.add(Model.MODEL.OPENAI);
        if(openAI != null && !openAI.equals("null")) availablesModels.add(Model.MODEL.TROLL);
    }
}