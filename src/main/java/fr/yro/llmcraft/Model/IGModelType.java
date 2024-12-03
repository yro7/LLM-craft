package fr.yro.llmcraft.Model;

import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Optional;
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
    public Limiter limits;

    public IGModelType(String s)  {
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
        String visibilityString = configSection.getString(s+".visibility").toUpperCase();

        Optional<Limiter> limits = Optional.ofNullable(Limiter.limiters.get(s));
        limits.ifPresentOrElse(limit -> this.limits = limit, () ->
                Logger.log(Level.WARNING, "No usage limiter found for model " + s + ", players could abuse the API."));
        IGModelParameters.Visibility visibility = visibilityFromString(visibilityString);

        this.parameters = new IGModelParameters(provider, systemPrompt, persistent, prefix,
                modelName, temperature, max_tokens, frequencyPenalty, timeOut, visibility);

    }

    private IGModelParameters.Visibility visibilityFromString(String visibilityString) {
        return switch(visibilityString){
            case "SHARED", "PUBLIC" -> IGModelParameters.Visibility.SHARED;
            default -> IGModelParameters.Visibility.PERSONAL;
        };
    }

    Double getTemperature() {
        return this.parameters.temperature;
    }

    long getTO() {
        return this.parameters.timeOut;
    }

    String getModelName() {
        return this.parameters.modelName;
    }

    public static HashMap<String, IGModelType> getIGModelsFromConfig() {
        HashMap<String, IGModelType> res = new HashMap<>();
        if(configSection == null){
            Logger.log(Level.SEVERE, "You need to define models types in your config.yml!");
        }
        Set<String> modelsPath = configSection.getKeys(false);

        for(String modelPath : modelsPath){
            IGModelType modelType = new IGModelType(modelPath);
            res.put(modelPath, modelType);
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

    public String toString(){
        return "ModelType \"" + this.name + "\" : \n" +
                "Provider: " + this.parameters.provider + "\n" +
                "Model name: " + this.parameters.modelName + "\n" +
                "Visibility: " + this.parameters.visibility + "\n" +
                "System prompt: " + this.parameters.systemPrompt + "\n" +
                "is persistent (chat memory): " + this.parameters.persistent + "\n" +
                "Prefix: " + this.parameters.prefix + "\n" +
                "Temperature: " + this.parameters.temperature + "\n" +
                "maxTokens: " + this.parameters.maxTokens + "\n";
    }



}

