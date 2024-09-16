package com.ui.apps.mail;

import java.io.File;
import java.io.IOException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintMailMessageSinker implements IMailMessageSinker  {
	
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
		} else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
			Object content = message.getContent();
			if (content != null) {
				messageContent = content.toString();
			}
		}

		/*print out details of each message
		System.out.println("Message #" + (i + 1) + ":");
		System.out.println("\t From: " + from);
		System.out.println("\t Subject: " + subject);
		System.out.println("\t Sent Date: " + sentDate);
		System.out.println("\t Message: " + messageContent);
		System.out.println("\t Attachments: " + attachFiles);*/
		
    	log.debug("From: [] Subject []",  
    			InternetAddress.toString(message.getFrom()), 
    			message.getSubject() );

    }
    	
}
