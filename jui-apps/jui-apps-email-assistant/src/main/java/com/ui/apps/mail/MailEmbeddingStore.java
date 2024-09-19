package com.ui.apps.mail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import com.ui.apps.mail.classifiers.BasicClassifier;
import com.ui.apps.mail.client.IMailMessageSinker;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import lombok.extern.slf4j.Slf4j;
import opennlp.tools.dictionary.Index;

@Slf4j
public class MailEmbeddingStore implements IMailMessageSinker{
	
	protected final String indexName = "mail-assistant";
	
	EmbeddingStore<TextSegment> embeddingStore;
	EmbeddingModel embeddingModel;
	ChatLanguageModel chatModel;
	
	Index pcIndex;
	
	public void createIndex(ChatLanguageModel chatModel, EmbeddingModel embeddingModel, String index, String namespace) throws Exception {
		
		this.embeddingModel = embeddingModel;
		this.chatModel = chatModel;

		/*
		embeddingStore = PineconeEmbeddingStore.builder()
		        .apiKey(System.getenv("PINECONE_API_KEY"))
		        .index(index)
		        .nameSpace(namespace)
		        .createIndex(PineconeServerlessIndexConfig.builder()
		                .cloud("AWS")
		                .region("us-east-1")
		                .dimension(embeddingModel.dimension())
		                .build())
		        .build();
		
		*/
		
		/*
    	Pinecone pc = new Pinecone.Builder(apiKey).build();
    	pc.createServerlessIndex(index, "cosine", 2, "aws", "us-east-1", DeletionProtection.DISABLED);
    	pcIndex = pc.getIndexConnection(index);
    	*/
		embeddingStore = PgVectorEmbeddingStore.builder()
                .host(System.getenv("DB_PG_HOST"))
                .port(Integer.parseInt(System.getenv("DB_PG_PORT")))
                .user(System.getenv("DB_PG_USER"))
                .password(System.getenv("DB_PG_PWD"))
                .database("postgres")
                .table(index)
                .dimension(384)
                //.dropTableFirst(true)
                .build();
    	
	}
	
	public void add(String content, Map<String, ?> metadata) {
		
		
		TextSegment segment = TextSegment.from(content, Metadata.from(metadata));
		TextSegment segment2 = TextSegment.from("test", Metadata.from(metadata));
		Embedding embedding = embeddingModel.embed(segment).content();
		embeddingStore.add(embedding, segment);
		
		//UpsertResponse response = pcIndex.upsert(content, embedding.vectorAsList(),"ns1");
		
	}
	
	public void query (String query ) {
		
		Embedding queryEmbedding = embeddingModel.embed(query).content();
		EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(1)
		        .build();
		EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
		
		EmbeddingMatch<TextSegment> embeddingMatch = searchResult.matches().get(0);
		System.out.println(embeddingMatch.score()); // 0.8144288515898701
		System.out.println(embeddingMatch.embedded().text()); // I like football.
	}

	@Override
	public void process(Message message, String saveDirectory) throws MessagingException, IOException {
		
		Address[] fromAddress = message.getFrom();
		String subject = message.getSubject();
		String sentDate = message.getSentDate().toString();

		String contentType = message.getContentType();
		String messageContent = "";

		String attachFiles = "";

		if (contentType.contains("multipart")) {
			
			Multipart multiPart = (Multipart) message.getContent();
			int numberOfParts = multiPart.getCount();
			
			for (int partCount = 0; partCount < numberOfParts; partCount++) {
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
				if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
					// this part is attachment
					String fileName = part.getFileName();
					attachFiles += fileName + ", ";
					String name = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
					part.saveFile(saveDirectory + File.separator + name);
				} else {
					// this part may be the message content
					messageContent = part.getContent().toString();
				}
			}

			if (attachFiles.length() > 1) {
				attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
			}
		} else if (contentType.contains("text/plain") ) {
			
			Object content = message.getContent();
			if (content != null) {
				messageContent = content.toString();
			}
			
		} else if (contentType.contains("text/html")) {
			Object content = message.getContent();
			if (content != null) {
				messageContent = tika_autoParser(content.toString());
				
			}
		}

		log.debug("From: [] Subject []",  
    			InternetAddress.toString(message.getFrom()), 
    			message.getSubject() );
    	
    	Map<String, String > metadata = new HashMap<String, String>();
    	metadata.put("From", InternetAddress.toString(message.getFrom()));
    	metadata.put("Subject", subject);
    	metadata.put("Sent Date", sentDate);
    	add(messageContent, metadata);
    	
    	String label = BasicClassifier.process(chatModel, messageContent);
    	log.info("Etichetta: [{}]", label);
		System.out.println("Etichetta: " + label);
	}
	
	public String tika_autoParser(String htmlContent) {
		
		Tika tika = new Tika();

    	InputStream stream = new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
        String content = null;
		try {
			content = tika.parseToString(stream);
		} catch (IOException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return content;
            
		 
	}
}
