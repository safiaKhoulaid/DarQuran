package com.darquran.domain.repository;

import com.darquran.domain.model.entities.users.Admin;
import com.darquran.domain.model.enums.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, String> {

    List<Admin> findAllBySection(Section section);
}
