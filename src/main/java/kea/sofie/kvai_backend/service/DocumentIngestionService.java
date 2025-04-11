package kea.sofie.kvai_backend.service;

import kea.sofie.kvai_backend.model.Politician;
import kea.sofie.kvai_backend.repository.PoliticianRepository;
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
    @Value("classpath:/poldocs/jakob-naesager.pdf")
    private Resource jacobnaesagerPdf;
    private final VectorStore vectorStore;
    private final PoliticianRepository politicianRepository;

    public DocumentIngestionService(VectorStore vectorStore, PoliticianRepository politicianRepository) {
        this.vectorStore = vectorStore;
        this.politicianRepository = politicianRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(politicianRepository.findAll().size() == 0) {

            Politician politician1 = new Politician("Jakob Næsager", "Det Konservative Folkeparti 🫡", "København");
            Politician politician2 = new Politician("Mia Nygaard", "Det Radikale Venstre 🟪", "København");
            Politician politician3 = new Politician("Pernille Rosenkrantz-Theil", "Socialdemokratiet 🌹", "København");

            politicianRepository.save(politician1);
            politicianRepository.save(politician2);
            politicianRepository.save(politician3);
        }


        // Read the documents ...
        TikaDocumentReader reader = new TikaDocumentReader(jacobnaesagerPdf);
        // Split the documents ...
        TextSplitter textSplitter = new TokenTextSplitter(125, 75, 25, 10000, true);

        try {
            // Read and split the document
            List<Document> documents = textSplitter.split(reader.read());
            vectorStore.accept(documents);
            // Efter vectorStore.accept(documents);
            log.info("Dokumenter gemt i VectorStore: {}", documents.size());
            for (Document doc : documents) {
                log.info("Dokument indhold: {}", doc.getText());
            }
            log.info("Loaded {} documents", documents.size());
        } catch (Exception e) {
            log.error("Error processing the document: {}", e.getMessage(), e);
        }
    }
}
