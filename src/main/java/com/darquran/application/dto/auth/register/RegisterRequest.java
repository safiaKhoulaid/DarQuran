package com.darquran.application.dto.auth.login.register;

import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String telephone;
    private Role role;
    private Section section;
}