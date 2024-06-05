package fr.yronusa.llmcraft;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static FileConfiguration config;
    public static String apiKey = "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";

    public static String getApiKey(){
        return apiKey;
    }

    public static void load() {
        config = LLM_craft.getInstance().getConfig();
        apiKey = config.getString("api-key");
    }
}