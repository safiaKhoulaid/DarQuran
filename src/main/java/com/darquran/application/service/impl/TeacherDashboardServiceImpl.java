package com.darquran.application.service.impl;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.grade.StudentGradeRequest;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.dto.teacher.TeacherStudentResponse;
import com.darquran.application.dto.courses.CourseResponse;
import com.darquran.application.mapper.absence.StudentAbsenceMapper;
import com.darquran.application.mapper.grade.StudentGradeMapper;
import com.darquran.application.service.CourseService;
import com.darquran.application.service.RoomService;
import com.darquran.application.service.ScheduleService;
import com.darquran.application.service.StudentAbsenceService;
import com.darquran.application.service.TeacherDashboardService;
import com.darquran.domain.model.entities.school.Enrollment;
import com.darquran.domain.model.entities.school.ScheduleSlot;
import com.darquran.domain.model.entities.school.StudentAbsence;
import com.darquran.domain.model.entities.school.StudentGrade;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.repository.EnrollmentRepository;
import com.darquran.domain.repository.RoomRepository;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.StudentAbsenceRepository;
import com.darquran.domain.repository.StudentGradeRepository;
import com.darquran.domain.repository.StudentRepository;
import com.darquran.domain.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final ScheduleService scheduleService;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAbsenceService studentAbsenceService;
    private final StudentAbsenceRepository studentAbsenceRepository;
    private final StudentAbsenceMapper studentAbsenceMapper;
    private final StudentGradeRepository studentGradeRepository;
    private final StudentGradeMapper studentGradeMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseService courseService;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getMyClasses(String teacherId) {
        Set<String> roomIds = new LinkedHashSet<>();
        roomRepository.findByTeacherId(teacherId).forEach(r -> roomIds.add(r.getId()));
        scheduleSlotRepository.findByTeacherId(teacherId).stream()
                .map(s -> s.getRoom().getId())
                .forEach(roomIds::add);
        return roomIds.stream()
                .map(roomService::getById)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherStudentResponse> getStudentsByClass(String roomId, String teacherId) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByRoomIdAndTeacherId(roomId, teacherId);
        if (slots.isEmpty()) {
            return List.of();
        }
        List<String> courseIds = slots.stream()
                .map(s -> s.getCourse().getId())
                .distinct()
                .toList();
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdIn(courseIds);
        Set<String> studentIds = enrollments.stream()
                .map(e -> e.getStudent().getId())
                .collect(Collectors.toSet());
        return studentIds.stream()
                .map(studentRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toTeacherStudentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses(String teacherId) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByTeacherId(teacherId);
        Set<String> courseIds = slots.stream()
                .map(s -> s.getCourse().getId())
                .collect(Collectors.toSet());
        return courseIds.stream()
                .map(courseService::getCourseById)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleSlotResponse> getMyScheduleSlots(String teacherId) {
        return scheduleService.getByTeacher(teacherId);
    }

    @Override
    @Transactional
    public StudentAbsenceResponse markAbsence(String teacherId, StudentAbsenceRequest request) {
        ScheduleSlot slot = scheduleSlotRepository.findById(request.getScheduleSlotId())
                .orElseThrow(() -> new EntityNotFoundException("Créneau introuvable"));
        if (!teacherId.equals(slot.getTeacher().getId())) {
            throw new IllegalArgumentException("Vous ne pouvez saisir une absence que pour vos propres créneaux.");
        }
        return studentAbsenceService.markAbsence(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAbsenceResponse> getAbsencesByClass(String roomId, String teacherId) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByRoomIdAndTeacherId(roomId, teacherId);
        if (slots.isEmpty()) {
            return List.of();
        }
        List<String> slotIds = slots.stream().map(ScheduleSlot::getId).toList();
        return studentAbsenceRepository.findByScheduleSlotIdIn(slotIds).stream()
                .map(studentAbsenceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudentGradeResponse addGrade(String teacherId, StudentGradeRequest request) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant introuvable"));
        List<ScheduleSlot> mySlots = scheduleSlotRepository.findByTeacherId(teacherId);
        Course course = mySlots.stream()
                .filter(s -> s.getCourse().getId().equals(request.getCourseId()))
                .findFirst()
                .map(ScheduleSlot::getCourse)
                .orElseThrow(() -> new IllegalArgumentException("Vous ne pouvez ajouter une note que pour un cours que vous enseignez."));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable"));
        StudentGrade grade = StudentGrade.builder()
                .student(student)
                .course(course)
                .value(request.getValue())
                .gradeDate(request.getGradeDate() != null ? request.getGradeDate() : java.time.LocalDate.now())
                .comment(request.getComment())
                .teacher(teacher)
                .build();
        return studentGradeMapper.toResponse(studentGradeRepository.save(grade));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGradeResponse> getGradesByCourse(String courseId, String teacherId) {
        if (scheduleSlotRepository.findByTeacherId(teacherId).stream()
                .noneMatch(s -> s.getCourse().getId().equals(courseId))) {
            throw new IllegalArgumentException("Cours non enseigné par ce professeur.");
        }
        return studentGradeRepository.findByCourseIdAndTeacherId(courseId, teacherId).stream()
                .map(studentGradeMapper::toResponse)
                .toList();
    }

    private TeacherStudentResponse toTeacherStudentResponse(Student s) {
        return TeacherStudentResponse.builder()
                .id(s.getId())
                .nom(s.getNom())
                .prenom(s.getPrenom())
                .email(s.getEmail())
                .build();
    }
}
