package com.ui.apps.mail;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

public interface IMailMessageSinker {
	
    public void process(Message message, String saveDirectory) throws MessagingException, IOException;
    	
}
