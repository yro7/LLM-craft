package fr.yronusa.llmcraft;

import java.util.Objects;

public class IGModelParameters {

    public IGModelTypes.Provider provider;
    public String systemPrompt;
    public boolean persistent;
    public String prefix;
    public String modelName;
    public double temperature;
    public int maxTokens;
    public double frequencyPenalty;
    public int timeOut;

    public IGModelParameters(IGModelTypes.Provider provider, String systemPrompt, boolean persistent,
                             String prefix, String modelName, double temperature, int max_tokens,
                             double frequency_penalty, int timeOut) {

        switch(provider){
            case OPENAI:
                this.systemPrompt = Objects.requireNonNullElse(systemPrompt, "");
                this.persistent = persistent;
                this.prefix = Objects.requireNonNullElse(prefix, "");
                this.modelName = Objects.requireNonNullElse(modelName, "gpt-3.5-turbo");
                this.temperature = Objects.requireNonNullElse(temperature, 0.7);
                this.maxTokens = Objects.requireNonNullElse(max_tokens, 1000);
                this.frequencyPenalty = Objects.requireNonNullElse(frequency_penalty, Double.valueOf(0));
                this.timeOut = Objects.requireNonNullElse(timeOut, 60);
            case ANTHROPIC:

        }
    }

}
