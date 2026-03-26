package com.darquran.infrastructure.persistence.seeders;

import com.darquran.domain.model.entities.course.Course;
import com.darquran.domain.model.entities.course.Lesson;
import com.darquran.domain.model.entities.course.Resource;
import com.darquran.domain.model.entities.live.LiveComment;
import com.darquran.domain.model.entities.live.LiveSession;
import com.darquran.domain.model.entities.school.*;
import com.darquran.domain.model.entities.users.*;
import com.darquran.domain.model.enums.*;
import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.model.enums.resources.ResourceType;
import com.darquran.domain.model.valueobjects.Adresse;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    private final SuperAdminRepository superAdminRepository;
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ResourceRepository resourceRepository;
    private final RoomRepository roomRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAbsenceRepository studentAbsenceRepository;
    private final TeacherAbsenceRepository teacherAbsenceRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final LiveSessionRepository liveSessionRepository;
    private final LiveCommentRepository liveCommentRepository;
    private final PasswordEncoder passwordEncoder;

    private final String DEFAULT_PASSWORD = "Pass123!";

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping...");
            return;
        }

        log.info("Starting database seeding...");

        List<SuperAdmin> superAdmins = seedSuperAdmins();
        List<Admin> admins = seedAdmins();
        List<Teacher> teachers = seedTeachers();
        List<Student> students = seedStudents();
        List<Course> courses = seedCourses();
        List<Lesson> lessons = seedLessons(courses);
        seedResources(lessons);
        List<Room> rooms = seedRooms(teachers);
        List<ScheduleSlot> scheduleSlots = seedScheduleSlots(rooms, courses, teachers);
        seedEnrollments(students, courses);
        seedStudentAbsences(students, scheduleSlots);
        seedTeacherAbsences(teachers, scheduleSlots);
        seedStudentGrades(students, courses, teachers);
        List<LiveSession> liveSessions = seedLiveSessions(teachers);
        seedLiveComments(liveSessions, students, teachers);

        log.info("Database seeding completed successfully!");
    }

    private List<SuperAdmin> seedSuperAdmins() {
        log.info("Seeding SuperAdmins...");
        List<SuperAdmin> superAdmins = new ArrayList<>();

        superAdmins.add(SuperAdmin.builder()
                .prenom("Safia")
                .nom("Khoulaid")
                .email("safia@superadmin.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("123 Rue Mohammed V", "Casablanca", "20000"))
                .telephone("0600000001")
                .role(Role.SUPER_ADMIN)
                .dateNaissance(LocalDate.of(1985, 3, 15))
                .section(Section.FEMME)
                .build());

        superAdmins.add(SuperAdmin.builder()
                .prenom("Youssef")
                .nom("Bennani")
                .email("youssef@superadmin.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("45 Avenue Hassan II", "Rabat", "10000"))
                .telephone("0600000002")
                .role(Role.SUPER_ADMIN)
                .dateNaissance(LocalDate.of(1980, 7, 22))
                .section(Section.HOMME)
                .build());

        return superAdminRepository.saveAll(superAdmins);
    }

    private List<Admin> seedAdmins() {
        log.info("Seeding Admins...");
        List<Admin> admins = new ArrayList<>();

        admins.add(Admin.builder()
                .prenom("Nafia")
                .nom("Akdi")
                .email("nafia@admin.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("78 Rue Ibn Sina", "Rabat", "10020"))
                .telephone("0611111001")
                .role(Role.ADMIN_SECTION)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1990, 5, 10))
                .build());

        admins.add(Admin.builder()
                .prenom("Fatima")
                .nom("Zahra")
                .email("fatima@admin.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("12 Rue Al Massira", "Casablanca", "20100"))
                .telephone("0611111002")
                .role(Role.ADMIN_SECTION)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(1988, 11, 25))
                .build());

        admins.add(Admin.builder()
                .prenom("Khalid")
                .nom("Mounir")
                .email("khalid@admin.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("34 Boulevard Zerktouni", "Marrakech", "40000"))
                .telephone("0611111003")
                .role(Role.ADMIN_SECTION)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1992, 2, 18))
                .build());

        return adminRepository.saveAll(admins);
    }

    private List<Teacher> seedTeachers() {
        log.info("Seeding Teachers...");
        List<Teacher> teachers = new ArrayList<>();

        teachers.add(Teacher.builder()
                .prenom("Ahmed")
                .nom("Elfassi")
                .email("ahmed@prof.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("56 Rue Fes Al Bali", "Fes", "30000"))
                .telephone("0622222001")
                .role(Role.ENSEIGNANT)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1982, 8, 5))
                .build());

        teachers.add(Teacher.builder()
                .prenom("Khadija")
                .nom("Benali")
                .email("khadija@prof.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("23 Rue Hassan II", "Tanger", "90000"))
                .telephone("0622222002")
                .role(Role.ENSEIGNANT)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(1987, 4, 12))
                .build());

        teachers.add(Teacher.builder()
                .prenom("Rachid")
                .nom("Alaoui")
                .email("rachid@prof.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("89 Avenue Mohammed VI", "Agadir", "80000"))
                .telephone("0622222003")
                .role(Role.ENSEIGNANT)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1979, 12, 30))
                .build());

        teachers.add(Teacher.builder()
                .prenom("Amina")
                .nom("Tazi")
                .email("amina@prof.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("67 Rue Moulay Ismail", "Meknes", "50000"))
                .telephone("0622222004")
                .role(Role.ENSEIGNANT)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(1991, 6, 8))
                .build());

        teachers.add(Teacher.builder()
                .prenom("Mohamed")
                .nom("Idrissi")
                .email("mohamed@prof.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("45 Rue Sidi Bennour", "El Jadida", "24000"))
                .telephone("0622222005")
                .role(Role.ENSEIGNANT)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(1984, 9, 17))
                .build());

        return teacherRepository.saveAll(teachers);
    }

    private List<Student> seedStudents() {
        log.info("Seeding Students...");
        List<Student> students = new ArrayList<>();

        students.add(Student.builder()
                .prenom("Omar")
                .nom("Boussifha")
                .email("omar@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("12 Rue Tanger", "Tanger", "90000"))
                .telephone("0633333001")
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2010, 3, 25))
                .build());

        students.add(Student.builder()
                .prenom("Salma")
                .nom("Lazrak")
                .email("salma@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("34 Rue Casablanca", "Casablanca", "20000"))
                .telephone("0633333002")
                .role(Role.ELEVE)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(2011, 7, 14))
                .build());

        students.add(Student.builder()
                .prenom("Yassine")
                .nom("Hajji")
                .email("yassine@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("56 Boulevard Zerktouni", "Rabat", "10000"))
                .telephone("0633333003")
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2009, 11, 3))
                .build());

        students.add(Student.builder()
                .prenom("Nadia")
                .nom("Cherkaoui")
                .email("nadia@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("78 Rue Fes", "Fes", "30000"))
                .telephone("0633333004")
                .role(Role.ELEVE)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(2012, 5, 20))
                .build());

        students.add(Student.builder()
                .prenom("Hassan")
                .nom("Moussaoui")
                .email("hassan@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("90 Avenue Mohammed V", "Marrakech", "40000"))
                .telephone("0633333005")
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2010, 8, 9))
                .build());

        students.add(Student.builder()
                .prenom("Imane")
                .nom("Filali")
                .email("imane@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("23 Rue Atlas", "Agadir", "80000"))
                .telephone("0633333006")
                .role(Role.ELEVE)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(2011, 1, 28))
                .build());

        students.add(Student.builder()
                .prenom("Ayoub")
                .nom("Saidi")
                .email("ayoub@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("45 Rue Oujda", "Oujda", "60000"))
                .telephone("0633333007")
                .role(Role.ELEVE)
                .section(Section.HOMME)
                .dateNaissance(LocalDate.of(2013, 4, 15))
                .build());

        students.add(Student.builder()
                .prenom("Laila")
                .nom("Kettani")
                .email("laila@student.com")
                .password(Password.create(DEFAULT_PASSWORD, passwordEncoder))
                .adresse(createAdresse("67 Boulevard Anfa", "Casablanca", "20200"))
                .telephone("0633333008")
                .role(Role.ELEVE)
                .section(Section.FEMME)
                .dateNaissance(LocalDate.of(2010, 10, 7))
                .build());

        return studentRepository.saveAll(students);
    }

    private List<Course> seedCourses() {
        log.info("Seeding Courses...");
        List<Course> courses = new ArrayList<>();

        courses.add(Course.builder()
                .title("التجويد - القواعد الأساسية")
                .slug("tajwid-regles-base")
                .description("تعلّم قواعد التجويد الأساسية لتحسين تلاوتك للقرآن الكريم بشكل صحيح ومنهجي.")
                .miniature("https://images.unsplash.com/photo-1609599006353-e629aaabfeae?auto=format&fit=crop&w=1200&q=80")
                .isPublic(true)
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.BEGINNER)
                .build());

        courses.add(Course.builder()
                .title("الحفظ - جزء عم")
                .slug("hifz-juz-amma")
                .description("برنامج متكامل لحفظ جزء عم مع خطة مراجعة يومية وتثبيت الحفظ.")
                .miniature("https://images.unsplash.com/photo-1519817650390-64a93db511aa?auto=format&fit=crop&w=1200&q=80")
                .isPublic(true)
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.BEGINNER)
                .build());

        courses.add(Course.builder()
                .title("التجويد المتقدم - أحكام النون الساكنة والتنوين")
                .slug("tajwid-avance-noon")
                .description("دراسة متقدمة لأحكام النون الساكنة والتنوين مع تطبيقات عملية وتمارين تلاوة.")
                .miniature("https://images.unsplash.com/photo-1542810634-71277d95dcbb?auto=format&fit=crop&w=1200&q=80")
                .isPublic(false)
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.INTERMEDIATE)
                .build());

        courses.add(Course.builder()
                .title("الحفظ - جزء تبارك")
                .slug("hifz-juz-tabarak")
                .description("خطة حفظ تدريجية لجزء تبارك مع ربط الآيات وفهم المعاني العامة.")
                .miniature("https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=1200&q=80")
                .isPublic(false)
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.INTERMEDIATE)
                .build());

        courses.add(Course.builder()
                .title("التجويد الاحترافي - مخارج الحروف")
                .slug("tajwid-expert-makhraj")
                .description("إتقان مخارج الحروف وصفاتها بدقة عالية لمن يريد الاحتراف في التلاوة.")
                .miniature("https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=1200&q=80")
                .isPublic(false)
                .status(CourseStatus.PUBLISHED)
                .level(CourseLevel.ADVANCED)
                .build());

        courses.add(Course.builder()
                .title("علوم القرآن - مدخل")
                .slug("sciences-coran-intro")
                .description("مدخل مبسّط إلى علوم القرآن: النزول، الجمع، التفسير، وأصول التعامل مع النص القرآني.")
                .miniature("https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=1200&q=80")
                .isPublic(true)
                .status(CourseStatus.DRAFT)
                .level(CourseLevel.BEGINNER)
                .build());

        courses.add(Course.builder()
                .title("قراءة ورش")
                .slug("lecture-warsh")
                .description("التعرّف على أصول قراءة ورش عن نافع وتطبيقاتها في التلاوة اليومية.")
                .miniature("https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=1200&q=80")
                .isPublic(false)
                .status(CourseStatus.ARCHIVED)
                .level(CourseLevel.ADVANCED)
                .build());

        return courseRepository.saveAll(courses);
    }

    private List<Lesson> seedLessons(List<Course> courses) {
        log.info("Seeding Lessons...");
        List<Lesson> lessons = new ArrayList<>();

        // Lessons for Tajwid Base course
        Course tajwidBase = courses.get(0);
        lessons.add(createLesson("مقدمة في علم التجويد", "التعرّف على معنى التجويد وأهميته في تحسين القراءة.", 1, tajwidBase));
        lessons.add(createLesson("الحروف الشمسية والقمرية", "التفريق بين الحروف الشمسية والقمرية مع أمثلة تطبيقية.", 2, tajwidBase));
        lessons.add(createLesson("الاستعاذة والبسملة", "كيفية البدء الصحيح بالتلاوة مع أحكام الاستعاذة والبسملة.", 3, tajwidBase));
        lessons.add(createLesson("علامات الوقف", "فهم علامات الوقف في المصحف وكيفية التعامل معها أثناء التلاوة.", 4, tajwidBase));

        // Lessons for Hifz Juz Amma
        Course hifzJuzAmma = courses.get(1);
        lessons.add(createLesson("سورة الفاتحة", "حفظ سورة الفاتحة مع ضبط مخارج الحروف وأحكام التلاوة.", 1, hifzJuzAmma));
        lessons.add(createLesson("سورة الناس", "حفظ سورة الناس مع شرح المعاني الإجمالية.", 2, hifzJuzAmma));
        lessons.add(createLesson("سورة الفلق", "حفظ سورة الفلق وتدريب عملي على التكرار الصحيح.", 3, hifzJuzAmma));
        lessons.add(createLesson("سورة الإخلاص", "حفظ سورة الإخلاص مع التنبيه على الأخطاء الشائعة.", 4, hifzJuzAmma));
        lessons.add(createLesson("سورة المسد", "حفظ سورة المسد وفهم سياقها العام.", 5, hifzJuzAmma));

        // Lessons for Tajwid Avance
        Course tajwidAvance = courses.get(2);
        lessons.add(createLesson("الإدغام بغنة", "أحكام الإدغام بغنة مع كلمات تدريبية من القرآن.", 1, tajwidAvance));
        lessons.add(createLesson("الإدغام بلا غنة", "تطبيقات الإدغام بلا غنة وأمثلة من السور القصيرة.", 2, tajwidAvance));
        lessons.add(createLesson("الإخفاء الحقيقي", "تعريف الإخفاء الحقيقي ومراتبه العملية.", 3, tajwidAvance));
        lessons.add(createLesson("الإقلاب", "مواضع الإقلاب وكيفية نطق النون ميماً مخفاة قبل الباء.", 4, tajwidAvance));
        lessons.add(createLesson("الإظهار الحلقي", "أحكام الإظهار الحلقي عند حروف الحلق الستة.", 5, tajwidAvance));

        // Lessons for Hifz Juz Tabarak
        Course hifzTabarak = courses.get(3);
        lessons.add(createLesson("سورة الملك (1-10)", "حفظ الآيات الأولى من سورة الملك مع مراجعة يومية.", 1, hifzTabarak));
        lessons.add(createLesson("سورة الملك (11-20)", "استكمال حفظ سورة الملك مع تثبيت المقاطع.", 2, hifzTabarak));
        lessons.add(createLesson("سورة الملك (21-30)", "إتمام السورة مع تقييم مستوى الضبط.", 3, hifzTabarak));
        lessons.add(createLesson("سورة القلم", "حفظ مقاطع مختارة من سورة القلم وفق خطة أسبوعية.", 4, hifzTabarak));

        // Lessons for Tajwid Expert
        Course tajwidExpert = courses.get(4);
        lessons.add(createLesson("مخرج الجوف", "شرح مخرج الجوف لحروف المد مع تمارين صوتية.", 1, tajwidExpert));
        lessons.add(createLesson("مخرج الحلق", "مخارج الحروف الحلقية وكيفية ضبطها.", 2, tajwidExpert));
        lessons.add(createLesson("مخرج اللسان", "تفصيل مخارج اللسان وأماكن خروج الحروف بدقة.", 3, tajwidExpert));
        lessons.add(createLesson("مخرج الشفتين", "تمارين عملية لحروف الشفتين وضبط التفخيم والترقيق.", 4, tajwidExpert));
        lessons.add(createLesson("مخرج الخيشوم", "التدريب على الغنة ومخرج الخيشوم في التلاوة.", 5, tajwidExpert));

        return lessonRepository.saveAll(lessons);
    }

    private void seedResources(List<Lesson> lessons) {
        log.info("Seeding Resources...");
        List<Resource> resources = new ArrayList<>();

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);

            // PDF resource with a real public file URL
            resources.add(Resource.builder()
                    .name("ملف الدرس - " + lesson.getTitle())
                    .fileUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                    .type(ResourceType.PDF)
                    .size(1024L * 500 + (i * 100))
                    .lesson(lesson)
                    .build());

            // Video resource with a real public file URL
            if (i % 2 == 0) {
                resources.add(Resource.builder()
                        .name("فيديو الشرح - " + lesson.getTitle())
                        .fileUrl("https://www.w3schools.com/html/mov_bbb.mp4")
                        .type(ResourceType.VIDEO)
                        .size(1024L * 1024 * 50 + (i * 1000))
                        .lesson(lesson)
                        .build());
            }

            // Text resource with a real public text file URL
            if (i % 3 == 0) {
                resources.add(Resource.builder()
                        .name("ملاحظات إضافية - " + lesson.getTitle())
                        .fileUrl("https://raw.githubusercontent.com/EbookFoundation/free-programming-books/main/books/free-programming-books-ar.md")
                        .type(ResourceType.TEXT)
                        .size(1024L * 10 + i)
                        .lesson(lesson)
                        .build());
            }
        }

        resourceRepository.saveAll(resources);
    }

    private List<Room> seedRooms(List<Teacher> teachers) {
        log.info("Seeding Rooms...");
        List<Room> rooms = new ArrayList<>();

        rooms.add(Room.builder()
                .name("Salle Al-Fatiha")
                .section(Section.HOMME)
                .capacity(25)
                .teacher(teachers.get(0))
                .build());

        rooms.add(Room.builder()
                .name("Salle Al-Baqara")
                .section(Section.HOMME)
                .capacity(30)
                .teacher(teachers.get(2))
                .build());

        rooms.add(Room.builder()
                .name("Salle Maryam")
                .section(Section.FEMME)
                .capacity(20)
                .teacher(teachers.get(1))
                .build());

        rooms.add(Room.builder()
                .name("Salle Al-Kahf")
                .section(Section.FEMME)
                .capacity(25)
                .teacher(teachers.get(3))
                .build());

        rooms.add(Room.builder()
                .name("Salle Yasin")
                .section(Section.HOMME)
                .capacity(35)
                .teacher(teachers.get(4))
                .build());

        rooms.add(Room.builder()
                .name("Salle An-Noor")
                .section(Section.FEMME)
                .capacity(20)
                .build());

        return roomRepository.saveAll(rooms);
    }

    private List<ScheduleSlot> seedScheduleSlots(List<Room> rooms, List<Course> courses, List<Teacher> teachers) {
        log.info("Seeding ScheduleSlots...");
        List<ScheduleSlot> slots = new ArrayList<>();

        // Monday slots
        slots.add(ScheduleSlot.builder()
                .dayOfWeek(1)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .room(rooms.get(0))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .build());

        slots.add(ScheduleSlot.builder()
                .dayOfWeek(1)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 30))
                .room(rooms.get(2))
                .course(courses.get(1))
                .teacher(teachers.get(1))
                .build());

        // Tuesday slots
        slots.add(ScheduleSlot.builder()
                .dayOfWeek(2)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 30))
                .room(rooms.get(1))
                .course(courses.get(2))
                .teacher(teachers.get(2))
                .build());

        slots.add(ScheduleSlot.builder()
                .dayOfWeek(2)
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(17, 30))
                .room(rooms.get(3))
                .course(courses.get(3))
                .teacher(teachers.get(3))
                .build());

        // Wednesday slots
        slots.add(ScheduleSlot.builder()
                .dayOfWeek(3)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .room(rooms.get(4))
                .course(courses.get(4))
                .teacher(teachers.get(4))
                .build());

        slots.add(ScheduleSlot.builder()
                .dayOfWeek(3)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 30))
                .room(rooms.get(0))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .build());

        // Thursday slots
        slots.add(ScheduleSlot.builder()
                .dayOfWeek(4)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 30))
                .room(rooms.get(2))
                .course(courses.get(1))
                .teacher(teachers.get(1))
                .build());

        slots.add(ScheduleSlot.builder()
                .dayOfWeek(4)
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(17, 30))
                .room(rooms.get(1))
                .course(courses.get(2))
                .teacher(teachers.get(2))
                .build());

        // Friday slots
        slots.add(ScheduleSlot.builder()
                .dayOfWeek(5)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .room(rooms.get(3))
                .course(courses.get(3))
                .teacher(teachers.get(3))
                .build());

        slots.add(ScheduleSlot.builder()
                .dayOfWeek(5)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 30))
                .room(rooms.get(4))
                .course(courses.get(4))
                .teacher(teachers.get(4))
                .build());

        // Saturday slots
        slots.add(ScheduleSlot.builder()
                .dayOfWeek(6)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .room(rooms.get(0))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .build());

        slots.add(ScheduleSlot.builder()
                .dayOfWeek(6)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .room(rooms.get(2))
                .course(courses.get(1))
                .teacher(teachers.get(1))
                .build());

        return scheduleSlotRepository.saveAll(slots);
    }

    private void seedEnrollments(List<Student> students, List<Course> courses) {
        log.info("Seeding Enrollments...");
        List<Enrollment> enrollments = new ArrayList<>();

        // Each student enrolled in 2-3 courses
        enrollments.add(Enrollment.builder().student(students.get(0)).course(courses.get(0)).active(true).enrolledAt(LocalDateTime.now().minusMonths(3)).build());
        enrollments.add(Enrollment.builder().student(students.get(0)).course(courses.get(1)).active(true).enrolledAt(LocalDateTime.now().minusMonths(2)).build());

        enrollments.add(Enrollment.builder().student(students.get(1)).course(courses.get(0)).active(true).enrolledAt(LocalDateTime.now().minusMonths(4)).build());
        enrollments.add(Enrollment.builder().student(students.get(1)).course(courses.get(2)).active(true).enrolledAt(LocalDateTime.now().minusMonths(1)).build());

        enrollments.add(Enrollment.builder().student(students.get(2)).course(courses.get(1)).active(true).enrolledAt(LocalDateTime.now().minusMonths(5)).build());
        enrollments.add(Enrollment.builder().student(students.get(2)).course(courses.get(3)).active(true).enrolledAt(LocalDateTime.now().minusMonths(2)).build());
        enrollments.add(Enrollment.builder().student(students.get(2)).course(courses.get(4)).active(true).enrolledAt(LocalDateTime.now().minusWeeks(3)).build());

        enrollments.add(Enrollment.builder().student(students.get(3)).course(courses.get(0)).active(true).enrolledAt(LocalDateTime.now().minusMonths(6)).build());
        enrollments.add(Enrollment.builder().student(students.get(3)).course(courses.get(1)).active(true).enrolledAt(LocalDateTime.now().minusMonths(3)).build());

        enrollments.add(Enrollment.builder().student(students.get(4)).course(courses.get(2)).active(true).enrolledAt(LocalDateTime.now().minusMonths(2)).build());
        enrollments.add(Enrollment.builder().student(students.get(4)).course(courses.get(4)).active(true).enrolledAt(LocalDateTime.now().minusWeeks(2)).build());

        enrollments.add(Enrollment.builder().student(students.get(5)).course(courses.get(0)).active(true).enrolledAt(LocalDateTime.now().minusMonths(4)).build());
        enrollments.add(Enrollment.builder().student(students.get(5)).course(courses.get(3)).active(false).enrolledAt(LocalDateTime.now().minusMonths(8)).build());

        enrollments.add(Enrollment.builder().student(students.get(6)).course(courses.get(1)).active(true).enrolledAt(LocalDateTime.now().minusMonths(1)).build());
        enrollments.add(Enrollment.builder().student(students.get(6)).course(courses.get(2)).active(true).enrolledAt(LocalDateTime.now().minusWeeks(1)).build());

        enrollments.add(Enrollment.builder().student(students.get(7)).course(courses.get(0)).active(true).enrolledAt(LocalDateTime.now().minusMonths(3)).build());
        enrollments.add(Enrollment.builder().student(students.get(7)).course(courses.get(4)).active(true).enrolledAt(LocalDateTime.now().minusMonths(1)).build());

        enrollmentRepository.saveAll(enrollments);
    }

    private void seedStudentAbsences(List<Student> students, List<ScheduleSlot> scheduleSlots) {
        log.info("Seeding StudentAbsences...");
        List<StudentAbsence> absences = new ArrayList<>();

        absences.add(StudentAbsence.builder()
                .student(students.get(0))
                .scheduleSlot(scheduleSlots.get(0))
                .date(LocalDate.now().minusDays(7))
                .status(AbsenceStatus.ABSENT)
                .justificationText("Maladie - certificat medical fourni")
                .build());

        absences.add(StudentAbsence.builder()
                .student(students.get(1))
                .scheduleSlot(scheduleSlots.get(1))
                .date(LocalDate.now().minusDays(5))
                .status(AbsenceStatus.LATE)
                .justificationText("Retard de transport")
                .build());

        absences.add(StudentAbsence.builder()
                .student(students.get(2))
                .scheduleSlot(scheduleSlots.get(2))
                .date(LocalDate.now().minusDays(10))
                .status(AbsenceStatus.EXCUSED)
                .justificationText("Voyage familial prevu")
                .justificationFileUrl("https://storage.darquran.com/justifications/student-3-travel.pdf")
                .build());

        absences.add(StudentAbsence.builder()
                .student(students.get(3))
                .scheduleSlot(scheduleSlots.get(3))
                .date(LocalDate.now().minusDays(3))
                .status(AbsenceStatus.PRESENT)
                .build());

        absences.add(StudentAbsence.builder()
                .student(students.get(4))
                .scheduleSlot(scheduleSlots.get(4))
                .date(LocalDate.now().minusDays(2))
                .status(AbsenceStatus.ABSENT)
                .build());

        absences.add(StudentAbsence.builder()
                .student(students.get(5))
                .scheduleSlot(scheduleSlots.get(0))
                .date(LocalDate.now().minusDays(14))
                .status(AbsenceStatus.EXCUSED)
                .justificationText("Rendez-vous medical")
                .build());

        studentAbsenceRepository.saveAll(absences);
    }

    private void seedTeacherAbsences(List<Teacher> teachers, List<ScheduleSlot> scheduleSlots) {
        log.info("Seeding TeacherAbsences...");
        List<TeacherAbsence> absences = new ArrayList<>();

        absences.add(TeacherAbsence.builder()
                .teacher(teachers.get(0))
                .scheduleSlot(scheduleSlots.get(0))
                .date(LocalDate.now().minusDays(14))
                .status(AbsenceStatus.EXCUSED)
                .justificationText("Formation pedagogique")
                .build());

        absences.add(TeacherAbsence.builder()
                .teacher(teachers.get(1))
                .scheduleSlot(scheduleSlots.get(1))
                .date(LocalDate.now().minusDays(21))
                .status(AbsenceStatus.ABSENT)
                .justificationText("Maladie")
                .justificationFileUrl("https://storage.darquran.com/justifications/teacher-2-medical.pdf")
                .build());

        absences.add(TeacherAbsence.builder()
                .teacher(teachers.get(2))
                .scheduleSlot(scheduleSlots.get(2))
                .date(LocalDate.now().minusDays(7))
                .status(AbsenceStatus.LATE)
                .justificationText("Probleme de transport")
                .build());

        teacherAbsenceRepository.saveAll(absences);
    }

    private void seedStudentGrades(List<Student> students, List<Course> courses, List<Teacher> teachers) {
        log.info("Seeding StudentGrades...");
        List<StudentGrade> grades = new ArrayList<>();

        grades.add(StudentGrade.builder()
                .student(students.get(0))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .value(17.5)
                .gradeDate(LocalDate.now().minusDays(30))
                .comment("Excellente progression en Tajwid. Continue ainsi!")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(0))
                .course(courses.get(1))
                .teacher(teachers.get(1))
                .value(15.0)
                .gradeDate(LocalDate.now().minusDays(25))
                .comment("Bonne memorisation, attention a la prononciation.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(1))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .value(18.0)
                .gradeDate(LocalDate.now().minusDays(28))
                .comment("Tres bien! Maitrise parfaite des regles de base.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(2))
                .course(courses.get(1))
                .teacher(teachers.get(1))
                .value(14.5)
                .gradeDate(LocalDate.now().minusDays(20))
                .comment("Bon travail. Reviser les sourates precedentes.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(2))
                .course(courses.get(3))
                .teacher(teachers.get(3))
                .value(16.0)
                .gradeDate(LocalDate.now().minusDays(15))
                .comment("Progres remarquables dans la memorisation.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(3))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .value(12.5)
                .gradeDate(LocalDate.now().minusDays(22))
                .comment("Des efforts a fournir sur les lettres emphathiques.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(4))
                .course(courses.get(2))
                .teacher(teachers.get(2))
                .value(19.0)
                .gradeDate(LocalDate.now().minusDays(18))
                .comment("Excellent! Maitrise complete des regles du Noon.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(5))
                .course(courses.get(0))
                .teacher(teachers.get(0))
                .value(13.0)
                .gradeDate(LocalDate.now().minusDays(12))
                .comment("Niveau correct, continuer les efforts.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(6))
                .course(courses.get(1))
                .teacher(teachers.get(1))
                .value(16.5)
                .gradeDate(LocalDate.now().minusDays(10))
                .comment("Tres bonne memorisation du Juz Amma.")
                .build());

        grades.add(StudentGrade.builder()
                .student(students.get(7))
                .course(courses.get(4))
                .teacher(teachers.get(4))
                .value(15.5)
                .gradeDate(LocalDate.now().minusDays(8))
                .comment("Bonne comprehension des Makhraj, a perfectionner.")
                .build());

        studentGradeRepository.saveAll(grades);
    }

    private List<LiveSession> seedLiveSessions(List<Teacher> teachers) {
        log.info("Seeding LiveSessions...");
        List<LiveSession> sessions = new ArrayList<>();

        sessions.add(LiveSession.builder()
                .title("Cours en direct - Tajwid Debutant")
                .description("Session interactive sur les bases du Tajwid avec questions-reponses en direct.")
                .streamKey("live-tajwid-001")
                .hlsPlaybackUrl("/hls/live-tajwid-001.m3u8")
                .status(LiveSessionStatus.SCHEDULED)
                .accessType(LiveAccessType.INTERNAL)
                .adaptiveQualityEnabled(true)
                .recordingEnabled(true)
                .scheduledStartAt(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .scheduledEndAt(LocalDateTime.now().plusDays(2).withHour(11).withMinute(30))
                .section(Section.HOMME)
                .user(teachers.get(0))
                .build());

        sessions.add(LiveSession.builder()
                .title("Recitation collective - Sourate Al-Kahf")
                .description("Recitation collective de Sourate Al-Kahf avec correction en direct.")
                .streamKey("live-kahf-002")
                .hlsPlaybackUrl("/hls/live-kahf-002.m3u8")
                .status(LiveSessionStatus.SCHEDULED)
                .accessType(LiveAccessType.INTERNAL)
                .adaptiveQualityEnabled(true)
                .recordingEnabled(true)
                .scheduledStartAt(LocalDateTime.now().plusDays(5).withHour(14).withMinute(0))
                .scheduledEndAt(LocalDateTime.now().plusDays(5).withHour(15).withMinute(30))
                .section(Section.FEMME)
                .user(teachers.get(1))
                .build());

        sessions.add(LiveSession.builder()
                .title("Conference - L'importance du Coran")
                .description("Conference publique sur l'importance de l'apprentissage du Coran.")
                .streamKey("live-conf-003")
                .hlsPlaybackUrl("/hls/live-conf-003.m3u8")
                .status(LiveSessionStatus.ENDED)
                .accessType(LiveAccessType.EXTERNAL)
                .adaptiveQualityEnabled(true)
                .recordingEnabled(true)
                .recordingUrl("https://storage.darquran.com/recordings/conf-003.mp4")
                .scheduledStartAt(LocalDateTime.now().minusDays(7).withHour(20).withMinute(0))
                .scheduledEndAt(LocalDateTime.now().minusDays(7).withHour(21).withMinute(30))
                .startedAt(LocalDateTime.now().minusDays(7).withHour(20).withMinute(0))
                .endedAt(LocalDateTime.now().minusDays(7).withHour(21).withMinute(25))
                .notificationSent(true)
                .section(Section.HOMME)
                .user(teachers.get(2))
                .build());

        sessions.add(LiveSession.builder()
                .title("Session Hifz - Revision Juz Amma")
                .description("Session de revision intensive du Juz Amma pour les etudiants avances.")
                .streamKey("live-hifz-004")
                .hlsPlaybackUrl("/hls/live-hifz-004.m3u8")
                .status(LiveSessionStatus.LIVE)
                .accessType(LiveAccessType.INTERNAL)
                .adaptiveQualityEnabled(true)
                .recordingEnabled(true)
                .scheduledStartAt(LocalDateTime.now().minusHours(1))
                .scheduledEndAt(LocalDateTime.now().plusHours(1))
                .startedAt(LocalDateTime.now().minusHours(1))
                .notificationSent(true)
                .section(Section.FEMME)
                .user(teachers.get(3))
                .build());

        sessions.add(LiveSession.builder()
                .title("Masterclass - Makhraj Al-Huruf")
                .description("Masterclass avancee sur les points d'articulation des lettres arabes.")
                .streamKey("live-master-005")
                .hlsPlaybackUrl("/hls/live-master-005.m3u8")
                .status(LiveSessionStatus.CANCELLED)
                .accessType(LiveAccessType.INTERNAL)
                .adaptiveQualityEnabled(true)
                .recordingEnabled(false)
                .scheduledStartAt(LocalDateTime.now().minusDays(3).withHour(16).withMinute(0))
                .scheduledEndAt(LocalDateTime.now().minusDays(3).withHour(18).withMinute(0))
                .section(Section.HOMME)
                .user(teachers.get(4))
                .build());

        return liveSessionRepository.saveAll(sessions);
    }

    private void seedLiveComments(List<LiveSession> sessions, List<Student> students, List<Teacher> teachers) {
        log.info("Seeding LiveComments...");
        List<LiveComment> comments = new ArrayList<>();

        // Comments for the ended session (index 2)
        LiveSession endedSession = sessions.get(2);
        comments.add(LiveComment.builder()
                .content("Barakallahu fik cheikh, tres belle conference!")
                .liveSession(endedSession)
                .author(students.get(0))
                .build());

        comments.add(LiveComment.builder()
                .content("Est-ce que l'enregistrement sera disponible?")
                .liveSession(endedSession)
                .author(students.get(2))
                .build());

        comments.add(LiveComment.builder()
                .content("Oui, l'enregistrement sera disponible dans les prochaines heures incha'Allah.")
                .liveSession(endedSession)
                .author(teachers.get(2))
                .build());

        comments.add(LiveComment.builder()
                .content("Jazakum Allahu khayran pour ce partage!")
                .liveSession(endedSession)
                .authorDisplayName("Visiteur Anonyme")
                .build());

        // Comments for the live session (index 3)
        LiveSession liveSession = sessions.get(3);
        comments.add(LiveComment.builder()
                .content("Assalamu alaykum, est-ce qu'on peut revoir la sourate An-Naba?")
                .liveSession(liveSession)
                .author(students.get(1))
                .build());

        comments.add(LiveComment.builder()
                .content("Oui bien sur, on va la revoir ensemble maintenant.")
                .liveSession(liveSession)
                .author(teachers.get(3))
                .build());

        comments.add(LiveComment.builder()
                .content("MashaAllah, quelle belle recitation!")
                .liveSession(liveSession)
                .author(students.get(3))
                .build());

        comments.add(LiveComment.builder()
                .content("Pouvez-vous repeter le verset 15 s'il vous plait?")
                .liveSession(liveSession)
                .author(students.get(5))
                .build());

        liveCommentRepository.saveAll(comments);
    }

    private Lesson createLesson(String title, String description, int order, Course course) {
        return Lesson.builder()
                .title(title)
                .description(description)
                .orderIndex(order)
                .course(course)
                .build();
    }

    private Adresse createAdresse(String rue, String ville, String codePostal) {
        return Adresse.builder()
                .rue(rue)
                .ville(ville)
                .codePostal(codePostal)
                .pays("Maroc")
                .build();
    }
}
