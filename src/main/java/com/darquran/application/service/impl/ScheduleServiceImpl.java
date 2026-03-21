package com.darquran.application.service.impl;

import com.darquran.application.dto.schedule.ScheduleSlotRequest;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.service.ScheduleService;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.school.Room;
import com.darquran.domain.model.entities.school.ScheduleSlot;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.repository.CourseRepository;
import com.darquran.domain.repository.RoomRepository;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleSlotRepository scheduleSlotRepository;
    private final RoomRepository roomRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    public ScheduleSlotResponse create(ScheduleSlotRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + request.getRoomId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + request.getCourseId()));
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found: " + request.getTeacherId()));

        ScheduleSlot slot = ScheduleSlot.builder()
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(room)
                .course(course)
                .teacher(teacher)
                .build();

        slot = scheduleSlotRepository.save(slot);
        return toResponse(slot);
    }

    @Override
    public ScheduleSlotResponse getById(String id) {
        return scheduleSlotRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found: " + id));
    }

    @Override
    public List<ScheduleSlotResponse> getAll() {
        return scheduleSlotRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ScheduleSlotResponse> getByRoom(String roomId) {
        return scheduleSlotRepository.findByRoomId(roomId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ScheduleSlotResponse> getByCourse(String courseId) {
        return scheduleSlotRepository.findByCourseId(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ScheduleSlotResponse> getByTeacher(String teacherId) {
        return scheduleSlotRepository.findByTeacherId(teacherId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!scheduleSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("Slot not found: " + id);
        }
        scheduleSlotRepository.deleteById(id);
    }

    private ScheduleSlotResponse toResponse(ScheduleSlot slot) {
        return ScheduleSlotResponse.builder()
                .id(slot.getId())
                .dayOfWeek(slot.getDayOfWeek())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .roomId(slot.getRoom().getId())
                .roomName(slot.getRoom().getName())
                .courseId(slot.getCourse().getId())
                .courseTitle(slot.getCourse().getTitle())
                .teacherId(slot.getTeacher().getId())
                .teacherName(slot.getTeacher().getPrenom() + " " + slot.getTeacher().getNom())
                .build();
    }
}

