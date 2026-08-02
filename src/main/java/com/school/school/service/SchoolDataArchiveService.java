package com.school.school.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.school.entity.Activity;
import com.school.school.entity.ClassSchedule;
import com.school.school.entity.Level;
import com.school.school.entity.Lesson;
import com.school.school.entity.OpeningHour;
import com.school.school.entity.School;
import com.school.school.entity.Staff;
import com.school.school.entity.StaffStatus;
import com.school.school.entity.Unit;
import com.school.school.entity.User;
import com.school.school.entity.UserRole;
import com.school.school.pojo.SchoolDataExport.ActivityArchive;
import com.school.school.pojo.SchoolDataExport.ClassScheduleArchive;
import com.school.school.pojo.SchoolDataExport.LessonArchive;
import com.school.school.pojo.SchoolDataExport.OpeningHourArchive;
import com.school.school.pojo.SchoolDataExport.StaffArchive;
import com.school.school.pojo.SchoolDataExport.UnitArchive;
import com.school.school.pojo.SchoolDataExport;
import com.school.school.pojo.SchoolResponse;
import com.school.school.pojo.SchoolUpdate;
import com.school.school.repository.ActivityRepository;
import com.school.school.repository.ClassScheduleRepository;
import com.school.school.repository.LevelRepository;
import com.school.school.repository.LessonRepository;
import com.school.school.repository.SchoolRepository;
import com.school.school.repository.StaffRepository;
import com.school.school.repository.UnitRepository;
import com.school.school.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.Base64;

@Service
public class SchoolDataArchiveService {
    private static final String SCHOOL_FILE = "school.json";
    private static final String SCHOOL_DATA_FILE = "school-data.json";

