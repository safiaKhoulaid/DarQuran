package com.darquran.infrastructure.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Désérialise LocalDateTime en acceptant :
 * - "yyyy-MM-dd'T'HH:mm" (ex. 2016-06-05T03:48) — typique du champ HTML datetime-local
 * - "yyyy-MM-dd'T'HH:mm:ss" ou avec millisecondes (ISO-8601)
 */
public class LenientLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter WITH_SECONDS = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter WITHOUT_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.isBlank()) {
            return null;
        }
        value = value.trim();
        // Accepter aussi "yyyy-MM-ddTHH:mm:ss.SSSZ" (toISOString du front) : on retire le Z et les millisecondes optionnelles
        if (value.endsWith("Z")) {
            value = value.substring(0, value.length() - 1);
            if (value.length() > 19) {
                value = value.substring(0, 19);
            }
        }
        try {
            return LocalDateTime.parse(value, WITH_SECONDS);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(value, WITHOUT_SECONDS);
            } catch (DateTimeParseException e2) {
                throw new IOException("Format de date/heure non reconnu : " + value + ". Utilisez yyyy-MM-ddTHH:mm ou yyyy-MM-ddTHH:mm:ss", e2);
            }
        }
    }
}
