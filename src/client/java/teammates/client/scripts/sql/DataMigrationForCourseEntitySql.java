package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.User;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion.FeedbackConstantSumQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackContributionQuestion.FeedbackContributionQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion.FeedbackMcqQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion.FeedbackMsqQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackNumericalScaleQuestion.FeedbackNumericalScaleQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackRankOptionsQuestion.FeedbackRankOptionsQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackRankRecipientsQuestion.FeedbackRankRecipientsQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion.FeedbackRubricQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion.FeedbackTextQuestionDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackConstantSumResponse.FeedbackConstantSumResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackContributionResponse.FeedbackContributionResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackMcqResponse.FeedbackMcqResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackMsqResponse.FeedbackMsqResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse.FeedbackNumericalScaleResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse.FeedbackRankOptionsResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse.FeedbackRankRecipientsResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse.FeedbackRubricResponseDetailsConverter;
import teammates.test.FileHelper;

/**
 * Data migration script for course entities from Datastore to Cloud SQL.
 */
@SuppressWarnings({ "PMD", "deprecation" })
public class DataMigrationForCourseEntitySql extends DatastoreClient {

    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    private static final int BATCH_SIZE = 100;

    private final Logger logger;

    private final VerifyCourseEntityAttributes verifier;

    private final AtomicLong numberOfAffectedEntities;
    private final AtomicLong numberOfScannedKey;
    private final AtomicLong numberOfUpdatedEntities;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    public DataMigrationForCourseEntitySql() {
        numberOfAffectedEntities = new AtomicLong();
        numberOfScannedKey = new AtomicLong();
        numberOfUpdatedEntities = new AtomicLong();

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

        // Refetch course and verify within a transaction so verifier's Hibernate
        // queries (getCourse, getNewSections, etc.) have an active session and
        // session state does not leak between course verifications.
        log(String.format("Verifying %s", courseId));
        HibernateUtil.beginTransaction();
        try {
            Course newCourse = getCourse(courseId);
            if (!verifier.equals(newCourse, oldCourse)) {
                HibernateUtil.rollbackTransaction();
                throw new Exception("Verification failed for course with id: " + oldCourse.getUniqueId());
            }
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            throw e;
        }

        oldCourse.setMigrated(true);
        ofy().save().entity(oldCourse).now();

        log("Finish migrating course with id: " + oldCourse.getUniqueId());
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

    private void migrateCourseDependencies(String courseId) {
        migrateSections(courseId);
        migrateTeams(courseId);
        migrateStudents(courseId);
        migrateFeedbackChain(courseId);
        migrateInstructorEntities(courseId);
        migrateDeadlineExtensionEntities(courseId);
    }

    private void migrateSections(String courseId) {
        log(String.format("Migrating Sections for course %s", courseId));
        HibernateUtil.beginTransaction();
        Course newCourse = getCourse(courseId);
        List<String> sectionNames = ofy().load().type(CourseStudent.class)
                .filter("courseId", courseId).list().stream()
                .map(cs -> DataMigrationForSectionSql.normalizeSectionName(cs.getSectionName()))
                .distinct()
                .collect(Collectors.toList());
        DataMigrationForSectionSql.migrateSections(newCourse, sectionNames, HibernateUtil::persist);
        HibernateUtil.commitTransaction();
    }

    private void migrateTeams(String courseId) {
        log(String.format("Migrating Teams for course %s", courseId));
        HibernateUtil.beginTransaction();
        Course courseWithSections = getCourseWithSections(courseId);
        Map<String, java.util.Set<String>> teamsBySection = buildTeamsBySectionMap(courseId);
        DataMigrationForTeamSql.migrateTeams(courseWithSections, teamsBySection, HibernateUtil::persist);
        HibernateUtil.commitTransaction();
    }

    private Map<String, java.util.Set<String>> buildTeamsBySectionMap(String courseId) {
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class)
                .filter("courseId", courseId).list();
        Map<String, java.util.Set<String>> teamsBySection = new HashMap<>();
        for (CourseStudent student : oldStudents) {
            String sectionName = DataMigrationForSectionSql.normalizeSectionName(student.getSectionName());
            String teamName = DataMigrationForTeamSql.normalizeTeamName(student.getTeamName());
            teamsBySection.computeIfAbsent(sectionName, k -> new java.util.HashSet<>()).add(teamName);
        }
        return teamsBySection;
    }

