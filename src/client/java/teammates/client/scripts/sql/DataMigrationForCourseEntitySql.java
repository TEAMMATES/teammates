package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
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
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.FileHelper;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
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
import teammates.storage.sqlentity.responses.FeedbackMsqResponse.FeedbackMsqResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse.FeedbackNumericalScaleResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse.FeedbackRankOptionsResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse.FeedbackRankRecipientsResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse.FeedbackRubricResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackTextResponse.FeedbackTextResponseDetailsConverter;

/**
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

    private static final int MAX_RESPONSE_COUNT = -1;

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
        log("Start migrating course with id: " + oldCourse.getUniqueId());
        teammates.storage.sqlentity.Course newCourse = createCourse(oldCourse);

        migrateCourseEntity(newCourse);
        flushEntitiesSavingBuffer();
        // verifyCourseEntity(newCourse);
        // markOldCourseAsMigrated(courseId)
        log("Finish migrating course with id: " + oldCourse.getUniqueId());
    }

    private void migrateCourseEntity(teammates.storage.sqlentity.Course newCourse) {
        Map<String, Section> sectionNameToSectionMap = migrateSectionChain(newCourse);
        // migrateFeedbackChain(newCourse, sectionNameToSectionMap);
    }

    // methods for migrate section chain ----------------------------------------------------------------------------------

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
            saveEntityDeferred(newSection);
            migrateTeams(newCourse, newSection, stuList);   
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
            saveEntityDeferred(newTeam);
            migrateStudents(newCourse, newTeam, stuList);
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

    // methods for migrate feedback chain ---------------------------------------------------------------------------------

    private void migrateFeedbackChain(teammates.storage.sqlentity.Course newCourse,
            Map<String, Section> sectionNameToSectionMap) {

        List<FeedbackSession> oldSessions = ofy().load().type(FeedbackSession.class)
                .filter("courseId", newCourse.getId()).list();
        
        Map<String, List<FeedbackQuestion>> sessionNameToQuestionsMap = ofy().load().type(FeedbackQuestion.class)
                .filter("courseId", newCourse.getId()).list().stream()
                .collect(Collectors.groupingBy(FeedbackQuestion::getFeedbackSessionName));

        for (FeedbackSession oldSession : oldSessions) {
            migrateFeedbackSession(newCourse, oldSession, sessionNameToQuestionsMap, sectionNameToSectionMap);
        }
    }

    private void migrateFeedbackSession(teammates.storage.sqlentity.Course newCourse, FeedbackSession oldSession,
            Map<String, List<FeedbackQuestion>> sessionNameToQuestionsMap, Map<String, Section> sectionNameToSectionMap) {
        teammates.storage.sqlentity.FeedbackSession newSession = createFeedbackSession(newCourse, oldSession);
        saveEntityDeferred(newSession);

        Map<String, List<FeedbackResponse>> questionIdToResponsesMap;
        Query<FeedbackResponse> responsesInSession = ofy().load().type(FeedbackResponse.class)
                .filter("courseId", newCourse.getId())
                .filter("feedbackSessionName", oldSession.getFeedbackSessionName());
        if (responsesInSession.count() <= MAX_RESPONSE_COUNT) {
            questionIdToResponsesMap = responsesInSession.list().stream()
                    .collect(Collectors.groupingBy(FeedbackResponse::getFeedbackQuestionId));
        } else {
            questionIdToResponsesMap = null;
        }

        // cascade migrate questions
        List<FeedbackQuestion> oldQuestions = sessionNameToQuestionsMap.get(oldSession.getFeedbackSessionName());
        for (FeedbackQuestion oldQuestion : oldQuestions) {
            migrateFeedbackQuestion(newSession, oldQuestion, questionIdToResponsesMap, sectionNameToSectionMap);
        }
    }

    private void migrateFeedbackQuestion(teammates.storage.sqlentity.FeedbackSession newSession,
            FeedbackQuestion oldQuestion, Map<String, List<FeedbackResponse>> questionIdToResponsesMap,
            Map<String, Section> sectionNameToSectionMap) {
        teammates.storage.sqlentity.FeedbackQuestion newFeedbackQuestion = createFeedbackQuestion(newSession, oldQuestion);
        saveEntityDeferred(newFeedbackQuestion);

        Map<String, List<FeedbackResponseComment>> responseIdToCommentsMap = ofy().load()
                .type(FeedbackResponseComment.class)
                .filter("courseId", newSession.getCourse().getId())
                .filter("feedbackSessionName", newSession.getName())
                .filter("feedbackQuestionId", oldQuestion.getId()).list().stream()
                .collect(Collectors.groupingBy(FeedbackResponseComment::getFeedbackResponseId));

        // cascade migrate responses
        List<FeedbackResponse> oldResponses;
        if (questionIdToResponsesMap != null) {
            oldResponses = questionIdToResponsesMap.get(oldQuestion.getId());
        } else {
            oldResponses = ofy().load().type(FeedbackResponse.class)
                    .filter("courseId", newSession.getCourse().getId())
                    .filter("feedbackSessionName", newSession.getName())
                    .filter("feedbackQuestionId", oldQuestion.getId()).list();
        }
        for (FeedbackResponse oldResponse : oldResponses) {
            Section newGiverSection = sectionNameToSectionMap.get(oldResponse.getGiverSection());
            Section newRecipientSection = sectionNameToSectionMap.get(oldResponse.getRecipientSection());
            migrateFeedbackResponse(newFeedbackQuestion, oldResponse, newGiverSection,
                    newRecipientSection, responseIdToCommentsMap);
        }
    }

    private void migrateFeedbackResponse(teammates.storage.sqlentity.FeedbackQuestion newQuestion,
            FeedbackResponse oldResponse, Section newGiverSection, Section newRecipientSection,
            Map<String, List<FeedbackResponseComment>> responseIdToCommentsMap) {
        teammates.storage.sqlentity.FeedbackResponse newResponse = createFeedbackResponse(newQuestion, oldResponse,
                newGiverSection, newRecipientSection);
        saveEntityDeferred(newResponse);
        
        // cascade migrate response comments
        List<FeedbackResponseComment> oldComments = responseIdToCommentsMap.get(oldResponse.getId());
        for (FeedbackResponseComment oldComment : oldComments) {
            migrateFeedbackResponseComment(newResponse, oldComment, newGiverSection, newRecipientSection);
        }
    }

    private void migrateFeedbackResponseComment(teammates.storage.sqlentity.FeedbackResponse newResponse,
            FeedbackResponseComment oldComment, Section newGiverSection, Section newRecipientSection) {
        teammates.storage.sqlentity.FeedbackResponseComment newComment = createFeedbackResponseComment(newResponse,
                oldComment, newGiverSection, newRecipientSection);
        saveEntityDeferred(newComment);
    }

    private teammates.storage.sqlentity.FeedbackSession createFeedbackSession(teammates.storage.sqlentity.Course newCourse,
            FeedbackSession oldSession) {
        teammates.storage.sqlentity.FeedbackSession newSession = new teammates.storage.sqlentity.FeedbackSession(
                oldSession.getFeedbackSessionName(),
                newCourse,
                oldSession.getCreatorEmail(),
                oldSession.getInstructions(),
                oldSession.getStartTime(),
                oldSession.getEndTime(),
                oldSession.getSessionVisibleFromTime(),
                oldSession.getResultsVisibleFromTime(),
                Duration.ofMinutes(oldSession.getGracePeriod()),
                oldSession.isOpeningEmailEnabled(),
                oldSession.isClosingEmailEnabled(),
                oldSession.isPublishedEmailEnabled());

        newSession.setClosedEmailSent(oldSession.isSentClosedEmail());
        newSession.setClosingSoonEmailSent(oldSession.isSentClosingEmail());
        newSession.setOpenEmailSent(oldSession.isSentOpenEmail());
        newSession.setOpeningSoonEmailSent(oldSession.isSentOpeningSoonEmail());
        newSession.setPublishedEmailSent(oldSession.isSentPublishedEmail());
        newSession.setCreatedAt(oldSession.getCreatedTime());
        newSession.setUpdatedAt(Instant.now()); // not present in datastore session
        newSession.setDeletedAt(oldSession.getDeletedTime());

        return newSession;
    }

    private teammates.storage.sqlentity.FeedbackQuestion createFeedbackQuestion(
            teammates.storage.sqlentity.FeedbackSession newSession, FeedbackQuestion oldQuestion) {

        teammates.storage.sqlentity.FeedbackQuestion newFeedbackQuestion =
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

        newFeedbackQuestion.setCreatedAt(oldQuestion.getCreatedAt());
        newFeedbackQuestion.setUpdatedAt(oldQuestion.getUpdatedAt());

        return newFeedbackQuestion;
    }

    private FeedbackQuestionDetails getFeedbackQuestionDetails(FeedbackQuestion oldQuestion) {
        switch (oldQuestion.getQuestionType()) {
            case MCQ:
                return new FeedbackMcqQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText());
            case MSQ:
                return new FeedbackMsqQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText());
            case TEXT:
                return new FeedbackTextQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText());
            case RUBRIC:
                return new FeedbackRubricQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText()); 
            case CONTRIB:
                return new FeedbackContributionQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText()); 
            case CONSTSUM:
            case CONSTSUM_RECIPIENTS:
            case CONSTSUM_OPTIONS:
                return new FeedbackConstantSumQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText()); 
            case NUMSCALE:
                return new FeedbackNumericalScaleQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText());
            case RANK_OPTIONS:
                return new FeedbackRankOptionsQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText()); 
            case RANK_RECIPIENTS:
                return new FeedbackRankRecipientsQuestionDetailsConverter()
                        .convertToEntityAttribute(oldQuestion.getQuestionText()); 
            default:
                throw new IllegalArgumentException("Invalid question type");
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
        switch(oldResponse.getFeedbackQuestionType()) {
            case MCQ:
                return new FeedbackTextResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case MSQ:
                return new FeedbackMsqResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case TEXT:
                return new FeedbackTextResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case RUBRIC:
                return new FeedbackRubricResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case CONTRIB:
                return new FeedbackContributionResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case CONSTSUM:
            case CONSTSUM_RECIPIENTS:
            case CONSTSUM_OPTIONS:
                return new FeedbackConstantSumResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case NUMSCALE:
                return new FeedbackNumericalScaleResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case RANK_OPTIONS:
                return new FeedbackRankOptionsResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            case RANK_RECIPIENTS:
                return new FeedbackRankRecipientsResponseDetailsConverter()
                        .convertToEntityAttribute(oldResponse.getAnswer());
            default:
                throw new IllegalArgumentException("Invalid response type");
        }
    }

    private teammates.storage.sqlentity.FeedbackResponseComment createFeedbackResponseComment(
            teammates.storage.sqlentity.FeedbackResponse newResponse, FeedbackResponseComment oldComment,
            Section giverSection, Section recipientSection) {
        teammates.storage.sqlentity.FeedbackResponseComment newComment =
                new teammates.storage.sqlentity.FeedbackResponseComment(
                        newResponse,
                        oldComment.getGiverEmail(),
                        oldComment.getCommentGiverType(),
                        giverSection,
                        recipientSection,
                        oldComment.getCommentText(),
                        oldComment.getIsVisibilityFollowingFeedbackQuestion(),
                        oldComment.getIsCommentFromFeedbackParticipant(),
                        oldComment.getShowCommentTo(),
                        oldComment.getShowGiverNameTo(),
                        oldComment.getLastEditorEmail());

        newComment.setCreatedAt(oldComment.getCreatedAt());
        newComment.setUpdatedAt(oldComment.getLastEditedAt());

        return newComment;
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
        String courseId = oldCourse.getUniqueId();
        
        HibernateUtil.beginTransaction();
        teammates.storage.sqlentity.Course newCourse = HibernateUtil.get(teammates.storage.sqlentity.Course.class, courseId);
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
