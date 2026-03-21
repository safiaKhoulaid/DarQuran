package com.darquran.application.service.impl;

import com.darquran.application.dto.users.teacher.TeacherRequest;
import com.darquran.application.dto.users.teacher.TeacherResponse;
import com.darquran.application.mapper.users.teacher.TeacherMapper;
import com.darquran.application.service.TeacherService;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.valueobjects.Adresse;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        Teacher teacher = teacherMapper.toEntity(request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            teacher.setPassword(Password.create(request.getPassword(), passwordEncoder));
        } else {
            throw new IllegalArgumentException("Le mot de passe est requis pour créer un enseignant.");
        }

        teacher.setRole(Role.ENSEIGNANT);

        // Adresse par défaut (peut être enrichie plus tard via un module dédié)
        teacher.setAdresse(new Adresse("Rue par défaut", "Ville", "10000", "Maroc"));

        Teacher saved = teacherRepository.save(teacher);
        return teacherMapper.toResponse(saved);
    }

    @Override
    public TeacherResponse getById(String id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant introuvable avec l'id : " + id));
        return teacherMapper.toResponse(teacher);
    }

    @Override
    public List<TeacherResponse> getAllBySection(Section section) {
        List<Teacher> teachers;
        if (section == null) {
            teachers = teacherRepository.findAll();
        } else {
            teachers = teacherRepository.findBySection(section);
        }
        return teachers.stream()
                .map(teacherMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeacherResponse update(String id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant introuvable avec l'id : " + id));

        teacherMapper.updateEntityFromRequest(request, teacher);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            teacher.setPassword(Password.create(request.getPassword(), passwordEncoder));
        }

        // On s'assure que le rôle reste ENSEIGNANT
        teacher.setRole(Role.ENSEIGNANT);

        Teacher saved = teacherRepository.save(teacher);
        return teacherMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Enseignant introuvable avec l'id : " + id);
        }
        teacherRepository.deleteById(id);
    }
}