    private void migrateStudents(String courseId) {
        log(String.format("Migrating Students for course %s", courseId));
        HibernateUtil.beginTransaction();
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class)
                .filter("courseId", courseId).list();

        Course newCourse = getCourse(courseId);
        Map<String, Map<String, Team>> teamLookup = buildTeamLookupMap(courseId);
        Map<String, Account> accountLookup = buildAccountLookupMap(oldStudents);

        for (CourseStudent oldStudent : oldStudents) {
            String sectionName = DataMigrationForSectionSql.normalizeSectionName(oldStudent.getSectionName());
            String teamName = DataMigrationForTeamSql.normalizeTeamName(oldStudent.getTeamName());
            Team team = teamLookup.get(sectionName).get(teamName);

            Student newStudent = createStudent(newCourse, team, oldStudent);
            newStudent.setAccount(accountLookup.get(oldStudent.getGoogleId()));
            HibernateUtil.persist(newStudent);
        }

        HibernateUtil.commitTransaction();
    }

    private Map<String, Map<String, Team>> buildTeamLookupMap(String courseId) {
        Map<String, Map<String, Team>> teamLookup = new HashMap<>();
        for (Section section : getSections(courseId)) {
            Map<String, Team> teamsInSection = new HashMap<>();
            for (Team team : section.getTeams()) {
                teamsInSection.put(team.getName(), team);
            }
            teamLookup.put(section.getName(), teamsInSection);
        }
        return teamLookup;
    }

    private Map<String, Account> buildAccountLookupMap(List<CourseStudent> students) {
        Map<String, Account> accountLookup = new HashMap<>();
        for (CourseStudent student : students) {
            accountLookup.put(student.getGoogleId(), getAccount(student.getGoogleId()));
        }
        return accountLookup;
    }

    private List<Section> getSections(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Section> cr = cb.createQuery(Section.class);
        Root<Section> sectionRoot = cr.from(Section.class);
        cr.select(sectionRoot).where(cb.equal(sectionRoot.get("course").get("id"), courseId));
        return HibernateUtil.createQuery(cr).getResultList();
    }

    private Account getAccount(String googleId) {
        return HibernateUtil.getBySimpleNaturalId(Account.class, SanitizationHelper.sanitizeGoogleId(googleId));
    }

    private Student createStudent(Course newCourse, Team team, CourseStudent oldStudent) {
        Student newStudent = new Student(
                newCourse,
                truncateToLength255(oldStudent.getName()),
                oldStudent.getEmail(),
                truncateToLength2000(oldStudent.getComments()),
                team);
        newStudent.setRegKey(oldStudent.getRegistrationKey());
        newStudent.setCreatedAt(oldStudent.getCreatedAt());
        return newStudent;
    }

    private void migrateFeedbackChain(String courseId) {
        log("Migrating feedback chain");
        HibernateUtil.beginTransaction();
        try {
            List<teammates.storage.entity.FeedbackSession> oldSessions = ofy().load()
                    .type(teammates.storage.entity.FeedbackSession.class)
                    .filter("courseId", courseId).list();

            Course newCourse = getCourse(courseId);
            Map<String, List<FeedbackQuestion>> questionsBySession = ofy().load()
                    .type(FeedbackQuestion.class)
                    .filter("courseId", courseId).list().stream()
                    .collect(Collectors.groupingBy(FeedbackQuestion::getFeedbackSessionName));
            Map<String, Section> sectionLookup = getSections(courseId).stream()
                    .collect(Collectors.toMap(Section::getName, s -> s));

            for (teammates.storage.entity.FeedbackSession oldSession : oldSessions) {
                FeedbackSession newSession = createFeedbackSession(newCourse, oldSession);
                HibernateUtil.persist(newSession);

                List<FeedbackQuestion> questions = questionsBySession.get(oldSession.getFeedbackSessionName());
                if (questions != null) {
                    for (FeedbackQuestion oldQuestion : questions) {
                        migrateFeedbackQuestion(newSession, oldQuestion, sectionLookup);
                    }
                }
            }
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            throw e;
        }
    }

    private FeedbackSession createFeedbackSession(Course newCourse,
            teammates.storage.entity.FeedbackSession oldSession) {
        FeedbackSession newSession = new FeedbackSession(
                oldSession.getFeedbackSessionName(),
                newCourse,
                oldSession.getCreatorEmail(),
                truncateToLength2000(oldSession.getInstructions()),
                oldSession.getStartTime(),
                oldSession.getEndTime(),
                oldSession.getSessionVisibleFromTime(),
                oldSession.getResultsVisibleFromTime(),
                Duration.ofMinutes(oldSession.getGracePeriod()),
                oldSession.isOpenedEmailEnabled(),
                oldSession.isClosingSoonEmailEnabled(),
                oldSession.isPublishedEmailEnabled());

        newSession.setClosedEmailSent(oldSession.isSentClosedEmail());
        newSession.setClosingSoonEmailSent(oldSession.isSentClosingSoonEmail());
        newSession.setOpenedEmailSent(oldSession.isSentOpenedEmail());
        newSession.setOpeningSoonEmailSent(oldSession.isSentOpeningSoonEmail());
        newSession.setPublishedEmailSent(oldSession.isSentPublishedEmail());
        newSession.setCreatedAt(oldSession.getCreatedTime());
        newSession.setDeletedAt(oldSession.getDeletedTime());
        return newSession;
    }

    private void migrateFeedbackQuestion(FeedbackSession newSession, FeedbackQuestion oldQuestion,
            Map<String, Section> sectionLookup) {
        teammates.storage.sqlentity.FeedbackQuestion newQuestion = createFeedbackQuestion(newSession, oldQuestion);
        HibernateUtil.persist(newQuestion);

        Map<String, List<FeedbackResponseComment>> commentsByResponse = ofy().load()
                .type(FeedbackResponseComment.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list().stream()
                .collect(Collectors.groupingBy(FeedbackResponseComment::getFeedbackResponseId));

        List<FeedbackResponse> oldResponses = ofy().load()
                .type(FeedbackResponse.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list();

        for (FeedbackResponse oldResponse : oldResponses) {
            Section giverSection = sectionLookup.get(
                    DataMigrationForSectionSql.normalizeSectionName(oldResponse.getGiverSection())
            );
            Section recipientSection = sectionLookup.get(
                    DataMigrationForSectionSql.normalizeSectionName(oldResponse.getRecipientSection())
            );
            migrateFeedbackResponse(newQuestion, oldResponse, giverSection, recipientSection, commentsByResponse);
        }
    }

    private teammates.storage.sqlentity.FeedbackQuestion createFeedbackQuestion(FeedbackSession newSession,
            FeedbackQuestion oldQuestion) {
        teammates.storage.sqlentity.FeedbackQuestion newQuestion =
                teammates.storage.sqlentity.FeedbackQuestion.makeQuestion(
                        newSession,
                        oldQuestion.getQuestionNumber(),
                        oldQuestion.getQuestionDescription(),
                        oldQuestion.getGiverType(),
                        oldQuestion.getRecipientType(),
                        oldQuestion.getNumberOfEntitiesToGiveFeedbackTo(),
                        oldQuestion.getShowResponsesTo(),
                        oldQuestion.getShowGiverNameTo(),
                        oldQuestion.getShowRecipientNameTo(),
                        getFeedbackQuestionDetails(oldQuestion));
        newQuestion.setCreatedAt(oldQuestion.getCreatedAt());
        return newQuestion;
    }

    private FeedbackQuestionDetails getFeedbackQuestionDetails(FeedbackQuestion oldQuestion) {
        return switch (oldQuestion.getQuestionType()) {
        case MCQ -> new FeedbackMcqQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case MSQ -> new FeedbackMsqQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case TEXT -> new FeedbackTextQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case RUBRIC -> new FeedbackRubricQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case CONTRIB -> new FeedbackContributionQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case CONSTSUM, CONSTSUM_RECIPIENTS, CONSTSUM_OPTIONS -> new FeedbackConstantSumQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case NUMSCALE -> new FeedbackNumericalScaleQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case RANK_OPTIONS -> new FeedbackRankOptionsQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        case RANK_RECIPIENTS -> new FeedbackRankRecipientsQuestionDetailsConverter()
                .convertToEntityAttribute(oldQuestion.getQuestionText());
        default -> throw new IllegalArgumentException("Invalid question type");
        };
    }

    private void migrateFeedbackResponse(teammates.storage.sqlentity.FeedbackQuestion newQuestion,
            FeedbackResponse oldResponse, Section giverSection, Section recipientSection,
            Map<String, List<FeedbackResponseComment>> commentsByResponse) {
        teammates.storage.sqlentity.FeedbackResponse newResponse = createFeedbackResponse(
                newQuestion, oldResponse, giverSection, recipientSection);
        HibernateUtil.persist(newResponse);

        List<FeedbackResponseComment> oldComments = commentsByResponse.getOrDefault(
                oldResponse.getId(), Collections.emptyList());
        for (FeedbackResponseComment oldComment : oldComments) {
            migrateFeedbackResponseComment(newResponse, oldComment, giverSection, recipientSection);
        }
    }

    private teammates.storage.sqlentity.FeedbackResponse createFeedbackResponse(
            teammates.storage.sqlentity.FeedbackQuestion newQuestion, FeedbackResponse oldResponse,
            Section giverSection, Section recipientSection) {
        teammates.storage.sqlentity.FeedbackResponse newResponse =
                teammates.storage.sqlentity.FeedbackResponse.makeResponse(
                        newQuestion,
                        oldResponse.getGiverEmail(),
                        giverSection,
                        oldResponse.getRecipientEmail(),
                        recipientSection,
                        getFeedbackResponseDetails(oldResponse));

        newResponse.setCreatedAt(oldResponse.getCreatedAt());
        newResponse.setUpdatedAt(oldResponse.getUpdatedAt());
        return newResponse;
    }

    private FeedbackResponseDetails getFeedbackResponseDetails(FeedbackResponse oldResponse) {
        return switch (oldResponse.getFeedbackQuestionType()) {
        case MCQ -> new FeedbackMcqResponseDetailsConverter().convertToEntityAttribute(oldResponse.getAnswer());
        case MSQ -> new FeedbackMsqResponseDetailsConverter().convertToEntityAttribute(oldResponse.getAnswer());
        case TEXT -> new FeedbackTextResponseDetails(oldResponse.getAnswer());
        case RUBRIC -> new FeedbackRubricResponseDetailsConverter().convertToEntityAttribute(oldResponse.getAnswer());
        case CONTRIB -> new FeedbackContributionResponseDetailsConverter()
                .convertToEntityAttribute(oldResponse.getAnswer());
        case CONSTSUM, CONSTSUM_RECIPIENTS, CONSTSUM_OPTIONS -> new FeedbackConstantSumResponseDetailsConverter()
                .convertToEntityAttribute(oldResponse.getAnswer());
        case NUMSCALE -> new FeedbackNumericalScaleResponseDetailsConverter()
                .convertToEntityAttribute(oldResponse.getAnswer());
        case RANK_OPTIONS -> new FeedbackRankOptionsResponseDetailsConverter()
                .convertToEntityAttribute(oldResponse.getAnswer());
        case RANK_RECIPIENTS -> new FeedbackRankRecipientsResponseDetailsConverter()
                .convertToEntityAttribute(oldResponse.getAnswer());
        default -> throw new IllegalArgumentException("Invalid response type");
        };
    }

    private void migrateFeedbackResponseComment(teammates.storage.sqlentity.FeedbackResponse newResponse,
            FeedbackResponseComment oldComment, Section giverSection, Section recipientSection) {
        teammates.storage.sqlentity.FeedbackResponseComment newComment =
                new teammates.storage.sqlentity.FeedbackResponseComment(
                        newResponse,
                        oldComment.getGiverEmail(),
                        oldComment.getCommentGiverType(),
                        giverSection,
                        recipientSection,
                        truncateToLength2000(oldComment.getCommentText()),
                        oldComment.getIsVisibilityFollowingFeedbackQuestion(),
                        oldComment.getIsCommentFromFeedbackParticipant(),
                        oldComment.getShowCommentTo(),
                        oldComment.getShowGiverNameTo(),
                        oldComment.getLastEditorEmail());

        newComment.setCreatedAt(oldComment.getCreatedAt());
        newComment.setUpdatedAt(oldComment.getLastEditedAt());
        HibernateUtil.persist(newComment);
    }

    private Course getCourseWithSections(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Course> cr = cb.createQuery(Course.class);
        Root<Course> root = cr.from(Course.class);
        root.fetch("sections", JoinType.LEFT);
        cr.select(root).where(cb.equal(root.get("id"), courseId));
        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    private void migrateInstructorEntities(String courseId) {
        log("Migrating instructors");
        HibernateUtil.beginTransaction();
        List<teammates.storage.entity.Instructor> oldInstructors = ofy().load()
                .type(teammates.storage.entity.Instructor.class)
                .filter("courseId", courseId).list();

        Course newCourse = getCourse(courseId);

        for (teammates.storage.entity.Instructor oldInstructor : oldInstructors) {
            Instructor newInstructor = createInstructor(newCourse, oldInstructor);
            newInstructor.setAccount(getAccount(oldInstructor.getGoogleId()));
            HibernateUtil.persist(newInstructor);
        }
        HibernateUtil.commitTransaction();
    }

    private Instructor createInstructor(Course newCourse, teammates.storage.entity.Instructor oldInstructor) {
        Instructor newInstructor = new Instructor(
                newCourse,
                truncateToLength255(oldInstructor.getName()),
                oldInstructor.getEmail(),
                oldInstructor.isDisplayedToStudents(),
                truncateToLength255(oldInstructor.getDisplayedName()),
                InstructorPermissionRole.getEnum(oldInstructor.getRole()),
                parseInstructorPrivileges(oldInstructor));

        newInstructor.setCreatedAt(oldInstructor.getCreatedAt());
        newInstructor.setUpdatedAt(oldInstructor.getUpdatedAt());
        newInstructor.setRegKey(oldInstructor.getRegistrationKey());
        return newInstructor;
    }

    private InstructorPrivileges parseInstructorPrivileges(teammates.storage.entity.Instructor oldInstructor) {
        if (oldInstructor.getInstructorPrivilegesAsText() == null) {
            return new InstructorPrivileges(oldInstructor.getRole());
        }

        InstructorPrivilegesLegacy legacyPrivileges = JsonUtils
                .fromJson(oldInstructor.getInstructorPrivilegesAsText(), InstructorPrivilegesLegacy.class);

        return legacyPrivileges == null
                ? new InstructorPrivileges(oldInstructor.getRole())
                : new InstructorPrivileges(legacyPrivileges);
    }

    private void migrateDeadlineExtensionEntities(String courseId) {
        log("Migrating deadline extensions");
        HibernateUtil.beginTransaction();

        List<teammates.storage.entity.DeadlineExtension> oldExtensions = ofy().load()
                .type(teammates.storage.entity.DeadlineExtension.class)
                .filter("courseId", courseId).list();

        Map<String, FeedbackSession> sessionLookup = getCourse(courseId).getFeedbackSessions().stream()
                .collect(Collectors.toMap(FeedbackSession::getName, s -> s));
        Map<String, Instructor> instructorCache = new HashMap<>();
        Map<String, Student> studentCache = new HashMap<>();

        for (teammates.storage.entity.DeadlineExtension oldExtension : oldExtensions) {
            FeedbackSession session = sessionLookup.get(oldExtension.getFeedbackSessionName());
            User user;
            if (oldExtension.getIsInstructor()) {
                user = instructorCache.computeIfAbsent(oldExtension.getUserEmail(),
                            email -> getNewInstructor(courseId, email));
            } else {
                user = studentCache.computeIfAbsent(oldExtension.getUserEmail(),
                            email -> getNewStudent(courseId, email));
            }

            if (user == null) {
                logError("User not found for deadline extension: " + oldExtension.getUserEmail());
                continue;
            }

            DeadlineExtension newExtension = createDeadlineExtension(oldExtension, session, user);
            HibernateUtil.persist(newExtension);
        }
        HibernateUtil.commitTransaction();
    }

    private DeadlineExtension createDeadlineExtension(teammates.storage.entity.DeadlineExtension oldExtension,
            FeedbackSession session, User user) {
        DeadlineExtension newExtension = new DeadlineExtension(user, session, oldExtension.getEndTime());
        newExtension.setCreatedAt(oldExtension.getCreatedAt());
        newExtension.setUpdatedAt(oldExtension.getUpdatedAt());
        newExtension.setClosingSoonEmailSent(oldExtension.getSentClosingSoonEmail());
        return newExtension;
    }

    private Course getCourse(String courseId) {
        return HibernateUtil.get(Course.class, courseId);
    }

    @Override
    protected void doOperation() {
        log("Running " + getClass().getSimpleName() + "...");
        log("Preview: " + isPreview());

        Cursor cursor = readPositionOfCursorFromFile().orElse(null);
        log(cursor == null ? "Start from the beginning" : "Start from cursor position: " + cursor.toUrlSafe());

        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            Query<teammates.storage.entity.Course> query = getFilterQuery().limit(BATCH_SIZE);
            if (cursor != null) {
                query = query.startAt(cursor);
            }

            QueryResults<?> iterator = query.iterator();
            teammates.storage.entity.Course currentCourse = getNextUnmigratedCourse(iterator);

            while (currentCourse != null) {
                shouldContinue = true;
                try {
                    doMigration(currentCourse);
                } catch (Exception e) {
                    numberOfScannedKey.incrementAndGet();
                    logError(e.getMessage());
                    logProgress();
                    e.printStackTrace();
                    return;
                }
                numberOfScannedKey.incrementAndGet();

                cursor = iterator.getCursorAfter();
                savePositionOfCursorToFile(cursor);

                currentCourse = iterator.hasNext() ? (teammates.storage.entity.Course) iterator.next() : null;
                if (hasReachedMaxCourses()) {
                    currentCourse = null;
                    shouldContinue = false;
                }
            }

            if (shouldContinue) {
                logProgress();
            }
        }

        deleteCursorPositionFile();
        log(isPreview() ? "Preview Completed!" : "Migration Completed!");
        logProgress();
    }

    private teammates.storage.entity.Course getNextUnmigratedCourse(QueryResults<?> iterator) {
        while (iterator.hasNext()) {
            teammates.storage.entity.Course course = (teammates.storage.entity.Course) iterator.next();
            if (course.isMigrated()) {
                continue; // skip already-migrated courses
            }
            deleteCourseCascade(course); // remove dangling SQL course from previous failed migration
            return course; // return so we can migrate it from Datastore
        }
        return null;
    }

    private boolean hasReachedMaxCourses() {
        return getMaxCoursesToMigrate().isPresent()
                && numberOfScannedKey.get() >= getMaxCoursesToMigrate().get();
    }

    private void logProgress() {
        log(String.format("Scanned: %d | Affected: %d | Updated: %d",
                numberOfScannedKey.get(), numberOfAffectedEntities.get(), numberOfUpdatedEntities.get()));
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
     * Truncates a string to a maximum length. Returns null if str is null.
     */
    protected String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
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
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> root = cr.from(Student.class);
        cr.select(root).where(cb.and(
                cb.equal(root.get("courseId"), courseId),
                cb.equal(root.get("email"), email)));
        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    private Instructor getNewInstructor(String courseId, String email) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> root = cr.from(Instructor.class);
        cr.select(root).where(cb.and(
                cb.equal(root.get("courseId"), courseId),
                cb.equal(root.get("email"), email)));
        return HibernateUtil.createQuery(cr).getSingleResult();
    }
}
