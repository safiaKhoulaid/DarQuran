package com.darquran.domain.repository;

import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.enums.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student , String> {

    List<Student> findBySection(Section section);
}
