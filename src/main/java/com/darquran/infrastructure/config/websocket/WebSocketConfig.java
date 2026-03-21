package com.darquran.infrastructure.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Hna fin Spring ghaylo7 les messages l nass (Sortie)
        config.enableSimpleBroker("/topic");

        // Hna fin Spring ghayst9bl les messages mn 3nd users (Entrée)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Hada howa "l'URL de base" bach tconnectay: http://localhost:8080/ws-live
        registry.addEndpoint("/ws-live")
                .setAllowedOriginPatterns("*") // Mohim bzaf bach Angular y9dr ydkhl
                .withSockJS(); // Fallback ila makhdmch WebSocket l3adi
    }
}