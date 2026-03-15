package com.darquran.application.dto.live;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveCommentRequest {

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 2000)
    private String content;

    /**
     * Nom affiché pour accès externe (optionnel si utilisateur connecté).
     */
    @Size(max = 100)
    private String authorDisplayName;
}