    private final SchoolService schoolService;
    private final SchoolRepository schoolRepository;
    private final LessonRepository lessonRepository;
    private final UnitRepository unitRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final StaffRepository staffRepository;
    private final ActivityRepository activityRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public SchoolDataArchiveService(
            SchoolService schoolService,
            SchoolRepository schoolRepository,
            LessonRepository lessonRepository,
            UnitRepository unitRepository,
            ClassScheduleRepository classScheduleRepository,
            StaffRepository staffRepository,
            ActivityRepository activityRepository,
            LevelRepository levelRepository,
            UserRepository userRepository
    ) {
        this.schoolService = schoolService;
        this.schoolRepository = schoolRepository;
        this.lessonRepository = lessonRepository;
        this.unitRepository = unitRepository;
        this.classScheduleRepository = classScheduleRepository;
        this.staffRepository = staffRepository;
        this.activityRepository = activityRepository;
        this.levelRepository = levelRepository;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Transactional
    public byte[] exportSchool(UUID schoolId) throws IOException {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("School not found"));
        SchoolDataExport export = buildExport(school);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry(SCHOOL_DATA_FILE));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(zip, export);
            zip.closeEntry();
        }

        return bytes.toByteArray();
    }

    @Transactional
    public SchoolResponse importSchool(UUID schoolId, MultipartFile file) throws IOException {
        SchoolDataExport export = readExport(file);
        SchoolResponse importedSchool = export.school();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("School not found"));

        SchoolUpdate update = new SchoolUpdate(
                importedSchool.schoolName(),
                importedSchool.schoolEmail(),
                importedSchool.schoolAddress(),
                importedSchool.logoUrl(),
                importedSchool.phoneNumbers(),
                importedSchool.description(),
                importedSchool.subTitle(),
                importedSchool.customizeSchoolId(),
                null
        );

        schoolService.updateSchool(update, schoolId);
        overwriteOpeningHours(school, export.openingHours());
        overwriteSchoolChildren(school, export);

        return schoolService.getSchool(schoolId);
    }

    private SchoolDataExport buildExport(School school) {
        UUID schoolId = school.getId();
        List<LessonArchive> lessons = lessonRepository.findBySchoolId(schoolId)
                .stream()
                .map(this::toLessonArchive)
                .toList();
        List<ClassScheduleArchive> schedules = classScheduleRepository.findBySchoolIdOrderByDayOfWeekAscStartTimeAsc(schoolId)
                .stream()
                .map(this::toScheduleArchive)
                .toList();
        List<StaffArchive> staff = staffRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId)
                .stream()
                .map(this::toStaffArchive)
                .toList();
        List<ActivityArchive> activities = activityRepository.findBySchoolIdOrderByActivityDateDescCreatedAtDesc(schoolId)
                .stream()
                .map(this::toActivityArchive)
                .toList();
        List<OpeningHourArchive> openingHours = school.getOpeningHours()
                .stream()
                .map(hour -> new OpeningHourArchive(hour.getDayOfWeek(), hour.getOpenTime(), hour.getCloseTime()))
                .toList();

        return new SchoolDataExport(2, Instant.now(), schoolService.getSchool(schoolId),
                openingHours, lessons, schedules, staff, activities);
    }

    private LessonArchive toLessonArchive(Lesson lesson) {
        List<UnitArchive> units = unitRepository.findByLessonId(lesson.getId())
                .stream()
                .map(unit -> new UnitArchive(
                        unit.getId(),
                        unit.getTitle(),
                        unit.getContent(),
                        unit.getVideoUrl(),
                        unit.getCreatedBy() != null ? unit.getCreatedBy().getEmail() : null
                ))
                .toList();

        return new LessonArchive(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getCategory(),
                lesson.getContent(),
                lesson.getLevel() != null ? lesson.getLevel().getName() : null,
                lesson.getCreatedBy() != null ? lesson.getCreatedBy().getEmail() : null,
                units
        );
    }

    private ClassScheduleArchive toScheduleArchive(ClassSchedule schedule) {
        return new ClassScheduleArchive(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMode(),
                schedule.getTeacher() != null ? schedule.getTeacher().getEmail() : null,
                schedule.getLocation(),
                schedule.getMeetingUrl()
        );
    }

    private StaffArchive toStaffArchive(Staff staff) {
        User user = staff.getUser();
        return new StaffArchive(
                staff.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                staff.getPhone(),
                staff.getRole(),
                staff.getHireDate(),
                staff.getStatus()
        );
    }

    private ActivityArchive toActivityArchive(Activity activity) {
        return new ActivityArchive(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getImages(),
                activity.getActivityDate(),
                activity.getLocation(),
                activity.getMaxParticipants(),
                activity.getRegisteredCount()
        );
    }

    private void overwriteOpeningHours(School school, List<OpeningHourArchive> openingHours) {
        school.getOpeningHours().clear();
        for (OpeningHourArchive archive : safeList(openingHours)) {
            if (archive.dayOfWeek() == null || archive.openTime() == null || archive.closeTime() == null) {
                continue;
            }

            OpeningHour openingHour = new OpeningHour();
            openingHour.setSchool(school);
            openingHour.setDayOfWeek(archive.dayOfWeek());
            openingHour.setOpenTime(archive.openTime());
            openingHour.setCloseTime(archive.closeTime());
            school.getOpeningHours().add(openingHour);
        }
    }

    private void overwriteSchoolChildren(School school, SchoolDataExport export) {
        UUID schoolId = school.getId();
        Map<String, User> usersByEmail = new HashMap<>();

        classScheduleRepository.deleteAll(classScheduleRepository.findBySchoolIdOrderByDayOfWeekAscStartTimeAsc(schoolId));
        activityRepository.deleteAll(activityRepository.findBySchoolIdOrderByActivityDateDescCreatedAtDesc(schoolId));
        for (Lesson lesson : lessonRepository.findBySchoolId(schoolId)) {
            unitRepository.deleteAll(unitRepository.findByLessonId(lesson.getId()));
            lessonRepository.delete(lesson);
        }
        staffRepository.deleteAll(staffRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId));
        classScheduleRepository.flush();
        activityRepository.flush();
        unitRepository.flush();
        lessonRepository.flush();
        staffRepository.flush();

        for (StaffArchive archive : safeList(export.staff())) {
            if (isBlank(archive.email()) || archive.role() == null) {
                continue;
            }

            User user = userForStaffArchive(archive);
            usersByEmail.put(user.getEmail(), user);

            Staff staff = new Staff();
            staff.setSchool(school);
            staff.setUser(user);
            staff.setRole(archive.role());
            staff.setPhone(archive.phone());
            staff.setHireDate(archive.hireDate());
            staff.setStatus(archive.status() != null ? archive.status() : StaffStatus.ACTIVE);
            staffRepository.save(staff);
        }

        for (LessonArchive archive : safeList(export.lessons())) {
            Lesson lesson = new Lesson();
            lesson.setSchool(school);
            lesson.setTitle(requireText(archive.title(), "Untitled lesson"));
            lesson.setCategory(requireText(archive.category(), "General"));
            lesson.setContent(requireText(archive.content(), "Imported lesson"));
            lesson.setLevel(levelForName(archive.levelName()));
            lesson.setCreatedBy(userByEmail(archive.createdByEmail(), usersByEmail, school.getOwner()));
            Lesson savedLesson = lessonRepository.save(lesson);

            for (UnitArchive unitArchive : safeList(archive.units())) {
                Unit unit = new Unit();
                unit.setLesson(savedLesson);
                unit.setTitle(requireText(unitArchive.title(), "Untitled unit"));
                unit.setContent(requireText(unitArchive.content(), "Imported unit"));
                unit.setVideoUrl(unitArchive.videoUrl());
                unit.setCreatedBy(userByEmail(unitArchive.createdByEmail(), usersByEmail, savedLesson.getCreatedBy()));
                unitRepository.save(unit);
            }
        }

        for (ClassScheduleArchive archive : safeList(export.schedules())) {
            if (archive.dayOfWeek() == null || archive.startTime() == null || archive.endTime() == null || archive.mode() == null) {
                continue;
            }

            ClassSchedule schedule = new ClassSchedule();
            schedule.setSchool(school);
            schedule.setDayOfWeek(archive.dayOfWeek());
            schedule.setStartTime(archive.startTime());
            schedule.setEndTime(archive.endTime());
            schedule.setMode(archive.mode());
            schedule.setTeacher(userByEmail(archive.teacherEmail(), usersByEmail, null));
            schedule.setLocation(archive.location());
            schedule.setMeetingUrl(archive.meetingUrl());
            classScheduleRepository.save(schedule);
        }

        for (ActivityArchive archive : safeList(export.activities())) {
            Activity activity = new Activity();
            activity.setSchool(school);
            activity.setTitle(archive.title());
            activity.setDescription(archive.description());
            activity.setImages(archive.images());
            activity.setActivityDate(archive.activityDate());
            activity.setLocation(archive.location());
            activity.setMaxParticipants(archive.maxParticipants());
            activity.setRegisteredCount(archive.registeredCount());
            activityRepository.save(activity);
        }
    }

    private User userForStaffArchive(StaffArchive archive) {
        User user = userRepository.findByEmail(archive.email())
                .orElseGet(() -> {
                    User created = new User();
                    created.setEmail(archive.email());
                    created.setPassword(generateTemporaryPassword());
                    return created;
                });

        user.setFirstName(requireText(archive.firstName(), "Imported"));
        user.setLastName(requireText(archive.lastName(), "Staff"));
        if (user.getRole() != UserRole.SUPER_ADMIN || archive.role().name().equals(UserRole.SUPER_ADMIN.name())) {
            user.setRole(UserRole.fromStaffRole(archive.role()));
        }

        return userRepository.save(user);
    }

    private User userByEmail(String email, Map<String, User> importedUsersByEmail, User fallback) {
        if (isBlank(email)) {
            return fallback;
        }

        User importedUser = importedUsersByEmail.get(email);
        if (importedUser != null) {
            return importedUser;
        }

        return userRepository.findByEmail(email).orElse(fallback);
    }

    private Level levelForName(String levelName) {
        List<Level> levels = levelRepository.findAll();
        if (!isBlank(levelName)) {
            for (Level level : levels) {
                if (levelName.equalsIgnoreCase(level.getName())) {
                    return level;
                }
            }
        }

        if (!levels.isEmpty()) {
            return levels.get(0);
        }

        Level level = new Level();
        level.setName(requireText(levelName, "Imported"));
        return levelRepository.save(level);
    }

    private String generateTemporaryPassword() {
        byte[] bytes = new byte[18];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String requireText(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private SchoolDataExport readExport(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalStateException("Import file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".json")) {
            return objectMapper.readValue(file.getInputStream(), SchoolDataExport.class);
        }

        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (SCHOOL_DATA_FILE.equals(entry.getName()) || SCHOOL_FILE.equals(entry.getName())) {
                    return objectMapper.readValue(zip, SchoolDataExport.class);
                }
            }
        }

        throw new IllegalStateException("school-data.json was not found in the import file");
    }
}
