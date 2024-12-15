package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class BackFrontEndEventsTest {

	public static void main(String[] args) throws InterruptedException  { 
	
		log.info("WS Channel Test");
		
		var my_bar = jui.progress(0, "Operation in progress. Please wait.");
		
		var btn = jui.button("Click-me", "primary");
		btn.onClick("alert('click me')");
		btn.onClick( (action, payload ) -> {

            log.info("Action: " + action + " payload: " + payload);
            btn.disable();
			
			for ( int i=1; i<=100; i++) {

    			try {
					Thread.sleep(100);

				} catch (InterruptedException e) {
					log.severe(e.getLocalizedMessage());
				}
    			my_bar.progress(i, "progress_text");
    		}
        	my_bar.progress(0, "progress_text");
        	btn.enable();
        } );
		
		var btn2 = jui.button("Click-me 2", "primary");
		btn2.onClick( () -> {
            System.out.println("Clicked!!");
        } );
		
		/*
		Thread.sleep(1000);
		*/
		//my_bar.empty();

		//st.button("Rerun")
		
        //jui.spinner("Task in esecuzione").executeTask(task);
        
        jui.start();

	}
}
