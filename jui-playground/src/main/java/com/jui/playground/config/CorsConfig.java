package com.jui.playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Configura le origini consentite
        config.addAllowedOriginPattern("*"); // Consente tutte le origini, puoi specificare domini specifici come "http://localhost:3000"
        
        // Configura metodi consentiti (GET, POST, ecc.)
        config.addAllowedMethod("*");
        
        // Configura le intestazioni consentite
        config.addAllowedHeader("*");

        // Se vuoi consentire le credenziali (cookie, autenticazione)
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}