package fr.yro.llmcraft.Model;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingResponseBuilder;
import fr.yro.llmcraft.Config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static fr.yro.llmcraft.Model.IGModelType.Provider.ANTHROPIC;
import static fr.yro.llmcraft.Model.IGModelType.Provider.OPENAI;

public class ChatLanguageModelBuilder {

    // TODO: use reflection to generify the 2 methods ?
    public static ChatLanguageModel build(IGModelType igModelType) throws ProviderUnavailableException {
        IGModelType.Provider provider = igModelType.parameters.provider;
        if(!Config.availableProviders.contains(provider)){
            throw new ProviderUnavailableException(provider);
        }

            return switch(provider){
            case OPENAI -> OpenAiChatModel.builder()
                        .apiKey(Config.openAI)
                        .modelName(igModelType.getModelName())
                        .temperature(igModelType.getTemperature())
                        .maxTokens(igModelType.getMaxTokens())
                        .frequencyPenalty(igModelType.getFrequencyPenalty())
                        .timeout(Duration.of(igModelType.getTO(), ChronoUnit.SECONDS))
                        .build();
            case ANTHROPIC -> AnthropicChatModel.builder()
                        .apiKey(Config.anthropicAPI)
                        .modelName(igModelType.getModelName())
                        .maxTokens(igModelType.getMaxTokens())
                        .temperature(igModelType.getTemperature())
                        .timeout(Duration.of(igModelType.getTO(), ChronoUnit.SECONDS))
                        .build();
        };
    }
    public static StreamingChatLanguageModel buildStream(IGModelType igModelType){


        return switch(igModelType.parameters.provider){
            case OPENAI -> OpenAiStreamingChatModel.builder()
                    .apiKey(Config.openAI)
                    .maxTokens(igModelType.getMaxTokens())
                    .modelName(igModelType.getModelName())
                    .temperature(igModelType.getTemperature())
                    .timeout(Duration.of(igModelType.getTO(), ChronoUnit.SECONDS))
                    .frequencyPenalty(igModelType.getFrequencyPenalty())
                    .build();
            case ANTHROPIC -> AnthropicStreamingChatModel.builder()
                    .apiKey(Config.anthropicAPI)
                    .maxTokens(igModelType.getMaxTokens())
                    .modelName(igModelType.getModelName())
                    .temperature(igModelType.getTemperature())
                    .timeout(Duration.of(igModelType.getTO(), ChronoUnit.SECONDS))
                    .build();
        };
    }

    public static class ProviderUnavailableException extends Exception {
        IGModelType.Provider provider;
        public ProviderUnavailableException(IGModelType.Provider provider) {
            this.provider = provider;
        }
        public IGModelType.Provider getProvider() {
            return this.provider;
        }
    }

}
