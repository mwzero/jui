package com.jui;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

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
