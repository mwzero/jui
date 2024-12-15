package com.jui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.jui.html.WebElement;
import com.jui.model.JuiNotification;

import lombok.Setter;
import lombok.extern.java.Log;

@Setter
@Log
public class JuiNotifier {
	
	//
	Socket clientSocket;
	
	public static final JuiNotifier notifier = new JuiNotifier();
	
	public synchronized void onAttributeChanged(JuiNotification notification) {
		
		try {

			OutputStream out = clientSocket.getOutputStream();
			out.write(encodeWebSocketFrame(notification.toJsonString()));
			out.flush();

			log.fine("Socket message sent");

		} catch (IOException e) {

			log.severe("Error sendiing socket message. Err:" + e.getLocalizedMessage());
		}
	}
	
	public void onAttributeChanged(WebElement webelement) {

		JuiNotification notification = new JuiNotification(webelement,"change");

		this.onAttributeChanged(notification);
		
	}
	
	
	protected static byte[] encodeWebSocketFrame(String message) {

	    byte[] rawData = message.getBytes(StandardCharsets.UTF_8);
	    int rawDataLength = rawData.length;

	    // Determina la dimensione del frame
	    int frameHeaderLength = 2; // Byte iniziali del frame
	    if (rawDataLength > 125 && rawDataLength <= 65535) {
	        frameHeaderLength += 2; // Estensione per payload medio
	    } else if (rawDataLength > 65535) {
	        frameHeaderLength += 8; // Estensione per payload lungo
	    }

	    // Crea il frame
	    byte[] frame = new byte[frameHeaderLength + rawDataLength];
	    frame[0] = (byte) 0x81; // Frame con FIN=true e opcode=1 (testo)

	    // Gestione della lunghezza del payload
	    if (rawDataLength <= 125) {
	        frame[1] = (byte) rawDataLength;
	    } else if (rawDataLength <= 65535) {
	        frame[1] = 126;
	        frame[2] = (byte) ((rawDataLength >> 8) & 0xFF); // Byte alto
	        frame[3] = (byte) (rawDataLength & 0xFF);        // Byte basso
	    } else {
	        frame[1] = 127;
	        for (int i = 0; i < 8; i++) {
	            frame[2 + i] = (byte) ((rawDataLength >> (56 - 8 * i)) & 0xFF);
	        }
	    }

	    // Copia i dati del payload nel frame
	    System.arraycopy(rawData, 0, frame, frameHeaderLength, rawDataLength);

	    return frame;
	}

}
