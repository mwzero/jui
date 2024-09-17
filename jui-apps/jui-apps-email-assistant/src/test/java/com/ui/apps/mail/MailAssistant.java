package com.ui.apps.mail;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.testcontainers.milvus.MilvusContainer;

import com.ui.apps.mail.client.PrintMailMessageSinker;
import com.ui.apps.mail.client.ReadEmail;

import java.util.List;

import javax.mail.Flags;
import javax.mail.search.FlagTerm;

public class MailAssistant {

    public static void main(String[] args) throws Exception {
    	
    	String username = System.getenv("GMAIL_USERNAME");
        String password = System.getenv("GMAIL_PASSWORD");
        String pineconeApiKey = System.getenv("PINECONE_API_KEY");
        		
    	EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    	MailEmbeddingStore mailStore = new MailEmbeddingStore();
    	mailStore.createIndex(embeddingModel, pineconeApiKey, "mail-assistant");
    	
		ReadEmail
			.builder()
				.username(username)
				.password(password)
				.host("imap.gmail.com")
				.port("993")
				.folder("INBOX")
				.flagTerm(new FlagTerm(new Flags(Flags.Flag.SEEN), false))
			.build()
			.process(mailStore);
		
    	mailStore.query("list all senders");

    }
}
