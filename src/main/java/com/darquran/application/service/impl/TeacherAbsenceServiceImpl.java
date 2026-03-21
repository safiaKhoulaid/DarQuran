package com.darquran.application.service.impl;

import com.darquran.application.dto.absence.TeacherAbsenceRequest;
import com.darquran.application.dto.absence.TeacherAbsenceResponse;
import com.darquran.application.mapper.absence.TeacherAbsenceMapper;
import com.darquran.application.service.TeacherAbsenceService;
import com.darquran.domain.model.entities.school.ScheduleSlot;
import com.darquran.domain.model.entities.school.TeacherAbsence;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.TeacherAbsenceRepository;
import com.darquran.domain.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherAbsenceServiceImpl implements TeacherAbsenceService {

    private final TeacherAbsenceRepository teacherAbsenceRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final TeacherAbsenceMapper mapper;

    @Override
    @Transactional
    public TeacherAbsenceResponse markAbsence(TeacherAbsenceRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Enseignant introuvable avec l'id : " + request.getTeacherId()));

        ScheduleSlot slot = scheduleSlotRepository.findById(request.getScheduleSlotId())
                .orElseThrow(() -> new EntityNotFoundException("Créneau introuvable avec l'id : " + request.getScheduleSlotId()));

        TeacherAbsence absence = TeacherAbsence.builder()
                .teacher(teacher)
                .scheduleSlot(slot)
                .date(request.getDate())
                .status(request.getStatus())
                .justificationText(request.getJustificationText())
                .justificationFileUrl(request.getJustificationFileUrl())
                .build();

        return mapper.toResponse(teacherAbsenceRepository.save(absence));
    }

    @Override
    public List<TeacherAbsenceResponse> getByTeacher(String teacherId) {
        return teacherAbsenceRepository.findByTeacherId(teacherId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherAbsenceResponse> getByTeacherAndPeriod(String teacherId, LocalDate start, LocalDate end) {
        return teacherAbsenceRepository.findByTeacherIdAndDateBetween(teacherId, start, end)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}

