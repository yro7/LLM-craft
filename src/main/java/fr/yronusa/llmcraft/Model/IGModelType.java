package fr.yronusa.llmcraft.Model;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import fr.yronusa.llmcraft.Config;
import fr.yronusa.llmcraft.Logger;
import fr.yronusa.llmcraft.ProviderUnavailableException;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

/**
 * {@link IGModelType} is the object that represents one type of model.
 * Each {@link IGModelType} can then be instanced in a {@link IGModel}.
 * Having those two separate objects allows you to have multiple instances of the same "model type"
 */
public class IGModelType {

    public static ConfigurationSection configSection;
    public static HashMap<String, IGModelType> modelsTypes;



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


    public IGModelType(String s) throws ProviderUnavailableException {

        this.name = s;
        Logger.log(Level.CONFIG, "Initializing new ModelType " + s);
        Provider provider = Provider.valueOf(configSection.getString(s+".provider").toUpperCase());
        String systemPrompt = configSection.getString(s+".system-prompt");
        boolean persistent = configSection.getBoolean(s + ".persistent");
        String prefix = configSection.getString(s+".prefix");
        String modelName = configSection.getString(s+".model-name");
        double temperature = configSection.getDouble(s+".temperature");
        int max_tokens = configSection.getInt(s+".max-tokens");
        double frequencyPenalty = configSection.getDouble(s+".frequency-penalty");
        int timeOut = configSection.getInt(s+".time-out");
        String visibilityString = configSection.getString(s+".visibility");

        IGModelParameters.Visibility visibility;
        try{
          visibility = IGModelParameters.Visibility.valueOf(visibilityString.toUpperCase());
        } catch(Exception e){
            visibility = IGModelParameters.Visibility.PRIVATE;
        }

        this.parameters = new IGModelParameters(provider, systemPrompt, persistent, prefix,
                modelName, temperature, max_tokens, frequencyPenalty, timeOut, visibility);

        if(!Config.availableProviders.contains(provider)){
            throw new ProviderUnavailableException(provider);
        }

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

    public static HashMap<String, IGModelType> getIGModelsFromConfig() {
        configSection = Config.config.getConfigurationSection("models");
        HashMap<String, IGModelType> res = new HashMap<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define models types in your config.yml!");
        }
        Set<String> modelsPath = configSection.getKeys(false);

        for(String modelPath : modelsPath){

            try {
                IGModelType modelType = new IGModelType(modelPath);
                res.put(modelPath, modelType);

            } catch (ProviderUnavailableException e) {
                Logger.log(Level.SEVERE, "ERROR: Provider " + e.getProvider() + " unavailable." +
                        " Maybe check your api-key in the config ?");
            }
        }
        return res;
    }

    public static void initialize(){
        IGModelType.modelsTypes = new HashMap<>();
        IGModelType.configSection = Config.config.getConfigurationSection("models");
        IGModelType.modelsTypes = IGModelType.getIGModelsFromConfig();
        modelsTypes.values().forEach(mt -> IGModel.createModel(mt,(mt.name+"1")));
    }

    public static boolean isModelType(String s){
        return modelsTypes.containsKey(s);
    }

    public static Set<String> modelTypes(){
        return modelsTypes.keySet();
    }


}

