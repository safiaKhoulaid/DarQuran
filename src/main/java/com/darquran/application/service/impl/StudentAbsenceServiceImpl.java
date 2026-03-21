package com.darquran.application.service.impl;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.mapper.absence.StudentAbsenceMapper;
import com.darquran.application.service.StudentAbsenceService;
import com.darquran.domain.model.entities.school.ScheduleSlot;
import com.darquran.domain.model.entities.school.StudentAbsence;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.StudentAbsenceRepository;
import com.darquran.domain.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAbsenceServiceImpl implements StudentAbsenceService {

    private final StudentAbsenceRepository studentAbsenceRepository;
    private final StudentRepository studentRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final StudentAbsenceMapper mapper;

    @Override
    @Transactional
    public StudentAbsenceResponse markAbsence(StudentAbsenceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable avec l'id : " + request.getStudentId()));

        ScheduleSlot slot = scheduleSlotRepository.findById(request.getScheduleSlotId())
            .orElseThrow(() -> new EntityNotFoundException("Créneau introuvable avec l'id : " + request.getScheduleSlotId()));

        StudentAbsence absence = StudentAbsence.builder()
                .student(student)
                .scheduleSlot(slot)
                .date(request.getDate())
                .status(request.getStatus())
                .justificationText(request.getJustificationText())
                .justificationFileUrl(request.getJustificationFileUrl())
                .build();

        return mapper.toResponse(studentAbsenceRepository.save(absence));
    }

    @Override
    public List<StudentAbsenceResponse> getByStudent(String studentId) {
        return studentAbsenceRepository.findByStudentId(studentId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentAbsenceResponse> getByStudentAndPeriod(String studentId, LocalDate start, LocalDate end) {
        return studentAbsenceRepository.findByStudentIdAndDateBetween(studentId, start, end)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}

