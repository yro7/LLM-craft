package fr.yronusa.llmcraft;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Set;

/**
 * {@link IGModelTypes} is the object that represents one type of model.
 * Each {@link IGModelTypes} can then be instancied in a {@link IGModel}.
 * Having those two separate objects allows you to have multiple instances of the same "model type"
 */
public class IGModelTypes {
    public static ConfigurationSection configSection;
    public static HashMap<String,IGModelTypes> modelsTypes;

    public enum Provider {
        OPENAI,
        ANTHROPIC
    }

    public String name;
    /**
     * Is used to save model's parameters. Is "embedded" into ChatLanguageModel, but having it as a
     * separate property allows to retrieve later the parameters if needed.
     */
    public IGModelParameters parameters;
    ChatLanguageModel model;


    public IGModelTypes(String s) {

        this.name = s;

        Provider provider = Provider.valueOf(configSection.getString(s+".prefix"));
        String systemPrompt = configSection.getString(s+".system-prompt");
        boolean persistent = configSection.getBoolean(s + ".persistent");
        String prefix = configSection.getString(s+".prefix");
        String modelName = configSection.getString(s+".model-name");
        double temperature = configSection.getDouble(s+".temperature");
        int max_tokens = configSection.getInt(s+".max-tokens");
        double frequencyPenalty = configSection.getDouble(s+".frequency-penalty");
        int timeOut = configSection.getInt(s+".time-out");

        this.parameters = new IGModelParameters(provider, systemPrompt, persistent, prefix,
                modelName, temperature, max_tokens, frequencyPenalty, timeOut);

        switch(provider){
            case OPENAI:
                this.model = OpenAiChatModel.builder()
                        .apiKey(Config.openAI)
                        .modelName(this.getModelName())
                        .temperature(this.getTemperature())
                        .timeout(Duration.of(this.getTO(), ChronoUnit.SECONDS))
                        .build();
        }

    }

    private Double getTemperature() {
        return this.parameters.temperature;
    }

    private long getTO() {
        return this.parameters.timeOut;
    }

    private String getModelName() {
        return this.parameters.modelName;
    }

    public static HashMap<String,IGModelTypes> getIGModelsFromConfig(String path) {
        configSection = Config.config.getConfigurationSection("models");
        HashMap<String,IGModelTypes> res = new HashMap<String,IGModelTypes>();
        if(configSection == null){
            System.out.println("§c[LLM-Craft] You need to define some models in your config.");
        }
        Set<String> modelsPath = configSection.getKeys(false);

        for (String s : modelsPath) {
            res.put(s,new IGModelTypes(s));
        }
        return res;
    }

    public static void initialize(){

    }

    public static boolean isModelType(String s){
        return modelsTypes.containsKey(s);
    }

    public static Set<String> modelTypes(){
        return modelsTypes.keySet();
    }


}
