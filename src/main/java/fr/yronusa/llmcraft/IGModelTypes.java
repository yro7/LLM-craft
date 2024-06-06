package fr.yronusa.llmcraft;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * {@link IGModelTypes} is the object that represents one type of model.
 * Each {@link IGModelTypes} can then be instancied in a {@link IGModel}.
 * Having those two separate objects allows you to have multiple instances of the same "model type"
 */
public class IGModelTypes {
    static ConfigurationSection configSection;
    public static List<IGModelTypes> models;

    public static enum PROVIDER {
        OPENAI,
        ANTHROPIC
    }

    public String name;
    public String prefix;
    public boolean persistent;
    ChatLanguageModel model;

    public IGModelTypes(String name, String prefix, boolean persistent) {
        this.name = name;
        this.prefix = prefix;
        this.persistent = persistent;
    }

    public IGModelTypes(String s) {

        this.name = s;
        this.prefix = configSection.getString(s+".prefix");
        this.persistent = configSection.getBoolean(s + ".persistent");
        PROVIDER provider = PROVIDER.valueOf(configSection.getString(s+".provider"));


    }

    public static List<IGModelTypes> getIGModelsFromConfig(String path) {
        configSection = Config.config.getConfigurationSection("models");
        List<IGModelTypes> res = new ArrayList<>();
        if(configSection == null){
            System.out.println("§c[LLM-Craft] You need to define some models in your config.");
        }
        Set<String> modelsPath = configSection.getKeys(false);

        for (String s : modelsPath) {
            res.add(new IGModelTypes(s));
        }
        return res;
    }

    public static void initialize(){




    }


}
