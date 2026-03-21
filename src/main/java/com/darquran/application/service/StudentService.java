package com.darquran.application.service;

import com.darquran.application.dto.users.student.StudentRequest;
import com.darquran.application.dto.users.student.StudentResponse;
import com.darquran.domain.model.enums.Section;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    StudentResponse getById(String id);

    List<StudentResponse> getAllBySection(Section section);

    StudentResponse update(String id, StudentRequest request);

    void delete(String id);
}

