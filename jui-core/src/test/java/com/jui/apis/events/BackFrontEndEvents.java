package com.jui.apis.events;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class BackFrontEndEvents {

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
			my_bar.clear();
			btn.enable();
        } );
		
        jui.server().start();

	}
}
