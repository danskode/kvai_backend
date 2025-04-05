package kea.sofie.kvai_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);
    @Value("classpath:/poldocs/jacob-naesager.pdf")
    private Resource jacobnaesagerPdf;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    private final VectorStore vectorStore;

    @Override
    public void run(String... args) throws Exception {

        // Read the documents ...
        TikaDocumentReader reader = new TikaDocumentReader(jacobnaesagerPdf);
        // Split the documents ...
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> documents = textSplitter.split(reader.read() );
        // Store the data in the vector database ...
        vectorStore.accept(documents);
        log.info("Loaded {} documents", documents.size());

    }
}
