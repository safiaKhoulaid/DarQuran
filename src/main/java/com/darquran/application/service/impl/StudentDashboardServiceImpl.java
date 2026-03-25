package com.darquran.application.service.impl;

import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.dashboard.StudentDashboardSummary;
import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.dto.users.student.StudentRequest;
import com.darquran.application.dto.users.student.StudentResponse;
import com.darquran.application.mapper.absence.StudentAbsenceMapper;
import com.darquran.application.mapper.enrollment.EnrollmentMapper;
import com.darquran.application.mapper.grade.StudentGradeMapper;
import com.darquran.application.mapper.users.student.StudentMapper;
import com.darquran.application.service.StudentDashboardService;
import com.darquran.domain.model.entities.school.Enrollment;
import com.darquran.domain.model.entities.school.Room;
import com.darquran.domain.model.entities.school.ScheduleSlot;
import com.darquran.domain.model.entities.school.StudentAbsence;
import com.darquran.domain.model.entities.school.StudentGrade;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.AbsenceStatus;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.EnrollmentRepository;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.StudentAbsenceRepository;
import com.darquran.domain.repository.StudentGradeRepository;
import com.darquran.domain.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final StudentRepository studentRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final StudentAbsenceRepository studentAbsenceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final StudentMapper studentMapper;
    private final StudentGradeMapper studentGradeMapper;
    private final StudentAbsenceMapper studentAbsenceMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getProfile(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + studentId));
        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateProfile(String studentId, StudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + studentId));

        studentMapper.updateEntityFromRequest(request, student);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            student.setPassword(Password.create(request.getPassword(), passwordEncoder));
        }

        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGradeResponse> getGrades(String studentId) {
        return studentGradeRepository.findByStudentId(studentId).stream()
                .map(studentGradeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAbsenceResponse> getAbsences(String studentId) {
        return studentAbsenceRepository.findByStudentId(studentId).stream()
                .map(studentAbsenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollments(String studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleSlotResponse> getSchedule(String studentId) {
        List<String> courseIds = enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> Boolean.TRUE.equals(e.getActive()))
                .map(e -> e.getCourse().getId())
                .toList();

        if (courseIds.isEmpty()) {
            return List.of();
        }

        return scheduleSlotRepository.findByCourseIdIn(courseIds).stream()
                .map(this::toScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRooms(String studentId) {
        List<String> courseIds = enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> Boolean.TRUE.equals(e.getActive()))
                .map(e -> e.getCourse().getId())
                .toList();

        if (courseIds.isEmpty()) {
            return List.of();
        }

        return scheduleSlotRepository.findByCourseIdIn(courseIds).stream()
                .map(ScheduleSlot::getRoom)
                .distinct()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardSummary getDashboardSummary(String studentId) {
        StudentResponse profile = getProfile(studentId);
        List<StudentGradeResponse> grades = getGrades(studentId);
        List<StudentAbsenceResponse> absences = getAbsences(studentId);
        List<EnrollmentResponse> enrollments = getEnrollments(studentId);
        List<ScheduleSlotResponse> schedule = getSchedule(studentId);
        List<RoomResponse> rooms = getRooms(studentId);

        // Calculate statistics
        StudentDashboardSummary.StudentStatistics statistics = calculateStatistics(
            enrollments, grades, absences
        );

        return StudentDashboardSummary.builder()
                .profile(profile)
                .grades(grades)
                .absences(absences)
                .enrollments(enrollments)
                .schedule(schedule)
                .rooms(rooms)
                .statistics(statistics)
                .build();
    }

    private StudentDashboardSummary.StudentStatistics calculateStatistics(
            List<EnrollmentResponse> enrollments,
            List<StudentGradeResponse> grades,
            List<StudentAbsenceResponse> absences) {

        int totalEnrollments = enrollments.size();
        int activeEnrollments = (int) enrollments.stream().filter(e -> Boolean.TRUE.equals(e.getActive())).count();

        int totalGrades = grades.size();
        Double averageGrade = grades.isEmpty() ? 0.0 :
                grades.stream()
                        .mapToDouble(g -> g.getValue() != null ? g.getValue() : 0.0)
                        .average()
                        .orElse(0.0);

        int totalAbsences = absences.size();
        long presentDays = absences.stream()
                .filter(a -> a.getStatus() == AbsenceStatus.PRESENT)
                .count();
        long lateDays = absences.stream()
                .filter(a -> a.getStatus() == AbsenceStatus.LATE)
                .count();
        long excusedAbsences = absences.stream()
                .filter(a -> a.getStatus() == AbsenceStatus.EXCUSED)
                .count();
        long unexcusedAbsences = absences.stream()
                .filter(a -> a.getStatus() == AbsenceStatus.ABSENT)
                .count();

        double attendanceRate = totalAbsences == 0 ? 100.0 :
                ((presentDays + lateDays) * 100.0) / totalAbsences;

        return StudentDashboardSummary.StudentStatistics.builder()
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .totalGrades(totalGrades)
                .averageGrade(Math.round(averageGrade * 100.0) / 100.0)
                .totalAbsences(totalAbsences)
                .presentDays((int) presentDays)
                .lateDays((int) lateDays)
                .excusedAbsences((int) excusedAbsences)
                .unexcusedAbsences((int) unexcusedAbsences)
                .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0)
                .build();
    }

    private ScheduleSlotResponse toScheduleResponse(ScheduleSlot slot) {
        return ScheduleSlotResponse.builder()
                .id(slot.getId())
                .dayOfWeek(slot.getDayOfWeek())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .roomId(slot.getRoom().getId())
                .roomName(slot.getRoom().getName())
                .courseId(slot.getCourse().getId())
                .courseTitle(slot.getCourse().getTitle())
                .teacherId(slot.getTeacher().getId())
                .teacherName(slot.getTeacher().getPrenom() + " " + slot.getTeacher().getNom())
                .build();
    }

    private RoomResponse toRoomResponse(Room room) {
        String teacherId = null;
        String teacherName = null;
        if (room.getTeacher() != null) {
            Teacher t = room.getTeacher();
            teacherId = t.getId();
            teacherName = (t.getPrenom() != null ? t.getPrenom() : "") + " " +
                    (t.getNom() != null ? t.getNom() : "");
            teacherName = teacherName.trim();
        }
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .section(room.getSection())
                .capacity(room.getCapacity())
                .teacherId(teacherId)
                .teacherName(teacherName)
                .build();
    }
}
