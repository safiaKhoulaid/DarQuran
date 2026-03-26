package com.darquran.streams;

import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.course.Lesson;
import com.darquran.domain.model.entities.live.LiveComment;
import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.entities.school.Enrollment;
import com.darquran.domain.model.entities.school.StudentGrade;
import com.darquran.domain.model.entities.users.Admin;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.Teacher;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.enums.Section;
import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.model.valueobjects.Password;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StreamExercisesTest {

    @Test
    void should_build_fake_datasets_for_stream_exercises() {
        List<User> users = fakeUsers();
        List<Course> courses = fakeCourses();
        List<Enrollment> enrollments = fakeEnrollments(courses, users);
        List<StudentGrade> grades = fakeStudentGrades(courses, users);
        List<LiveSession> liveSessions = fakeLiveSessions(users);

        assertThat(users).hasSize(6);
        assertThat(courses).hasSize(3);
        assertThat(enrollments).hasSize(5);
        assertThat(grades).hasSize(5);
        assertThat(liveSessions).hasSize(3);
    }

    static List<User> fakeUsers() {
        LocalDateTime now = LocalDateTime.now();

        Student student1 = Student.builder()
                .id("stu-1")
                .nom("Alaoui")
                .prenom("Yassine")
                .email("yassine@darquran.ma")
                .password(Password.fromHash("$2a$10$fakehash1"))
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2010, 5, 10))
                .createdAt(now.minusDays(10))
                .build();

        Student student2 = Student.builder()
                .id("stu-2")
                .nom("Bennani")
                .prenom("Aya")
                .email("aya@darquran.ma")
                .password(Password.fromHash("$2a$10$fakehash2"))
                .role(Role.ELEVE)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(2011, 8, 21))
                .createdAt(now.minusDays(7))
                .build();

        Student student3 = Student.builder()
                .id("stu-3")
                .nom("Idrissi")
                .prenom("Hamza")
                .email("hamza@darquran.ma")
                .password(Password.fromHash("$2a$10$fakehash3"))
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2009, 2, 12))
                .createdAt(now.minusDays(5))
                .build();

        Teacher teacher1 = Teacher.builder()
                .id("tea-1")
                .nom("Karimi")
                .prenom("Bilal")
                .email("bilal.teacher@darquran.ma")
                .password(Password.fromHash("$2a$10$fakehash4"))
                .role(Role.ENSEIGNANT)
                .section(Section.HOMME)
                .createdAt(now.minusDays(20))
                .build();

        Teacher teacher2 = Teacher.builder()
                .id("tea-2")
                .nom("Zahraoui")
                .prenom("Salma")
                .email("salma.teacher@darquran.ma")
                .password(Password.fromHash("$2a$10$fakehash5"))
                .role(Role.ENSEIGNANT)
                .section(Section.FEMME)
                .createdAt(now.minusDays(18))
                .build();

        Admin admin = Admin.builder()
                .id("adm-1")
                .nom("Amrani")
                .prenom("Omar")
                .email("admin@darquran.ma")
                .password(Password.fromHash("$2a$10$fakehash6"))
                .role(Role.ADMIN_SECTION)
                .section(Section.HOMME)
                .createdAt(now.minusDays(30))
                .build();

        return List.of(student1, student2, student3, teacher1, teacher2, admin);
    }

    static List<Course> fakeCourses() {
        Course tajwid = Course.builder()
                .id("course-1")
                .title("Tajwid Debutant")
                .slug("tajwid-debutant")
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.BEGINNER)
                .createdAt(LocalDateTime.now().minusDays(40))
                .build();
        tajwid.setPublic(true);

        Course hifz = Course.builder()
                .id("course-2")
                .title("Hifz Intermediaire")
                .slug("hifz-intermediaire")
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.INTERMEDIATE)
                .createdAt(LocalDateTime.now().minusDays(35))
                .build();
        hifz.setPublic(true);

        Course fiqh = Course.builder()
                .id("course-3")
                .title("Fiqh Avance")
                .slug("fiqh-avance")
                .status(CourseStatus.DRAFT)
                .level(CourseLevel.ADVANCED)
                .createdAt(LocalDateTime.now().minusDays(15))
                .build();
        fiqh.setPublic(false);

        tajwid.setLessons(new ArrayList<>(List.of(
                Lesson.builder().id("les-1").title("Makharij").orderIndex(1).course(tajwid).build(),
                Lesson.builder().id("les-2").title("Sifat").orderIndex(2).course(tajwid).build()
        )));

        hifz.setLessons(new ArrayList<>(List.of(
                Lesson.builder().id("les-3").title("Revision Juz Amma").orderIndex(1).course(hifz).build(),
                Lesson.builder().id("les-4").title("Memorisation guidee").orderIndex(2).course(hifz).build(),
                Lesson.builder().id("les-5").title("Suivi hebdo").orderIndex(3).course(hifz).build()
        )));

        fiqh.setLessons(new ArrayList<>(List.of(
                Lesson.builder().id("les-6").title("Usul al-fiqh").orderIndex(1).course(fiqh).build()
        )));

        return List.of(tajwid, hifz, fiqh);
    }

    static List<Enrollment> fakeEnrollments(List<Course> courses, List<User> users) {
        Student s1 = (Student) users.get(0);
        Student s2 = (Student) users.get(1);
        Student s3 = (Student) users.get(2);

        return List.of(
                Enrollment.builder().id("enr-1").student(s1).course(courses.get(0)).enrolledAt(LocalDateTime.now().minusDays(12)).active(true).build(),
                Enrollment.builder().id("enr-2").student(s2).course(courses.get(0)).enrolledAt(LocalDateTime.now().minusDays(11)).active(true).build(),
                Enrollment.builder().id("enr-3").student(s3).course(courses.get(1)).enrolledAt(LocalDateTime.now().minusDays(10)).active(true).build(),
                Enrollment.builder().id("enr-4").student(s1).course(courses.get(1)).enrolledAt(LocalDateTime.now().minusDays(9)).active(false).build(),
                Enrollment.builder().id("enr-5").student(s2).course(courses.get(2)).enrolledAt(LocalDateTime.now().minusDays(3)).active(true).build()
        );
    }

    static List<StudentGrade> fakeStudentGrades(List<Course> courses, List<User> users) {
        Student s1 = (Student) users.get(0);
        Student s2 = (Student) users.get(1);
        Student s3 = (Student) users.get(2);
        Teacher t1 = (Teacher) users.get(3);
        Teacher t2 = (Teacher) users.get(4);

        return List.of(
                StudentGrade.builder().id("gr-1").student(s1).teacher(t1).course(courses.get(0)).value(14.5).gradeDate(LocalDate.now().minusDays(8)).createdAt(LocalDateTime.now().minusDays(8)).build(),
                StudentGrade.builder().id("gr-2").student(s2).teacher(t1).course(courses.get(0)).value(16.0).gradeDate(LocalDate.now().minusDays(8)).createdAt(LocalDateTime.now().minusDays(8)).build(),
                StudentGrade.builder().id("gr-3").student(s3).teacher(t2).course(courses.get(1)).value(11.0).gradeDate(LocalDate.now().minusDays(6)).createdAt(LocalDateTime.now().minusDays(6)).build(),
                StudentGrade.builder().id("gr-4").student(s1).teacher(t2).course(courses.get(1)).value(9.5).gradeDate(LocalDate.now().minusDays(6)).createdAt(LocalDateTime.now().minusDays(6)).build(),
                StudentGrade.builder().id("gr-5").student(s2).teacher(t1).course(courses.get(2)).value(13.0).gradeDate(LocalDate.now().minusDays(2)).createdAt(LocalDateTime.now().minusDays(2)).build()
        );
    }

    static List<LiveSession> fakeLiveSessions(List<User> users) {
        Teacher teacherHomme = (Teacher) users.get(3);
        Teacher teacherFemme = (Teacher) users.get(4);

        LiveSession scheduled = LiveSession.builder()
                .id("live-1")
                .title("Live Tajwid Homme")
                .streamKey("tajwid-h-001")
                .status(LiveSessionStatus.SCHEDULED)
                .accessType(LiveAccessType.INTERNAL)
                .section(Section.HOMME)
                .scheduledStartAt(LocalDateTime.now().plusHours(2))
                .user(teacherHomme)
                .build();

        LiveSession live = LiveSession.builder()
                .id("live-2")
                .title("Live Hifz Femme")
                .streamKey("hifz-f-001")
                .status(LiveSessionStatus.LIVE)
                .accessType(LiveAccessType.INTERNAL)
                .section(Section.FEMME)
                .scheduledStartAt(LocalDateTime.now().minusMinutes(20))
                .startedAt(LocalDateTime.now().minusMinutes(15))
                .user(teacherFemme)
                .build();

        LiveSession ended = LiveSession.builder()
                .id("live-3")
                .title("Live Fiqh Public")
                .streamKey("fiqh-pub-001")
                .status(LiveSessionStatus.ENDED)
                .accessType(LiveAccessType.EXTERNAL)
                .section(Section.HOMME)
                .scheduledStartAt(LocalDateTime.now().minusDays(1))
                .startedAt(LocalDateTime.now().minusDays(1).plusMinutes(5))
                .endedAt(LocalDateTime.now().minusDays(1).plusHours(1))
                .user(teacherHomme)
                .build();

        live.setComments(new ArrayList<>(List.of(
                LiveComment.builder().id("com-1").content("BarakAllahou fikom").liveSession(live).author((Student) users.get(1)).createdAt(LocalDateTime.now().minusMinutes(10)).build(),
                LiveComment.builder().id("com-2").content("Son est clair").liveSession(live).authorDisplayName("Visiteur public").createdAt(LocalDateTime.now().minusMinutes(8)).build()
        )));

        return List.of(scheduled, live, ended);
    }

    @Test
    void ex1() {
        List<User> users = fakeUsers();

        List<String> emails = users
                .stream()
                .map(user -> user.getEmail().toLowerCase())
                .sorted()
                .toList();

        assertThat(emails).isNotEmpty();
        assertThat(emails).isSorted();
    }

    @Test
    void ex2() {
        List<Course> courses = fakeCourses();

        List<String> titles = courses.
                stream()
                .filter(Course::isPublic)
                .map(c -> c.getTitle())
                .toList();
        System.out.println(titles);
        assertThat(titles.size()).isEqualTo(2);
    }

    @Test
    void ex3() {
        List<Enrollment> enrollements = fakeEnrollments(fakeCourses(), fakeUsers());

        long total_active_enrollement = enrollements
                .stream()
                .filter(e -> e.getActive().equals(true))
                .count();
        ;
        assertThat(total_active_enrollement).isEqualTo(4);
    }

    @Test
    void ex4() {
        List<User> users = fakeUsers();
        List<User> last_five_users = users.stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .limit(5)
                .toList();

        assertThat(last_five_users.size()).isEqualTo(5);
    }
}
