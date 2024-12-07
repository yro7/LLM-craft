package fr.yro.llmcraft.Model;

import fr.yro.llmcraft.Logger;

import java.util.Objects;
import java.util.logging.Level;

public class IGModelParameters {

    public enum Visibility {
        PERSONAL,
        SHARED
    }

    public IGModelType.Provider provider;
    public String systemPrompt;
    public boolean persistent;
    public String prefix;
    public String modelName;
    public double temperature;
    public int maxTokens;
    public double frequencyPenalty;
    public int timeOut;
    public Visibility visibility;

    public IGModelParameters(IGModelType.Provider provider, String systemPrompt, boolean persistent,
                             String prefix, String modelName, double temperature, int max_tokens,
                             double frequency_penalty, int timeOut, Visibility visibility) {
        this.systemPrompt = Objects.requireNonNullElse(systemPrompt, "");
        this.persistent = persistent;
        this.prefix = Objects.requireNonNullElse(prefix, "");
        this.modelName = Objects.requireNonNullElse(modelName, "gpt-3.5-turbo");
        this.temperature = temperature;
        this.maxTokens = max_tokens;
        this.frequencyPenalty = frequency_penalty;
        this.timeOut = timeOut;
        this.visibility = Objects.requireNonNullElse(visibility, Visibility.PERSONAL);
        this.provider = provider;

        if(this.frequencyPenalty != 0 && provider != IGModelType.Provider.OPENAI){
            Logger.log(Level.WARNING, "ModelType + " + modelName + " : Frequency Penalty option is only available for OpenAI models, the option will be ignored.");
        }
    }

}
