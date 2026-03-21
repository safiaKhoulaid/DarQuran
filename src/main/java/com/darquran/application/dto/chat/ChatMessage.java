package com.darquran.application.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String sessionId; // ID de la session live (pour le chat par session)
    private String sender;     // Nom affiché (ex: Safia)
    private String content;   // Contenu du message
    private String type;      // CHAT, JOIN, LEAVE
    private String timestamp; // ISO instant côté client ou serveur
}