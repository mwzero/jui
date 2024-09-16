package com.ui.apps.mail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class ReadEmail {
	
	String host;
	String port;
	String username;
	String password;
	
	String folder;
	FlagTerm flagTerm;
	
	private static String saveDirectory = "c://temp//content";
	
    public void process(IMailMessageSinker sinker) {
    	
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.port", port);
        properties.put("mail.imaps.ssl.enable", "true");

        try {
            Session emailSession = Session.getInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(username, password);

            Folder emailFolder = store.getFolder(folder);
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.search(flagTerm);

            System.out.println("Numero di email non lette: " + messages.length);

            for (int i = 0; i < messages.length; i++) {
            	
                Message message = messages[i];
                
                sinker.process(message, saveDirectory);
            }

            emailFolder.close(false);
            store.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
