package com.jui.utils;

import java.net.ProxySelector;
import java.net.URI;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.util.List;

public class ProxyDetection {
    
    public static void main(String[] args) {
        try {
            // URI per cui rilevare le impostazioni del proxy
            URI uri = new URI("http://www.example.com");

            // Ottieni il ProxySelector predefinito
            ProxySelector proxySelector = ProxySelector.getDefault();

            // Ottieni la lista dei proxy per l'URI specificato
            List<Proxy> proxyList = proxySelector.select(uri);

            for (Proxy proxy : proxyList) {
                System.out.println("Proxy type: " + proxy.type());

                InetSocketAddress addr = (InetSocketAddress) proxy.address();
                if (addr != null) {
                    System.out.println("Proxy hostname: " + addr.getHostName());
                    System.out.println("Proxy port: " + addr.getPort());
                } else {
                    System.out.println("No proxy detected");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

