package com.jui.model;

import java.util.Map;

import com.google.gson.Gson;
import com.jui.html.WebElement;

import lombok.Setter;

@Setter
public class JuiNotification {
	
	 String id;
	 String clientId;
	 String action;
	 String command;
	 
	 Map<String, Object> payload;
     
     public JuiNotification(WebElement webElement, String action) {
    	 
     	this.id = webElement.Id();
     	this.clientId = webElement.clientId();
     	this.action = action;
     	
     	this.payload = webElement.getMapValue();
     	
     }
     
     public String toJsonString() {
    	 
	     Gson gson = new Gson();
	     return gson.toJson(this);
     }
     
     

}
