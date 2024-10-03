package com.jui.model;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

/**
 * The message that the front-end sends to the back-end.
 * It encapsulates the data that the client-side application needs to transmit 
 * to the server, such as user inputs, form data, or any other interaction events.
 */
@Getter
@Setter
public class JuiMessage {
	
	 String action;
     String id;
     
     public static JuiMessage parseOf(String message) {
    	 
     	Gson gson = new Gson();
     	JuiMessage msg = gson.fromJson(message, JuiMessage.class);
     	
		return msg;

     }
     
     

}
