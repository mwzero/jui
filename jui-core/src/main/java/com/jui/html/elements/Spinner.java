package com.jui.html.elements;

import java.io.IOException;
import java.io.OutputStream;

import com.jui.JuiApp;
import com.jui.html.WebElement;
import com.jui.utils.WS;

import lombok.extern.java.Log;

@Log
public class Spinner extends WebElement {
	
	String text;
	
	public Spinner(String text) {
		super("Spinner");
		
		this.text = text;
	}
	
	private void onTaskComplete() {
		
		
		try {
			OutputStream out;
			out = JuiApp.jui.clientSocket.getOutputStream();
			String response = "finito";
			out.write(WS.encodeWebSocketFrame(response));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("Task completato!");
    }
	
	public void executeTask(Runnable task) {
		
        Thread thread = new Thread(() -> {
            try {
                // Esecuzione del Runnable
                task.run();
            } finally {
                // Eseguire il metodo al termine
                onTaskComplete();
            }
        });
        thread.start();
    }
	
	
	@Override
	public String getHtml() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.clientId()));
		
		return """
				<div id="%s" class="spinner-border" style="width: 3rem; height: 3rem;" role="status">
					<span class="visually-hidden">%s</span>
				</div>
				""".formatted(this.clientId(), this.text);
						
	}
}
