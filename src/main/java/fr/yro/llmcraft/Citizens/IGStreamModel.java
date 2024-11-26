package fr.yro.llmcraft.Citizens;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import fr.yro.llmcraft.Config;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import org.bukkit.command.CommandSender;

import java.util.List;

public class IGStreamModel extends IGModel  {

    interface Assistant {

        TokenStream chat(String message);
    }

    public IGStreamModel(IGModelType type, String identifier, String systemAppend){
        super(type, identifier, systemAppend);

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.identifier = identifier;
        this.modelType = type;

        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .apiKey(Config.openAI)
                .modelName("GPT_4_O_MINI")
                .build();

        Assistant assistant = AiServices.create(Assistant.class, model);

        TokenStream tokenStream = assistant.chat("Tell me a joke");

        tokenStream.onNext(System.out::println)

                .onRetrieved((List<Content> contents) -> System.out.println(contents))
                .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))
                .onComplete((Response<AiMessage> response) -> System.out.println(response))
                .onError((Throwable error) -> error.printStackTrace())
                .start();
    }

    @Override
    public void chat(String prompt, CommandSender sender){
        chat(prompt, sender, Range.GLOBAL);
    }


}
