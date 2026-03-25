package com.darquran.application.service.impl;

import com.darquran.application.dto.users.student.StudentRequest;
import com.darquran.application.dto.users.student.StudentResponse;
import com.darquran.application.mapper.users.student.StudentMapper;
import com.darquran.application.service.StudentService;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.valueobjects.Adresse;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.EnrollmentRepository;
import com.darquran.domain.repository.LiveCommentRepository;
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
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAbsenceRepository studentAbsenceRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final LiveCommentRepository liveCommentRepository;

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            student.setPassword(Password.create(request.getPassword(), passwordEncoder));
        } else {
            throw new IllegalArgumentException("Le mot de passe est requis pour créer un élève.");
        }

        student.setRole(Role.ELEVE);

        // Adresse par défaut (peut être enrichie plus tard via un module dédié)
        student.setAdresse(new Adresse("Rue par défaut", "Ville", "10000", "Maroc"));

        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Override
    public StudentResponse getById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable avec l'id : " + id));
        return studentMapper.toResponse(student);
    }

    @Override
    public List<StudentResponse> getAllBySection(Section section) {
        List<Student> students;
        if (section == null) {
            students = studentRepository.findAll();
        } else {
            students = studentRepository.findBySection(section);
        }
        return students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentResponse update(String id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable avec l'id : " + id));

        studentMapper.updateEntityFromRequest(request, student);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            student.setPassword(Password.create(request.getPassword(), passwordEncoder));
        }

        // On s'assure que le rôle reste ELEVE
        student.setRole(Role.ELEVE);

        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Élève introuvable avec l'id : " + id);
        }

        // Nettoyage des dépendances pour éviter les erreurs FK.
        enrollmentRepository.deleteAll(enrollmentRepository.findByStudentId(id));
        studentAbsenceRepository.deleteAll(studentAbsenceRepository.findByStudentId(id));
        studentGradeRepository.deleteAll(studentGradeRepository.findByStudentId(id));
        liveCommentRepository.deleteAll(liveCommentRepository.findByAuthorId(id));

        studentRepository.deleteById(id);
    }
}

