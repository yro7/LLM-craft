package fr.yro.llmcraft.Helper;

import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberStrategy;
import com.google.gson.stream.JsonReader;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;
import kotlin.text.Regex;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper extends IGModel {

    public List<Document> documentsToEmbed;

    /**
     * Helper class for RAG assitants. It's a work in progress, for now it's commented.
     * Please consider contributing with a PR :)
     * @param type
     * @param identifier
     */
    public Helper(IGModelType type, String identifier) {
        super(type, identifier);
        /**
        Path directoryPath = Path.of("/home/yroo/Spigot/1.12/plugins");
        ApacheTikaDocumentParser t = new ApacheTikaDocumentParser();
        TextDocumentParser parser =  new TextDocumentParser();


        String regex = ".*\\.yml.*";
        Pattern pattern = Pattern.compile(regex);
        List<Document> documents = FileSystemDocumentLoader
                .loadDocumentsRecursively(directoryPath, t)
                .stream().filter(doc -> {
                    String fileName = doc.metadata().get("file_name");
                    Matcher matcher = pattern.matcher(fileName);
                    return matcher.matches();
                        }).toList();

        System.out.println("Documents : " + documents);
        documents.forEach(document -> System.out.println(document.metadata()));
        documents.forEach(document -> System.out.println(document.metadata().get("file_name")));
        System.out.println("Documents size : " + documents.size());


        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        System.out.println("ingesting documents...");
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        System.out.println("building assistant...");

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(type.model)
                .chatMemory(chatMemory)
                .systemMessageProvider(chatMemoryId -> type.parameters.systemPrompt)
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        activeModels.put(identifier,this);
        **/
    }


    public static void test(){



    }
}
