package com.darquran.presentation.controller;

import com.darquran.application.dto.chat.ChatMessage;
import com.darquran.application.service.LiveSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;

@Controller
@CrossOrigin("*")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LiveSessionService liveSessionService;

    /** Chat global (legacy). */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(Instant.now().toString());
        }
        return chatMessage;
    }

    /**
     * Chat par session live : envoi vers /topic/live/{sessionId}.
     * Le client envoie vers /app/live/{sessionId}/chat avec payload { sender, content, type }.
     */
    @MessageMapping("/live/{sessionId}/chat")
    @SendTo("/topic/live/{sessionId}")
    public ChatMessage sendLiveMessage(
            @DestinationVariable String sessionId,
            @Payload ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now().toString());
        }
        message.setSessionId(sessionId);
        return message;
    }

    /**
     * Notification join/leave pour une session (optionnel).
     */
    @MessageMapping("/live/{sessionId}/presence")
    @SendTo("/topic/live/{sessionId}")
    public ChatMessage presence(
            @DestinationVariable String sessionId,
            @Payload ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now().toString());
        }
        message.setSessionId(sessionId);
        if (message.getType() == null) {
            message.setType("PRESENCE");
        }
        return message;
    }
}