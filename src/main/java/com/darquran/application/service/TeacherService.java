package com.darquran.application.service;

import com.darquran.application.dto.users.teacher.TeacherRequest;
import com.darquran.application.dto.users.teacher.TeacherResponse;
import com.darquran.domain.model.enums.Section;

import java.util.List;

public interface TeacherService {

    TeacherResponse create(TeacherRequest request);

    TeacherResponse getById(String id);

    List<TeacherResponse> getAllBySection(Section section);

    TeacherResponse update(String id, TeacherRequest request);

    void delete(String id);
}

