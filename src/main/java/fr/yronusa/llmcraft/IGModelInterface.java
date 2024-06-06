package fr.yronusa.llmcraft;

import dev.langchain4j.service.SystemMessage;

public interface IGModelInterface {

    @SystemMessage(String systemPrompt)
    String chat(String userMessage);
}
