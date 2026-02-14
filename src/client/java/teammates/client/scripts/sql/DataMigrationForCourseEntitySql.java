package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.User;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.test.FileHelper;

/**
 */
@SuppressWarnings({ "PMD", "deprecation" })
public class DataMigrationForCourseEntitySql extends DatastoreClient {

    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    private static final int BATCH_SIZE = 100;

    private static final int MAX_BUFFER_SIZE = 1000;

    private final List<BaseEntity> entitiesSavingBuffer;

    private final Logger logger;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfAffectedEntities;
    AtomicLong numberOfScannedKey;
    AtomicLong numberOfUpdatedEntities;

    private final VerifyCourseEntityAttributes verifier;

    public DataMigrationForCourseEntitySql() {
        numberOfAffectedEntities = new AtomicLong();
        numberOfScannedKey = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

        entitiesSavingBuffer = new ArrayList<>();

        logger = new Logger("Course Chain Migration:");

        verifier = new VerifyCourseEntityAttributes();

        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    public static void main(String[] args) {
        new DataMigrationForCourseEntitySql().doOperationRemotely();
    }

    protected Query<teammates.storage.entity.Course> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Course.class);
    }

    protected boolean isPreview() {
        return false;
    }

    /**
     * Optional limit on how many courses to migrate (e.g. for batch runs).
     * Default is no limit.
     */
    protected Optional<Long> getMaxCoursesToMigrate() {
        return Optional.empty();
    }

    /**
     * Migrates the course and all related entity.
     */
    protected void migrateCourse(teammates.storage.entity.Course oldCourse) throws Exception {
        log("Start migrating course with id: " + oldCourse.getUniqueId());
        String courseId = migrateCourseEntity(oldCourse);
        migrateCourseDependencies(courseId);
        flushEntitiesSavingBuffer();

        // Refetch course - this is not needed but done to get around
        // the inherited interface of verifier

        HibernateUtil.beginTransaction();
        Course newCourse = getCourse(courseId);
        HibernateUtil.commitTransaction();

        log(String.format("Verifying %s", courseId));

        if (!verifier.equals(newCourse, oldCourse)) {
            throw new Exception("Verification failed for course with id: " + oldCourse.getUniqueId());
        }

        oldCourse.setMigrated(true);
        ofy().save().entity(oldCourse).now();

        log("Finish migrating course with id: " + oldCourse.getUniqueId());
    }

    private void migrateCourseDependencies(String newCourseId) {
        migrateSectionChain(newCourseId);
        migrateFeedbackChain(newCourseId);
        // Map<String, teammates.storage.sqlentity.FeedbackSession> feedbackSessionNameToFeedbackSessionMap =
                // migrateFeedbackChain(newCourse, sectionNameToSectionMap);
        migrateInstructorEntities(newCourseId);
        // migrateUserAccounts(newCourse, userGoogleIdToUserMap);
        migrateDeadlineExtensionEntities(newCourseId);
    }

    // methods for migrate section chain ----------------------------------------------------------------------------------
    // entities: Section, Team, Student

    private void migrateSectionChain(
            String courseId) {
        log(String.format("Migrating section chain for %s", courseId));

        migrateSections(courseId);
        migrateTeams(courseId);
        migrateStudents(courseId);
    }

    private String migrateCourseEntity(teammates.storage.entity.Course oldCourse) {
        Course newCourse = new Course(
                oldCourse.getUniqueId(),
                oldCourse.getName(),
                oldCourse.getTimeZone(),
                oldCourse.getInstitute());
        newCourse.setDeletedAt(oldCourse.getDeletedAt());
        newCourse.setCreatedAt(oldCourse.getCreatedAt());

        HibernateUtil.beginTransaction();
        HibernateUtil.persist(newCourse);
        HibernateUtil.commitTransaction();
        return newCourse.getId();
    }

    private void migrateSections(String courseId) {
        log(String.format("Migrating Sections for course %s", courseId));
        HibernateUtil.beginTransaction();
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId).list();
        Course newCourse = getCourse(courseId);
        List<String> sectionNames = oldStudents.stream().map(CourseStudent::getSectionName).distinct()
                .collect(Collectors.toList());
        SectionMigrator.migrate(newCourse, sectionNames, HibernateUtil::persist);
        HibernateUtil.commitTransaction();
    }

    private void migrateTeams(String courseId) {
        log(String.format("Migrating Teams for course %s", courseId));
        HibernateUtil.beginTransaction();
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId).list();
        Map<String, Set<String>> sectionNameToTeamNames = new HashMap<>();
        for (CourseStudent student : oldStudents) {
            sectionNameToTeamNames.putIfAbsent(student.getSectionName(), new HashSet<>());
            sectionNameToTeamNames.get(student.getSectionName()).add(student.getTeamName());
        }
        Course newCourseWithSections = getCourseWithSections(courseId);
        TeamMigrator.migrate(newCourseWithSections, sectionNameToTeamNames, HibernateUtil::persist);
        HibernateUtil.commitTransaction();
    }

    private void migrateStudents(String courseId) {
        log(String.format("Migrating Students for course %s", courseId));
        HibernateUtil.beginTransaction();
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId)
            .list();

        Course newCourse = getCourse(courseId);


        // Get postgres Sections and the names of related teams
        // Key: Section Name    Value: Team Name
        Map<String, HashSet<String>> sectionToTeamNameMap = new HashMap<>();
        List<Section> newSections = getSections(courseId);
        for (Section newSection : newSections) {
            String sectionName = newSection.getName();
            HashSet<String> teamHashSet = new HashSet<>();
            for (Team newTeam : newSection.getTeams()) {
                teamHashSet.add(newTeam.getName());
            }
            sectionToTeamNameMap.put(sectionName, teamHashSet);
        }

        // Get postgres team entities with their relations to the sections
        // Key: Section Name    Value: Team name - Team Entity Map
        Map<String, HashMap<String, Team>> newSectionToTeamEntityMap = new HashMap<>();
        for (Entry<String, HashSet<String>> entry :sectionToTeamNameMap.entrySet()) {
            String sectionName = entry.getKey();
            for (String teamName : entry.getValue()) {
                Team newTeam = getTeam(courseId, sectionName, teamName);

                newSectionToTeamEntityMap.putIfAbsent(sectionName, new HashMap<>());
                newSectionToTeamEntityMap.get(sectionName).putIfAbsent(teamName, newTeam);
            }
        }

        Map<String, Account> googleIdToAccountMap = new HashMap<>();

        for (CourseStudent oldStudent : oldStudents) {
            String googleId = oldStudent.getGoogleId();
            Account associatedAccount = getAccount(googleId);
            googleIdToAccountMap.put(googleId, associatedAccount);
        }

        for (CourseStudent oldStudent : oldStudents) {
            Team newTeam = newSectionToTeamEntityMap
                .get(oldStudent.getSectionName())
                .get(oldStudent.getTeamName());

            Student newStudent = createStudent(newCourse, newTeam, oldStudent);
            Account associatedAccount = googleIdToAccountMap.get(oldStudent.getGoogleId());
            newStudent.setAccount(associatedAccount);

            HibernateUtil.persist(newStudent);
        }


        HibernateUtil.commitTransaction();
    }

    private List<Section> getSections(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Section> cr = cb
                .createQuery(teammates.storage.sqlentity.Section.class);
        Root<teammates.storage.sqlentity.Section> sectionRoot = cr.from(teammates.storage.sqlentity.Section.class);
        cr.select(sectionRoot).where(cb.equal(sectionRoot.get("course").get("id"), courseId));
        List<Section> newSections = HibernateUtil.createQuery(cr).getResultList();
        return newSections;
    }

    private Team getTeam(String courseId, String sectionName, String teamName)  {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Team> cr = cb.createQuery(Team.class);
        Root<Team> teamRoot = cr.from(Team.class);
        cr.where(cb.and(
            cb.equal(teamRoot.get("section").get("name"), sectionName),
            cb.equal(teamRoot.get("section").get("course").get("id"), courseId),
            cb.equal(teamRoot.get("name"), teamName)
        ));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    private Account getAccount(String googleId) {
        return HibernateUtil.getBySimpleNaturalId(Account.class, SanitizationHelper.sanitizeGoogleId(googleId));
    }

    private Student createStudent(Course newCourse,
            teammates.storage.sqlentity.Team newTeam,
            CourseStudent oldStudent) {
        String truncatedStudentName = truncateToLength255(oldStudent.getName());
        String truncatedComments = truncateToLength2000(oldStudent.getComments());

        Student newStudent = new Student(newCourse, truncatedStudentName, oldStudent.getEmail(),
            truncatedComments, newTeam);

        // newStudent.setUpdatedAt(oldStudent.getUpdatedAt());
        newStudent.setRegKey(oldStudent.getRegistrationKey());
        newStudent.setCreatedAt(oldStudent.getCreatedAt());

        return newStudent;
    }

    private void migrateFeedbackChain(String courseId) {
        log("Migrating feedback chain");
        new FeedbackChainMigrator(this::ofy).migrate(courseId);
    }

    private Course getCourseWithSections(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Course> cr = cb.createQuery(Course.class);
        Root<Course> courseRoot = cr.from(Course.class);
        courseRoot.fetch("sections", JoinType.LEFT);
        cr.select(courseRoot).where(cb.equal(courseRoot.get("id"), courseId));
        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    // methods for misc migration methods ---------------------------------------------------------------------------------
    // entities: Instructor, DeadlineExtension

    private void migrateInstructorEntities(String courseId) {
        HibernateUtil.beginTransaction();
        List<teammates.storage.entity.Instructor> oldInstructors = ofy().load()
                .type(teammates.storage.entity.Instructor.class)
                .filter("courseId", courseId)
                .list();

        Course newCourse = getCourse(courseId);

        for (teammates.storage.entity.Instructor oldInstructor : oldInstructors) {
            Instructor newInstructor = createInstructor(newCourse, oldInstructor);
            newInstructor.setAccount(getAccount(oldInstructor.getGoogleId()));
            // if (oldInstructor.getGoogleId() != null) {
            //     userGoogleIdToUserMap.put(oldInstructor.getGoogleId(), newInstructor);
            // }
            HibernateUtil.persist(newInstructor);
        }
        HibernateUtil.commitTransaction();
    }

    private Instructor createInstructor(Course newCourse,
            teammates.storage.entity.Instructor oldInstructor) {
        InstructorPrivileges newPrivileges;
        if (oldInstructor.getInstructorPrivilegesAsText() == null) {
            newPrivileges = new InstructorPrivileges(oldInstructor.getRole());
        } else {
            InstructorPrivilegesLegacy privilegesLegacy = JsonUtils
                    .fromJson(oldInstructor.getInstructorPrivilegesAsText(), InstructorPrivilegesLegacy.class);
            newPrivileges = new InstructorPrivileges(privilegesLegacy);
        }

        String truncatedInstructorName = truncateToLength255(oldInstructor.getName());
        String truncatedDisplayName = truncateToLength255(oldInstructor.getDisplayedName());

        teammates.storage.sqlentity.Instructor newInstructor = new teammates.storage.sqlentity.Instructor(
                newCourse,
                truncatedInstructorName,
                oldInstructor.getEmail(),
                oldInstructor.isDisplayedToStudents(),
                truncatedDisplayName,
                InstructorPermissionRole.getEnum(oldInstructor.getRole()),
                newPrivileges);

        newInstructor.setCreatedAt(oldInstructor.getCreatedAt());
        newInstructor.setUpdatedAt(oldInstructor.getUpdatedAt());
        newInstructor.setRegKey(oldInstructor.getRegistrationKey());
        return newInstructor;
    }

    private void migrateDeadlineExtensionEntities(String courseId) {
        HibernateUtil.beginTransaction();
        log("Migrating deadline extension");
        Map<String, Instructor> emailToInstructorMap = new HashMap<>();
        Map<String, Student> emailToStudentMap = new HashMap<>();

        List<teammates.storage.entity.DeadlineExtension> oldDeadlineExtensions = ofy().load()
                .type(teammates.storage.entity.DeadlineExtension .class)
                .filter("courseId", courseId)
                .list();

        Map<String, FeedbackSession> feedbackSessionNameToFeedbackSessionMap =
            new HashMap<>();

        for (FeedbackSession newFeedbackSession : getCourse(courseId).getFeedbackSessions()) {
            feedbackSessionNameToFeedbackSessionMap.put(newFeedbackSession.getName(), newFeedbackSession);
        }

        for (teammates.storage.entity.DeadlineExtension oldDeadlineExtension : oldDeadlineExtensions) {
            String userEmail = oldDeadlineExtension.getUserEmail();
            String feedbackSessionName = oldDeadlineExtension.getFeedbackSessionName();
            teammates.storage.sqlentity.FeedbackSession feedbackSession = feedbackSessionNameToFeedbackSessionMap.get(feedbackSessionName);
            User user;
            log(userEmail);
            if (oldDeadlineExtension.getIsInstructor()) {
                if (emailToInstructorMap.containsKey(userEmail)) {
                    user = emailToInstructorMap.get(userEmail);
                } else {
                    Instructor instructor = getNewInstructor(courseId, userEmail);
                    emailToInstructorMap.put(userEmail, instructor);
                    user = instructor;
                }

                if (user == null) {
                    logError("Instructor not found for deadline extension: " + oldDeadlineExtension);
                    continue;
                }
            } else {
                if (emailToStudentMap.containsKey(userEmail)) {
                    user = emailToStudentMap.get(userEmail);
                } else {
                    Student student = getNewStudent(courseId, userEmail);
                    emailToStudentMap.put(userEmail, student);
                    user = student;
                }
                if (user == null) {
                    logError("Student not found for deadline extension: " + oldDeadlineExtension);
                    continue;
                }
            }
            DeadlineExtension newDeadlineExtension = createDeadlineExtension(oldDeadlineExtension, feedbackSession, user);
            HibernateUtil.persist(newDeadlineExtension);
        }
        HibernateUtil.commitTransaction();
    }

    private DeadlineExtension createDeadlineExtension(teammates.storage.entity.DeadlineExtension oldDeadlineExtension,
                                          teammates.storage.sqlentity.FeedbackSession feedbackSession,
                                          User newUser) {

        DeadlineExtension newDeadlineExtension =
                new teammates.storage.sqlentity.DeadlineExtension(
                    newUser,
                    feedbackSession,
                    oldDeadlineExtension.getEndTime());

        newDeadlineExtension.setCreatedAt(oldDeadlineExtension.getCreatedAt());
        newDeadlineExtension.setUpdatedAt(oldDeadlineExtension.getUpdatedAt());
        newDeadlineExtension.setClosingSoonEmailSent(oldDeadlineExtension.getSentClosingSoonEmail());

        return newDeadlineExtension;
    }

    private Course getCourse(String courseId) {
        return HibernateUtil.get(Course.class, courseId);
    }

    @Override
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
            Query<teammates.storage.entity.Course> filterQueryKeys = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                filterQueryKeys = filterQueryKeys.startAt(cursor);
            }

            QueryResults<?> iterator = filterQueryKeys.iterator();

            teammates.storage.entity.Course currentOldCourse = null;
            // Cascade delete the course if it is not fully migrated.
            if (iterator.hasNext()) {
                currentOldCourse = (teammates.storage.entity.Course) iterator.next();
                if (currentOldCourse.isMigrated()) {
                    currentOldCourse = iterator.hasNext() ? (teammates.storage.entity.Course) iterator.next() : null;
                } else {
                    deleteCourseCascade(currentOldCourse);
                }
            }


            while (currentOldCourse != null) {
                shouldContinue = true;
                try {
                    doMigration(currentOldCourse);
                } catch (Exception e) {
                    numberOfScannedKey.incrementAndGet();
                    logError(e.getMessage());
                    log("Total number of course entities scanned: " + numberOfScannedKey.get());
                    log("Number of affected course entities: " + numberOfAffectedEntities.get());
                    log("Number of updated course entities: " + numberOfUpdatedEntities.get());
                    e.printStackTrace();
                    return;
                }
                numberOfScannedKey.incrementAndGet();

                cursor = iterator.getCursorAfter();
                savePositionOfCursorToFile(cursor);

                currentOldCourse = iterator.hasNext() ? (teammates.storage.entity.Course) iterator.next() : null;
                if (getMaxCoursesToMigrate().isPresent()
                        && numberOfScannedKey.get() >= getMaxCoursesToMigrate().get()) {
                    currentOldCourse = null;
                }
            }

            if (shouldContinue) {
                log(String.format("Cursor Position: %s", cursor != null ? cursor.toUrlSafe() : "null"));
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
    private void deleteCourseCascade(teammates.storage.entity.Course oldCourse) {
        String courseId = oldCourse.getUniqueId();

        HibernateUtil.beginTransaction();
        Course newCourse = HibernateUtil.get(Course.class, courseId);
        if (newCourse == null) {
            HibernateUtil.commitTransaction();
            return;
        }

        log("delete dangling course with id: " + courseId);
        HibernateUtil.remove(newCourse);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
        HibernateUtil.commitTransaction();
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
    private void doMigration(teammates.storage.entity.Course entity) throws Exception {
        numberOfAffectedEntities.incrementAndGet();
        if (!isPreview()) {
            migrateCourse(entity);
            numberOfUpdatedEntities.incrementAndGet();
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
     * Truncates a string to a maximum length.
     */
    protected String truncate(String str, int maxLength) {
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    /**
     * Truncates to a length of 255.
     */
    protected String truncateToLength255(String str) {
        return truncate(str, 255);
    }

    /**
     * Truncates to a length of 2000.
     */
    protected String truncateToLength2000(String str) {
        return truncate(str, 2000);
    }

    /**
     * Logs a comment.
     */
    protected void log(String logLine) {
        logger.log(logLine);
    }

    /**
     * Logs an error and persists it to the disk.
     */
    protected void logError(String logLine) {
        logger.log("[ERROR]" + logLine);
    }

    private Student getNewStudent(String courseId, String email) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb
                .createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        cr.select(studentRoot).where(cb.and(
            cb.equal(studentRoot.get("courseId"), courseId),
            cb.equal(studentRoot.get("email"), email)
        ));
        Student newStudent = HibernateUtil.createQuery(cr).getSingleResult();
        return newStudent;
    }

    private Instructor getNewInstructor(String courseId, String email) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb
                .createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        cr.select(instructorRoot).where(cb.and(
            cb.equal(instructorRoot.get("courseId"), courseId),
            cb.equal(instructorRoot.get("email"), email)
        ));
        Instructor newInstructor = HibernateUtil.createQuery(cr).getSingleResult();
        return newInstructor;
    }
}
