package com.darquran.application.service.impl;

import com.darquran.application.dto.room.RoomRequest;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.service.RoomService;
import com.darquran.domain.model.entities.school.Room;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.repository.RoomRepository;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.StudentAbsenceRepository;
import com.darquran.domain.repository.TeacherAbsenceRepository;
import com.darquran.domain.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final StudentAbsenceRepository studentAbsenceRepository;
    private final TeacherAbsenceRepository teacherAbsenceRepository;

    @Override
    @Transactional
    public RoomResponse create(RoomRequest request) {
        Room room = Room.builder()
                .name(request.getName())
                .section(request.getSection())
                .capacity(request.getCapacity())
                .build();
        room = roomRepository.save(room);
        assignTeacherIfValid(room, request.getTeacherId());
        room = roomRepository.save(room);
        return toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getById(String id) {
        return roomRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAll() {
        return roomRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoomResponse update(String id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + id));
        if (request.getName() != null && !request.getName().isBlank()) {
            room.setName(request.getName());
        }
        if (request.getSection() != null) {
            room.setSection(request.getSection());
        }
        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }
        // En update:
        // - teacherId absent (null) => ne pas toucher l'affectation actuelle
        // - teacherId vide => retirer l'enseignant
        // - teacherId renseigné => affecter si section compatible
        if (request.getTeacherId() != null) {
            assignTeacherIfValid(room, request.getTeacherId());
        }
        room = roomRepository.save(room);
        return toResponse(room);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found: " + id);
        }

        var slots = scheduleSlotRepository.findByRoomId(id);
        if (!slots.isEmpty()) {
            var slotIds = slots.stream().map(s -> s.getId()).toList();
            studentAbsenceRepository.deleteAll(studentAbsenceRepository.findByScheduleSlotIdIn(slotIds));
            slotIds.forEach(slotId -> teacherAbsenceRepository.deleteAll(teacherAbsenceRepository.findByScheduleSlotId(slotId)));
            scheduleSlotRepository.deleteAll(slots);
        }

        roomRepository.deleteById(id);
    }

    /**
     * Affecte un enseignant à la salle uniquement si l'enseignant appartient à la même section.
     */
    private void assignTeacherIfValid(Room room, String teacherId) {
        if (teacherId == null || teacherId.isBlank()) {
            room.setTeacher(null);
            return;
        }
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            room.setTeacher(null);
            return;
        }
        if (teacher.getSection() != null && teacher.getSection().equals(room.getSection())) {
            room.setTeacher(teacher);
        } else {
            room.setTeacher(null);
        }
    }

    private RoomResponse toResponse(Room room) {
        String teacherId = null;
        String teacherName = null;
        if (room.getTeacher() != null) {
            Teacher t = room.getTeacher();
            teacherId = t.getId();
            teacherName = t.getPrenom() != null && t.getNom() != null
                    ? t.getPrenom() + " " + t.getNom()
                    : t.getEmail();
        }
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .section(room.getSection())
                .capacity(room.getCapacity())
                .teacherId(teacherId)
                .teacherName(teacherName)
                .build();
    }
}

