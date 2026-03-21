package com.darquran.application.dto.room;

import com.darquran.domain.model.enums.Section;
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
public class RoomRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    private Section section;

    private Integer capacity;

    /** ID du professeur à affecter (optionnel). Doit appartenir à la même section que la salle. */
    private String teacherId;
}

