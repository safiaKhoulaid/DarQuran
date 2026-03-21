package com.darquran.domain.repository;

import com.darquran.domain.model.entities.school.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {

    List<Room> findByTeacherId(String teacherId);
}

