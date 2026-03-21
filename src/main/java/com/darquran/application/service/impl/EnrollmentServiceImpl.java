package com.darquran.application.service.impl;

import com.darquran.application.dto.enrollment.EnrollmentRequest;
import com.darquran.application.dto.enrollment.EnrollmentResponse;
import com.darquran.application.mapper.enrollment.EnrollmentMapper;
import com.darquran.application.service.EnrollmentService;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.school.Enrollment;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.repository.CourseRepository;
import com.darquran.domain.repository.EnrollmentRepository;
import com.darquran.domain.repository.StudentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        if (enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId()).isPresent()) {
            throw new EntityExistsException("L'élève est déjà inscrit à ce cours.");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable avec l'id : " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable avec l'id : " + request.getCourseId()));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .active(true)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void unenroll(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Inscription introuvable avec l'id : " + enrollmentId));

        enrollment.setActive(false);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getByStudent(String studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getByCourse(String courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}

