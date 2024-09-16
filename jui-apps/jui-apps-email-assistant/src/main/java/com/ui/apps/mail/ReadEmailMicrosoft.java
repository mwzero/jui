package com.ui.apps.mail;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;


//https://github.com/OfficeDev/ews-java-api/wiki/Getting-Started-Guide
	
@Slf4j

public class ReadEmailMicrosoft {
	
	ExchangeService service;
	ExchangeCredentials credentials;
	
	String host;
	String username;
	String password;
	
	@Builder
	public ReadEmailMicrosoft(String username, String password, String host) throws URISyntaxException {
		
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
    	credentials = new WebCredentials(username, password);
    	service.setCredentials(credentials);
    	service.setUrl(new URI(host));
	}
	
	public void findAppointments(String startDate, String endDate) throws Exception {
		
			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			Date start = formatter.parse(startDate);
			Date end = formatter.parse(endDate);
			
			CalendarFolder cf=CalendarFolder.bind(service, WellKnownFolderName.Calendar);
			FindItemsResults<Appointment> findResults = cf.findAppointments(new CalendarView(start, end));
			
			for (Appointment appt : findResults.getItems()) {
				
				appt.load(PropertySet.FirstClassProperties);
				
				log.info("Subject[{}] Date[{}] Sender[{}]"
						,appt.getSubject()
						,appt.getDateTimeCreated()
						,appt.getOrganizer().getName());
				//System.out.println("BODY========"+appt.getBody());
			}
	}
	
    public void readingMail() throws Exception {
    	
    	    	 final int pageSize = 50;
    	    ItemView view = new ItemView(pageSize);
    	    FindItemsResults<Item> findResults;

    	    //do {
    	        findResults = service.findItems(WellKnownFolderName.Inbox, view);

    	        for (Item item : findResults.getItems()) {
    	        	
    	        	if (item instanceof EmailMessage) {
    	        		// If the item is an e-mail message, write the sender's name.
    	        		EmailMessage message = (EmailMessage)item;
    	        		
    	        		log.info("[{}] [{}] [{}]", 
        	        			message.getSubject(),
        	        			message.getDateTimeCreated(),
        	        			message.getSender().getName());	
    	        		
    	        		if (message.getHasAttachments() || message.getAttachments().getItems().size() > 0) {

    	        			Item itemApp = Item.bind(service, item.getId());
    	        			
	        		        //get all the attachments
	        		        AttachmentCollection attachmentsCol = itemApp.getAttachments();

	        		        log.info("File Count: " +attachmentsCol.getCount());

	        		        //loop over the attachments
	        		        for (int i = 0; i < attachmentsCol.getCount(); i++) {
	        		        	
	        		            Attachment attachment = attachmentsCol.getPropertyAtIndex(i);
	        		            
	        		            if (attachment instanceof FileAttachment || attachment.getIsInline()) {
	        		            	
	        		            	
	        		            	 
	        		            	FileAttachment fileAttachment = (FileAttachment) attachment;
	        		            	fileAttachment.load("C:\\temp\\xxmail\\" + fileAttachment.getName());
	        		            	
	        		            	/*
	        	        		    byte[] attachmentContent = fileAttachment.getContent();

	        	        		    if (attachmentContent != null && attachmentContent.length > 0) {

	        	        		        //check the size
	        	        		        int attachmentSize = attachmentContent.length;
	        	        		        String fileName = fileAttachment.getName();
	        	        		        String mimeType = fileAttachment.getContentType();
	        	        		        log.info("File Name: " + fileName + "  File Size: " + attachmentSize);

	        	        		        if (attachmentContent != null && attachmentContent.length > 0) {
	        	        		            //String base64Encoded = UtilFunctions.encodeToBase64(attachmentContent);
	        	        		            //fileAttachments.put(UtilConstants.ATTACHMENT_CONTENT, base64Encoded);
	        	        		        }
	        	        		    }
	        	        		    */
	        		            }
	        		        }
    	        		}
    	            } else if (item instanceof Appointment) {
    	        	} else {
    	        	}
    	        }

    	        view.setOffset(view.getOffset() + pageSize);
    	        
    	    //} while (findResults.isMoreAvailable());
    }
    
    public void sendMail() throws Exception {
    	
        EmailMessage msg= new EmailMessage(service);
        msg.setSubject("Test Maurizio");
        msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API."));
        msg.getToRecipients().add("m.farina@crif.com");
        msg.send();
    }
    	
}
