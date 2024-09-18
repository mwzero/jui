package com.ui.apps.mail;

import javax.mail.Flags;
import javax.mail.search.FlagTerm;

import com.ui.apps.mail.client.ReadEmail;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailAssistant {

    public static void main(String[] args) throws Exception {
    	
    	String username = System.getenv("GMAIL_USERNAME");
        String password = System.getenv("GMAIL_PASSWORD");
        String dbPgHost = System.getenv("DB_PG_HOST");
		int dbPgPort = Integer.parseInt(System.getenv("DB_PG_PORT"));
		String dbPgUser = System.getenv("DB_PG_USER");
		//String dbPgPwd = System.getenv("DB_PG_PWD");
		
		log.info("Reading mail for [{}]", username);
		log.info("Store: [{}] [{}] [{}]", dbPgHost, dbPgPort, dbPgUser);
        
        		
    	EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    	MailEmbeddingStore mailStore = new MailEmbeddingStore();
    	
    	mailStore.createIndex(embeddingModel, "mailstore", "ns-mails");
    	
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
		
    	mailStore.query("write a summary about glovo");

    }
}
