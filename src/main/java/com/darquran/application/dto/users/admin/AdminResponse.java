package com.darquran.application.dto.users.admin;

import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Section section;
    private LocalDate dateNaissance;
    private String photoUrl;
    private LocalDateTime createdAt;
    private Role role;
}

