package com.darquran.application.service;

import com.darquran.application.dto.absence.StudentAbsenceRequest;
import com.darquran.application.dto.absence.StudentAbsenceResponse;
import com.darquran.application.dto.grade.StudentGradeRequest;
import com.darquran.application.dto.grade.StudentGradeResponse;
import com.darquran.application.dto.room.RoomResponse;
import com.darquran.application.dto.teacher.TeacherStudentResponse;
import com.darquran.application.dto.courses.CourseResponse;

import java.util.List;

public interface TeacherDashboardService {

    /** Classes (salles) où le professeur enseigne. */
    List<RoomResponse> getMyClasses(String teacherId);

    /** Élèves inscrits aux cours que le professeur enseigne dans cette salle. */
    List<TeacherStudentResponse> getStudentsByClass(String roomId, String teacherId);

    /** Cours que le professeur enseigne (distinct des créneaux). */
    List<CourseResponse> getMyCourses(String teacherId);

    /** Créneaux du professeur (pour sélectionner un créneau lors de la saisie d'absence). */
    List<com.darquran.application.dto.schedule.ScheduleSlotResponse> getMyScheduleSlots(String teacherId);

    /** Marquer une absence (vérifie que le créneau appartient au professeur). */
    StudentAbsenceResponse markAbsence(String teacherId, StudentAbsenceRequest request);

    /** Absences des élèves pour les créneaux du professeur dans cette salle. */
    List<StudentAbsenceResponse> getAbsencesByClass(String roomId, String teacherId);

    /** Ajouter une note (vérifie que le professeur enseigne le cours). */
    StudentGradeResponse addGrade(String teacherId, StudentGradeRequest request);

    /** Notes d'un cours (enseigné par ce professeur). */
    List<StudentGradeResponse> getGradesByCourse(String courseId, String teacherId);
}
