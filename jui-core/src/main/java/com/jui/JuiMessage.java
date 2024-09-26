package com.jui;

import com.google.gson.Gson;

public class JuiMessage {
	
	 String action;
     String id;
     
     public static JuiMessage parseOf(String message) {
    	 
     	Gson gson = new Gson();
     	JuiMessage msg = gson.fromJson(message, JuiMessage.class);
     	
		return msg;

     }
     
     

}
