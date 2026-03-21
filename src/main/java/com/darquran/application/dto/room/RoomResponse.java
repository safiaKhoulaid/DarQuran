package com.darquran.application.dto.room;

import com.darquran.domain.model.enums.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private String id;
    private String name;
    private Section section;
    private Integer capacity;
    private String teacherId;
    private String teacherName;
}

