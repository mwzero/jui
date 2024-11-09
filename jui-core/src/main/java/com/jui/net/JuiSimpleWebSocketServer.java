package com.jui.net;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.jui.net.handlers.HandlerWebSocket;

import lombok.Setter;
import lombok.extern.java.Log;

@Setter
@Log
public class JuiSimpleWebSocketServer {
	
	private Map<String, HandlerWebSocket> handlers = new HashMap<>();
	
	int port = 8025;
	
	public static JuiSimpleWebSocketServer create(InetSocketAddress inetSocketAddress, int port) {
		
		JuiSimpleWebSocketServer wss = new JuiSimpleWebSocketServer();
		wss.setPort(port);
		return wss;
	}
	
	private JuiSimpleWebSocketServer() {
		
	}
	
	public void createContext(String path, HandlerWebSocket webSocketHandler) {
		
		handlers.put(path, webSocketHandler);
	}
	
	public void start() {
    	
        try (ServerSocket serverSocket = new ServerSocket(port)) {
        	
        	log.info("Server WebSocket in ascolto sulla porta:" + port);

            while (true) {
            	
            	try (Socket clientSocket = serverSocket.accept() ) {
                
            		log.info("Connessione stabilita con il client");

                    String path = performHandshake(clientSocket);
                    if (path != null) {
                        log.info("Handshake WebSocket completato con successo per il path: " + path);
                        handleWebSocketCommunication(clientSocket, path);
                    } else {
                    	log.severe("Handshake WebSocket failed, closing ws connection");
                        clientSocket.close();
                    }
                    
                } catch (SocketException e) {
                	log.severe("SocketException: Connessione interrotta. Dettagli: " + e.getMessage());
                } catch (IOException e) {
                	log.severe("IOException: Problema nella comunicazione con il client. Dettagli: " + e.getMessage());
                } 
            }
        } catch (IOException e) {
        	log.severe("Errore nell'avvio del server: " + e.getMessage());
        }
    }

    private String performHandshake(Socket clientSocket) throws IOException {
    	
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

        String data;
        String webSocketKey = null;
        String path = null;

        while (!(data = in.readLine()).isEmpty()) {
            if (data.startsWith("GET")) {
                String[] requestLine = data.split(" ");
                path = requestLine[1]; // Path è il secondo elemento della richiesta GET
            } else if (data.contains("Sec-WebSocket-Key")) {
                webSocketKey = data.split(":")[1].trim();
            }
        }

        if (webSocketKey == null || path == null) {
            return null;
        }

        String webSocketAcceptKey;
		try {
			webSocketAcceptKey = generateWebSocketAcceptKey(webSocketKey);

			out.println("HTTP/1.1 101 Switching Protocols");
	        out.println("Upgrade: websocket");
	        out.println("Connection: Upgrade");
	        out.println("Sec-WebSocket-Accept: " + webSocketAcceptKey);
	        out.println(); // Linea vuota per terminare l'header
	        out.flush();
		} catch (UnsupportedEncodingException e) {
			
			log.severe(e.getLocalizedMessage());
		}

        return path;
    }

    private String generateWebSocketAcceptKey(String webSocketKey) throws UnsupportedEncodingException {
    	
        String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String combined = webSocketKey + magicString;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashedData = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedData);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la generazione della chiave di accettazione WebSocket", e);
        }
    }

    private void handleWebSocketCommunication(Socket clientSocket, String path) throws IOException {
        InputStream in = clientSocket.getInputStream();
        OutputStream out = clientSocket.getOutputStream();
        
        HandlerWebSocket handler = handlers.get(path);
        if (handler == null) {
        	log.warning("Path [%s] not reconized.".formatted(path));
            clientSocket.close();
            return;
        }

        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                // 
                String message = decodeWebSocketFrame(buffer, bytesRead);
                log.info("Ricevuto dal client (" + path + "): " + message);
                
                String response = handler.handleMessage(message);

                byte[] encodedMessage = encodeWebSocketFrame(response);
                out.write(encodedMessage);
                out.flush();
            }
        } catch (SocketException e) {
        	log.severe("SocketException: Client connection interrupted: " + e.getMessage());
        } catch (IOException e) {
        	log.severe("Socket IO error: " + e.getMessage());
        } finally {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }

    private static String decodeWebSocketFrame(byte[] buffer, int length) {
        int payloadLength = buffer[1] & 0x7F;
        int dataStart = 2;

        if (payloadLength == 126) {
            dataStart += 2; 
        } else if (payloadLength == 127) {
            dataStart += 8; 
        }

        byte[] maskingKey = new byte[4];
        System.arraycopy(buffer, dataStart, maskingKey, 0, 4);
        dataStart += 4;

        byte[] payload = new byte[length - dataStart];
        System.arraycopy(buffer, dataStart, payload, 0, payload.length);

        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (payload[i] ^ maskingKey[i % 4]);
        }

        return new String(payload, StandardCharsets.UTF_8);
    }

    private static byte[] encodeWebSocketFrame(String message) {
        byte[] rawData = message.getBytes(StandardCharsets.UTF_8);
        int frameLength = rawData.length + 2;

        byte[] frame = new byte[frameLength];
        frame[0] = (byte) 0x81; 
        frame[1] = (byte) rawData.length; 

        System.arraycopy(rawData, 0, frame, 2, rawData.length);

        return frame;
    }

	

	
}
