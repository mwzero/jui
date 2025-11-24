
package it.example.streamlitlike.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        
        SessionManager sessionManager = new InMemorySessionManager();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MainPageHandler(sessionManager));
        server.createContext("/event", new EventHandler(sessionManager));
        server.createContext("/upload", new UploadHandler(sessionManager));

        server.setExecutor(null);
        System.out.println("Server avviato su http://localhost:8080");
        server.start();
    }
}
