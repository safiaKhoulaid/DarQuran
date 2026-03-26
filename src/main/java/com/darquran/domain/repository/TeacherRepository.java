package com.darquran.domain.repository;

import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, String> {

    Page<List<Teacher>> findBySection(Section section );
}