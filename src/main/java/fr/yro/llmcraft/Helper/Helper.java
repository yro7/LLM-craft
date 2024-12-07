package fr.yro.llmcraft.Helper;

import dev.langchain4j.data.document.Document;
import fr.yro.llmcraft.Model.IGModel;
import fr.yro.llmcraft.Model.IGModelType;

import java.util.List;

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
