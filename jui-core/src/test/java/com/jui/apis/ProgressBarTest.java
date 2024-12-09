package com.jui.apis;

import static com.jui.JuiApp.jui;

import com.jui.html.elements.Button;

import lombok.extern.java.Log;

@Log
public class ProgressBarTest {

	public static void main(String[] args) throws InterruptedException  { 
	
		log.info("WS Channel Test");
		
		var my_bar = jui.progress(0, "Operation in progress. Please wait.");
		
		var btn = jui.button("Click-me", "primary");
		btn.onClick("alert('click me')");
		
		btn.onServerSide( () -> {
			
			System.out.println("Esecuzione del task...");
            btn.disable();
			
			for ( int i=1; i<=100; i++) {
    			try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			my_bar.progress(i, "progress_text");
    		}
        	my_bar.progress(0, "progress_text");
        	btn.enable();
            
    	});
		
		

		/*
		Thread.sleep(1000);
		*/
		//my_bar.empty();

		//st.button("Rerun")
		
		
        //jui.spinner("Task in esecuzione").executeTask(task);
        
        jui.start();

	}
}
