package com.darquran.application.service.impl;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.schedule.ScheduleSlotResponse;
import com.darquran.application.mapper.absence.StudentAbsenceMapper;
import com.darquran.application.mapper.grade.StudentGradeMapper;
import com.darquran.application.service.CourseService;
import com.darquran.application.service.RoomService;
import com.darquran.application.service.ScheduleService;
import com.darquran.application.service.StudentAbsenceService;
import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.school.Room;
import com.darquran.domain.model.entities.school.ScheduleSlot;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.enums.AbsenceStatus;
import com.darquran.domain.repository.EnrollmentRepository;
import com.darquran.domain.repository.RoomRepository;
import com.darquran.domain.repository.ScheduleSlotRepository;
import com.darquran.domain.repository.StudentAbsenceRepository;
import com.darquran.domain.repository.StudentGradeRepository;
import com.darquran.domain.repository.StudentRepository;
import com.darquran.domain.repository.TeacherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherDashboardServiceImplTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomService roomService;
    @Mock
    private ScheduleSlotRepository scheduleSlotRepository;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentAbsenceService studentAbsenceService;
    @Mock
    private StudentAbsenceRepository studentAbsenceRepository;
    @Mock
    private StudentAbsenceMapper studentAbsenceMapper;
    @Mock
    private StudentGradeRepository studentGradeRepository;
    @Mock
    private StudentGradeMapper studentGradeMapper;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private CourseService courseService;

    @InjectMocks
    private TeacherDashboardServiceImpl service;

    @Test
    @DisplayName("getStudentsByClass : aucun créneau pour ce prof dans la salle → liste vide")
    void getStudentsByClass_noSlots_returnsEmpty() {
        when(scheduleSlotRepository.findByRoomIdAndTeacherId("r1", "t1")).thenReturn(List.of());
        assertThat(service.getStudentsByClass("r1", "t1")).isEmpty();
        verify(enrollmentRepository, never()).findByCourseIdIn(any());
    }

    @Test
    @DisplayName("markAbsence : créneau d'un autre prof → IllegalArgumentException")
    void markAbsence_wrongTeacher_throws() {
        Teacher other = new Teacher();
        other.setId("other");
        ScheduleSlot slot = ScheduleSlot.builder()
                .id("slot1")
                .teacher(other)
                .dayOfWeek(1)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 0))
                .room(Room.builder().id("r1").name("A").build())
                .course(Course.builder().id("c1").title("Quran").build())
                .build();
        when(scheduleSlotRepository.findById("slot1")).thenReturn(Optional.of(slot));

        StudentAbsenceRequest req = StudentAbsenceRequest.builder()
                .studentId("s1")
                .scheduleSlotId("slot1")
                .date(LocalDate.now())
                .status(AbsenceStatus.ABSENT)
                .build();

        assertThatThrownBy(() -> service.markAbsence("t1", req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("propres créneaux");
        verify(studentAbsenceService, never()).markAbsence(any());
    }

    @Test
    @DisplayName("getMyClasses : fusionne salles titulaires et salles des créneaux")
    void getMyClasses_mergesRoomIds() {
        Room r1 = Room.builder().id("room-a").name("A").build();
        Room r2 = Room.builder().id("room-b").name("B").build();
        when(roomRepository.findByTeacherId("t1")).thenReturn(List.of(r1));

        Teacher teacher = new Teacher();
        teacher.setId("t1");
        ScheduleSlot slot = ScheduleSlot.builder()
                .id("s1")
                .teacher(teacher)
                .dayOfWeek(1)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .room(r2)
                .course(Course.builder().id("c1").title("C").build())
                .build();
        when(scheduleSlotRepository.findByTeacherId("t1")).thenReturn(List.of(slot));

        when(roomService.getById("room-a")).thenReturn(RoomResponse.builder().id("room-a").name("A").build());
        when(roomService.getById("room-b")).thenReturn(RoomResponse.builder().id("room-b").name("B").build());

        List<RoomResponse> classes = service.getMyClasses("t1");

        assertThat(classes).extracting(RoomResponse::getId).containsExactly("room-a", "room-b");
    }

    @Test
    @DisplayName("getMyScheduleSlots délègue au ScheduleService")
    void getMyScheduleSlots_delegates() {
        List<ScheduleSlotResponse> expected = List.of(
                ScheduleSlotResponse.builder().id("1").dayOfWeek(1).build());
        when(scheduleService.getByTeacher("t1")).thenReturn(expected);
        assertThat(service.getMyScheduleSlots("t1")).isEqualTo(expected);
    }
}
