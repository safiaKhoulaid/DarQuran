package com.darquran.application.dto.live;

import com.darquran.domain.model.enums.live.LiveAccessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveSessionRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotBlank(message = "Stream key is required")
    @Size(max = 100)
    private String streamKey;

    @NotNull(message = "Access type is required")
    private LiveAccessType accessType;

    private boolean adaptiveQualityEnabled = true;

    private boolean recordingEnabled = true;

    @NotNull(message = "Scheduled start is required")
    private LocalDateTime scheduledStartAt;

    private LocalDateTime scheduledEndAt;
}
