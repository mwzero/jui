package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class StatusElementstTest {

	public static void main(String[] args)  {
	
		log.info("Text Elements API");
		
		jui.success("Success", "this is :blue[pre-formatted] text :sunglasses: :angry:");
		
		jui.info("Info", "this is :blue[pre-formatted] text :sunglasses: :angry:");
		
		jui.warning("warning", "this is :blue[pre-formatted] text :sunglasses: :angry:");
		
		jui.error("Error", "this is :blue[pre-formatted] text :sunglasses: :angry:");
		
        jui.start();

	}
}
