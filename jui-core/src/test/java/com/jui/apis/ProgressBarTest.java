package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class ProgressBarTest {

	public static void main(String[] args) throws InterruptedException  {
	
		log.info("WS Channel Test");
		
		var my_bar = jui.progress(50, "Operation in progress. Please wait.");

		/*
		for ( int i=51; i<=100; i++) {
			Thread.sleep(1000);
			my_bar.progress(i, "progress_text");
		}
		
		Thread.sleep(1000);
		*/
		//my_bar.empty();

		//st.button("Rerun")
		
		
		// Esempio di Runnable
        Runnable task = () -> {
            System.out.println("Esecuzione del task...");
            try {
                Thread.sleep(20000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task interrotto!");
            }
        };

        jui.spinner("Task in esecuzione").executeTask(task);
        
        jui.start();

	}
}
