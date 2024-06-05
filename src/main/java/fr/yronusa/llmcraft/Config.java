package fr.yronusa.llmcraft;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static FileConfiguration config;
    public static String openAI;

    public static void load() {
        config = LLM_craft.getInstance().getConfig();
        openAI = config.getString("openAI");
    }
}