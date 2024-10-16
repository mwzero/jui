package com.jui.net.wss;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

@Setter
public class SimpleWebSocketServer {
	
	private Map<String, WebSocketHandler> handlers = new HashMap<>();
	
	int port = 8025;
	
	public static SimpleWebSocketServer create(InetSocketAddress inetSocketAddress, int port) {
		
		SimpleWebSocketServer wss = new SimpleWebSocketServer();
		wss.setPort(port);
		return wss;
	}
	
	private SimpleWebSocketServer() {
		
	}
	
	public void createContext(String path, WebSocketHandler webSocketHandler) {
		
		handlers.put(path, webSocketHandler);
		
	}
	
	public void start() {
    	
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server WebSocket in ascolto sulla porta " + port);

            while (true) {
            	
            	try (Socket clientSocket = serverSocket.accept() ) {
                
                    System.out.println("Connessione stabilita con il client");

                    String path = performHandshake(clientSocket);
                    if (path != null) {
                        System.out.println("Handshake WebSocket completato con successo per il path: " + path);

                        // Gestisci la comunicazione in base al path
                        handleWebSocketCommunication(clientSocket, path);
                    } else {
                        System.out.println("Handshake WebSocket fallito, chiudo la connessione");
                        clientSocket.close();
                    }
                    
                } catch (SocketException e) {
                    System.err.println("SocketException: Connessione interrotta. Dettagli: " + e.getMessage());
                    // Qui potresti implementare una logica di riconnessione o logging avanzato
                } catch (IOException e) {
                    System.err.println("IOException: Problema nella comunicazione con il client. Dettagli: " + e.getMessage());
                } 
            }
        } catch (IOException e) {
            System.err.println("Errore nell'avvio del server: " + e.getMessage());
        }
    }

 // Aggiunge il path al ritorno dell'handshake
    private String performHandshake(Socket clientSocket) throws IOException {
    	
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

        String data;
        String webSocketKey = null;
        String path = null;

        // Leggi l'header HTTP inviato dal client
        while (!(data = in.readLine()).isEmpty()) {
            if (data.startsWith("GET")) {
                // Ottieni il path dalla richiesta GET
                String[] requestLine = data.split(" ");
                path = requestLine[1]; // Path è il secondo elemento della richiesta GET
            } else if (data.contains("Sec-WebSocket-Key")) {
                webSocketKey = data.split(":")[1].trim();
            }
        }

        if (webSocketKey == null || path == null) {
            return null;
        }

        // Genera la chiave di risposta WebSocket
        String webSocketAcceptKey;
		try {
			webSocketAcceptKey = generateWebSocketAcceptKey(webSocketKey);
			// Costruisci la risposta di handshake
	        out.println("HTTP/1.1 101 Switching Protocols");
	        out.println("Upgrade: websocket");
	        out.println("Connection: Upgrade");
	        out.println("Sec-WebSocket-Accept: " + webSocketAcceptKey);
	        out.println(); // Linea vuota per terminare l'header
	        out.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        

        return path; // Ritorna il path del client
    }

    private String generateWebSocketAcceptKey(String webSocketKey) throws UnsupportedEncodingException {
        String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String combined = webSocketKey + magicString;

        // Esegui SHA-1 hash della stringa combinata
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashedData = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedData);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la generazione della chiave di accettazione WebSocket", e);
        }
    }

 // Modificato per gestire diverse logiche a seconda del path
    private void handleWebSocketCommunication(Socket clientSocket, String path) throws IOException {
        InputStream in = clientSocket.getInputStream();
        OutputStream out = clientSocket.getOutputStream();
        
        WebSocketHandler handler = handlers.get(path);
        if (handler == null) {
            System.out.println("Path non riconosciuto: " + path);
            clientSocket.close();
            return;
        }

        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                // Decodifica il payload WebSocket
                String message = decodeWebSocketFrame(buffer, bytesRead);
                System.out.println("Ricevuto dal client (" + path + "): " + message);
                
                //Usa l'handler per gestire il messaggio
                String response = handler.handleMessage(message);

                // Costruisci la risposta e inviala al client
                byte[] encodedMessage = encodeWebSocketFrame(response);
                out.write(encodedMessage);
                out.flush();
            }
        } catch (SocketException e) {
            System.err.println("SocketException: Connessione con il client interrotta. Dettagli: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: Problema nella lettura/scrittura del socket. Dettagli: " + e.getMessage());
        } finally {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }

    private static String decodeWebSocketFrame(byte[] buffer, int length) {
        int payloadLength = buffer[1] & 0x7F;
        int dataStart = 2;

        // Controlla se il payload è esteso
        if (payloadLength == 126) {
            dataStart += 2; // Se il payload è esteso (2 byte)
        } else if (payloadLength == 127) {
            dataStart += 8; // Se il payload è molto lungo (8 byte)
        }

        // I prossimi 4 byte sono la maschera
        byte[] maskingKey = new byte[4];
        System.arraycopy(buffer, dataStart, maskingKey, 0, 4);
        dataStart += 4;

        // Ora estrai il payload mascherato
        byte[] payload = new byte[length - dataStart];
        System.arraycopy(buffer, dataStart, payload, 0, payload.length);

        // Applica la maschera al payload
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (payload[i] ^ maskingKey[i % 4]);
        }

        // Ritorna il payload come stringa UTF-8
        return new String(payload, StandardCharsets.UTF_8);
    }

    private static byte[] encodeWebSocketFrame(String message) {
        byte[] rawData = message.getBytes(StandardCharsets.UTF_8);
        int frameLength = rawData.length + 2;

        byte[] frame = new byte[frameLength];
        frame[0] = (byte) 0x81; // Primo byte: testo, FIN bit impostato
        frame[1] = (byte) rawData.length; // Secondo byte: lunghezza del payload

        // Copia il payload nel frame
        System.arraycopy(rawData, 0, frame, 2, rawData.length);

        return frame;
    }

	

	
}
