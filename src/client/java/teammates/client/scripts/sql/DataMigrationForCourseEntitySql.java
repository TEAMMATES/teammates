package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.logic.core.CoursesLogic;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.FileHelper;

/**
 * Data migration class for course entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForCourseEntitySql extends DatastoreClient {

    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    private static final int BATCH_SIZE = 100;

    private static final int MAX_BUFFER_SIZE = 1000;

    private List<BaseEntity> entitiesSavingBuffer;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfAffectedEntities;
    AtomicLong numberOfScannedKey;
    AtomicLong numberOfUpdatedEntities;

    private CoursesLogic coursesLogic = CoursesLogic.inst();

    public DataMigrationForCourseEntitySql() {
        numberOfAffectedEntities = new AtomicLong();
        numberOfScannedKey = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        entitiesSavingBuffer = new ArrayList<>();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) {
        new DataMigrationForCourseEntitySql().doOperationRemotely();
    }

    protected Query<Course> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Course.class);
    }

    protected boolean isPreview() {
        return false;
    }

    /**
     * Migrates the course and all related entity.
     */
    protected void migrateCourse(Course oldCourse) throws Exception {
        teammates.storage.sqlentity.Course newCourse = createCourse(oldCourse);

        migrateCourseEntity(newCourse);
        // flushEntitiesSavingBuffer();
        // verifyCourseEntity(newCourse);
        // markOldCourseAsMigrated(courseId)
    }

    private void migrateCourseEntity(teammates.storage.sqlentity.Course newCourse) {
        Map<String, teammates.storage.sqlentity.Section> sectionNameToSectionMap = migrateSectionChain(newCourse);
        System.out.println(sectionNameToSectionMap); // To stop lint from complaining
        // TODO: Add mirgrateFeedbackChain
        // migrateFeedbackChain(sectionNameToSectionMap);
    }

    private Map<String, teammates.storage.sqlentity.Section> migrateSectionChain(
            teammates.storage.sqlentity.Course newCourse) {
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", newCourse.getId())
                .list();
        Map<String, teammates.storage.sqlentity.Section> sections = new HashMap<>();
        Map<String, List<CourseStudent>> sectionToStuMap = oldStudents.stream()
                .collect(Collectors.groupingBy(CourseStudent::getSectionName));

        for (Map.Entry<String, List<CourseStudent>> entry : sectionToStuMap.entrySet()) {
            String sectionName = entry.getKey();
            List<CourseStudent> stuList = entry.getValue();
            teammates.storage.sqlentity.Section newSection = createSection(newCourse, sectionName);
            sections.put(sectionName, newSection);
            migrateTeams(newCourse, newSection, stuList);
            saveEntityDeferred(newSection);
        }
        return sections;
    }

    private void migrateTeams(teammates.storage.sqlentity.Course newCourse,
            teammates.storage.sqlentity.Section newSection, List<CourseStudent> studentsInSection) {
        Map<String, List<CourseStudent>> teamNameToStuMap = studentsInSection.stream()
                .collect(Collectors.groupingBy(CourseStudent::getTeamName));
        for (Map.Entry<String, List<CourseStudent>> entry : teamNameToStuMap.entrySet()) {
            String teamName = entry.getKey();
            List<CourseStudent> stuList = entry.getValue();
            teammates.storage.sqlentity.Team newTeam = createTeam(newSection, teamName);
            migrateStudents(newCourse, newTeam, stuList);
            saveEntityDeferred(newTeam);
        }
    }

    private void migrateStudents(teammates.storage.sqlentity.Course newCourse, teammates.storage.sqlentity.Team newTeam,
            List<CourseStudent> studentsInTeam) {
        for (CourseStudent oldStudent : studentsInTeam) {
            teammates.storage.sqlentity.Student newStudent = createStudent(newCourse, newTeam, oldStudent);
            saveEntityDeferred(newStudent);
        }
    }

    private teammates.storage.sqlentity.Course createCourse(Course oldCourse) {
        teammates.storage.sqlentity.Course newCourse = new teammates.storage.sqlentity.Course(
                oldCourse.getUniqueId(),
                oldCourse.getName(),
                oldCourse.getTimeZone(),
                oldCourse.getInstitute());
        newCourse.setDeletedAt(oldCourse.getDeletedAt());
        newCourse.setCreatedAt(oldCourse.getCreatedAt());

        saveEntityDeferred(newCourse);
        return newCourse;
    }

    private teammates.storage.sqlentity.Section createSection(teammates.storage.sqlentity.Course newCourse,
            String sectionName) {
        // if (sectionName.equals(Const.DEFAULT_SECTION)) {
        // return Const.DEFAULT_SQL_SECTION;
        // }
        Section newSection = new Section(newCourse, sectionName);
        newSection.setCreatedAt(Instant.now());
        return newSection;
    }

    private teammates.storage.sqlentity.Team createTeam(teammates.storage.sqlentity.Section section, String teamName) {
        Team newTeam = new teammates.storage.sqlentity.Team(section, teamName);
        newTeam.setCreatedAt(Instant.now());
        return newTeam;
    }

    private Student createStudent(teammates.storage.sqlentity.Course newCourse,
            teammates.storage.sqlentity.Team newTeam,
            CourseStudent oldStudent) {
        Student newStudent = new Student(newCourse, oldStudent.getName(), oldStudent.getEmail(),
                oldStudent.getComments(), newTeam);
        newStudent.setUpdatedAt(oldStudent.getUpdatedAt());
        newStudent.setRegKey(oldStudent.getRegistrationKey());
        newStudent.setCreatedAt(oldStudent.getCreatedAt());

        return newStudent;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doOperation() {
        log("Running " + getClass().getSimpleName() + "...");
        log("Preview: " + isPreview());

        Cursor cursor = readPositionOfCursorFromFile().orElse(null);
        if (cursor == null) {
            log("Start from the beginning");
        } else {
            log("Start from cursor position: " + cursor.toUrlSafe());
        }

        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            Query<Course> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }

            QueryResults<?> iterator = filterQueryKeys.iterator();

            Course currentOldCourse = null;
            // Cascade delete the course if it is not fully migrated.
            if (iterator.hasNext()) {
                currentOldCourse = (Course) iterator.next();
                if (currentOldCourse.isMigrated()) {
                    currentOldCourse = (Course) iterator.next();
                } else {
                    deleteCourseCascade(currentOldCourse);
                }
            }

            while (currentOldCourse != null) {
                shouldContinue = true;
                doMigration(currentOldCourse);
                numberOfScannedKey.incrementAndGet();

                cursor = iterator.getCursorAfter();
                savePositionOfCursorToFile(cursor);

                currentOldCourse = iterator.hasNext() ? (Course) iterator.next() : null;
            }

            if (shouldContinue) {
                log(String.format("Cursor Position: %s", cursor.toUrlSafe()));
                log(String.format("Number Of Course Entity Key Scanned: %d", numberOfScannedKey.get()));
                log(String.format("Number Of Course Entity affected: %d", numberOfAffectedEntities.get()));
                log(String.format("Number Of Course Entity updated: %d", numberOfUpdatedEntities.get()));
            }
        }

        deleteCursorPositionFile();
        log(isPreview() ? "Preview Completed!" : "Migration Completed!");
        log("Total number of course entities: " + numberOfScannedKey.get());
        log("Number of affected course entities: " + numberOfAffectedEntities.get());
        log("Number of updated course entities: " + numberOfUpdatedEntities.get());
    }

    /**
     * Deletes the course and its related entities from sql database.
     */
    private void deleteCourseCascade(Course oldCourse) {
        log("delete course id: " + oldCourse.getUniqueId());
        coursesLogic.deleteCourseCascade(oldCourse.getUniqueId());
    }

    /**
     * Reads the cursor position from the saved file.
     *
     * @return cursor if the file can be properly decoded.
     */
    private Optional<Cursor> readPositionOfCursorFromFile() {
        try {
            String cursorPosition = FileHelper.readFile(BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor");
            return Optional.of(Cursor.fromUrlSafe(cursorPosition));
        } catch (IOException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Saves the cursor position to a file so it can be used in the next run.
     */
    private void savePositionOfCursorToFile(Cursor cursor) {
        try {
            FileHelper.saveFile(
                    BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor", cursor.toUrlSafe());
        } catch (IOException e) {
            logError("Fail to save cursor position " + e.getMessage());
        }
    }

    /**
     * Deletes the cursor position file.
     */
    private void deleteCursorPositionFile() {
        FileHelper.deleteFile(BASE_LOG_URI + this.getClass().getSimpleName() + ".cursor");
    }

    /**
     * Migrates the entity and counts the statistics.
     */
    private void doMigration(Course entity) {
        try {
            numberOfAffectedEntities.incrementAndGet();
            if (!isPreview()) {
                migrateCourse(entity);
                numberOfUpdatedEntities.incrementAndGet();
            }
        } catch (Exception e) {
            logError("Problem migrating entity " + entity);
            logError(e.getMessage());
        }
    }

    /**
     * Stores the entity to save in a buffer and saves it later.
     */
    protected void saveEntityDeferred(BaseEntity entity) {
        entitiesSavingBuffer.add(entity);
        if (entitiesSavingBuffer.size() == MAX_BUFFER_SIZE) {
            flushEntitiesSavingBuffer();
        }
    }

    /**
     * Flushes the saving buffer by issuing Cloud SQL save request.
     */
    private void flushEntitiesSavingBuffer() {
        if (!entitiesSavingBuffer.isEmpty() && !isPreview()) {
            log("Saving entities in batch..." + entitiesSavingBuffer.size());

            long startTime = System.currentTimeMillis();
            HibernateUtil.beginTransaction();
            for (BaseEntity entity : entitiesSavingBuffer) {
                HibernateUtil.persist(entity);
            }

            HibernateUtil.flushSession();
            HibernateUtil.clearSession();
            HibernateUtil.commitTransaction();
            long endTime = System.currentTimeMillis();
            log("Flushing " + entitiesSavingBuffer.size() + " took " + (endTime - startTime) + " milliseconds");
        }
        entitiesSavingBuffer.clear();
    }

    /**
     * Logs a comment.
     */
    protected void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));

        Path logPath = Paths.get(BASE_LOG_URI + this.getClass().getSimpleName() + ".log");
        try (OutputStream logFile = Files.newOutputStream(logPath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            logFile.write((logLine + System.lineSeparator()).getBytes(Const.ENCODING));
        } catch (Exception e) {
            System.err.println("Error writing log line: " + logLine);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Returns the log prefix.
     */
    protected String getLogPrefix() {
        return String.format("Migrating Course chains:");
    }

    /**
     * Logs an error and persists it to the disk.
     */
    protected void logError(String logLine) {
        System.err.println(logLine);

        log("[ERROR]" + logLine);
    }
}
