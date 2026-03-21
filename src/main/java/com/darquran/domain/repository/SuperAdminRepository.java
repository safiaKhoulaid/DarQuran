package com.darquran.domain.repository;

import com.darquran.domain.model.entities.users.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, String> {
}
