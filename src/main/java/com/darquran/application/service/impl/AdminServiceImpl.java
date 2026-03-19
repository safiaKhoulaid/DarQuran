package com.darquran.application.service.impl;

import com.darquran.application.dto.users.admin.AdminRequest;
import com.darquran.application.dto.users.admin.AdminResponse;
import com.darquran.application.mapper.users.admin.AdminMapper;
import com.darquran.application.service.AdminService;
import com.darquran.application.service.EmailService;
import com.darquran.domain.model.entities.users.Admin;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public AdminResponse create(AdminRequest request) {
        Admin admin = adminMapper.toEntity(request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassword(Password.create(request.getPassword(), passwordEncoder));
        } else {
            throw new IllegalArgumentException("Le mot de passe est requis pour créer un administrateur.");
        }

        admin.setRole(Role.ADMIN_SECTION);

        Admin saved = adminRepository.save(admin);

        // Envoi d'un email de bienvenue en arabe avec ses identifiants
        String subject = "مرحبا بك كمسؤول في دار القرآن";
        String body = """
                السلام عليكم ورحمة الله وبركاته،

                تم إنشاء حساب مسؤول (أدمن) لك في نظام إدارة معهد دار القرآن.

                تفاصيل حسابك:
                البريد الإلكتروني: %s
                كلمة المرور: %s

                ننصحك بتغيير كلمة المرور بعد أول تسجيل دخول.

                بارك الله في جهودك.
                إدارة المعهد.
                """.formatted(saved.getEmail(), request.getPassword());

        emailService.sendEmail(saved.getEmail(), subject, body);

        return adminMapper.toResponse(saved);
    }

    @Override
    public AdminResponse getById(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable avec l'id : " + id));
        return adminMapper.toResponse(admin);
    }

    @Override
    public List<AdminResponse> getAllBySection(Section section) {
        List<Admin> admins;
        if (section == null) {
            admins = adminRepository.findAll();
        } else {
            admins = adminRepository.findBySection(section);
        }
        return admins.stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminResponse update(String id, AdminRequest request) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable avec l'id : " + id));

        adminMapper.updateEntityFromRequest(request, admin);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassword(Password.create(request.getPassword(), passwordEncoder));
        }

        // On s'assure que le rôle reste ADMIN_SECTION
        admin.setRole(Role.ADMIN_SECTION);

        Admin saved = adminRepository.save(admin);
        return adminMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin introuvable avec l'id : " + id);
        }
        adminRepository.deleteById(id);
    }
}

