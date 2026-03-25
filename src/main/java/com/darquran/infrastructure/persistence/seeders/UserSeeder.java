package com.darquran.infrastructure.persistence.seeders;

import com.darquran.domain.model.entities.users.SuperAdmin;
import com.darquran.domain.model.entities.users.Admin;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.valueobjects.Adresse;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Legacy seeder - disabled in favor of DatabaseSeeder.
 * Use DatabaseSeeder with @Profile("dev") for comprehensive seeding.
 */
@Component
@RequiredArgsConstructor
@Profile("legacy-seed")
public class UserSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final TeacherRepository enseignantRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuperAdminRepository superAdminRepository;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 Seeder Start: Ana dkhlt l method run!");
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("🌱 Démarrage du Seeding des données...");

        // --- Helper Variables ---
        // Mot de passe Fort (Respectant le Regex: Majuscule, Chiffre, Min 8 chars)
        String passCommun = "Pass123!";

        SuperAdmin superAdmin = SuperAdmin.builder()
                .prenom("Safia")
                .nom("Khoulaid")
                .email("safia@superadmin.com")
                .password(Password.create(passCommun, passwordEncoder))
                .adresse(createAdresse("Casablanca"))
                .telephone("0600000001")
                .role(Role.SUPER_ADMIN)
                .dateNaissance(LocalDate.of(2000, 5, 20))
                .section(Section.FEMME)
                .build();
        superAdminRepository.save(superAdmin);

        Admin adminHomme = Admin.builder()
                .prenom("Nafia")
                .nom("Akdi")
                .email("nafia@admin.com")
                .password(Password.create(passCommun, passwordEncoder))
                .adresse(createAdresse("Rabat"))
                .telephone("0600000002")
                .role(Role.ADMIN_SECTION)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1996, 5, 20))
                .build();
        adminRepository.save(adminHomme);

        // 4. ENSEIGNANT (Section Homme)
        Teacher prof = Teacher.builder()
                .prenom("Ahmed")
                .nom("Elfassi")
                .email("ahmed@prof.com")
                .password(Password.create(passCommun, passwordEncoder))
                .adresse(createAdresse("Fes"))
                .telephone("0600000003")
                .role(Role.ENSEIGNANT)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1995, 5, 20))
                .build();
        enseignantRepository.save(prof);


        // 6. ELEVE (Lié au Parent)
        Student eleve = Student.builder()
                .prenom("Omar")
                .nom("Boussifha")
                .email("omar@student.com")
                .password(Password.create(passCommun, passwordEncoder))
                .adresse(createAdresse("Tanger")) // Nafs adresse d bah
                .telephone("0600000005")
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2015, 5, 20))
                .build();
        studentRepository.save(eleve);

        System.out.println("✅ Seeding terminé avec succès !");
    }

    // Helper method bach manb9awch n3awdo new Adresse(...)
    private Adresse createAdresse(String ville) {
        return Adresse.builder()
                .rue("Centre Ville")
                .codePostal("10000")
                .ville(ville)
                .pays("Maroc")
                .build();
    }
}