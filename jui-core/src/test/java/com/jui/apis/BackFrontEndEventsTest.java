package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class BackFrontEndEventsTest {

	public static void main(String[] args) { 
	
		var my_bar = jui.progress(0, "Operation in progress. Please wait.");
		
		var btn = jui.button("Click-me", "primary");
		btn.onClick("alert('click me')");
		btn.onClick( () -> {

            btn.disable();
			for ( int i=1; i<=100; i++) {
				try {
					Thread.sleep(100);
					my_bar.progress(i, "progress %d%%".formatted(i));
				} catch (InterruptedException e) {
					log.severe(e.getLocalizedMessage());
				}
    		}
			my_bar.progress(0, "progress 0%");
        	btn.enable();
        } );
		
        jui.start();

	}
}
