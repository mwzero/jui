package com.ui.apps.mail.client;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.search.FlagTerm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MailTests {
	
	@Test
	public void readOfficeMail() throws Exception {
		
		String username = System.getenv("OFFICE_USERNAME");
        String password = System.getenv("OFFICE_PASSWORD");
        String host = System.getenv("OFFICE_HOST");
        
        ReadEmailMicrosoft
        	.builder()
        		.username(username)
        		.password(password)
        		.host(host)
        	.build()
        	.readingMail();
        
	}
	
	@Test
	@Disabled
	public void readGoogleMail() throws IOException, URISyntaxException, MessagingException {
		
		String username = System.getenv("GMAIL_USERNAME");
        String password = System.getenv("GMAIL_PASSWORD");

		ReadEmail
			.builder()
				.username(username)
				.password(password)
				.host("imap.gmail.com")
				.port("993")
				.folder("INBOX")
				.flagTerm(new FlagTerm(new Flags(Flags.Flag.SEEN), false))
			.build()
			.process(new PrintMailMessageSinker());
		
	}

}
