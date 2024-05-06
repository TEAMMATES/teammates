package teammates.client.scripts.sql;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
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
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.sqlentity.User;
import teammates.storage.sqlentity.DeadlineExtension;
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
import teammates.test.FileHelper;

/**
 */
@SuppressWarnings({ "PMD", "deprecation" })
public class DataMigrationForCourseEntitySql extends DatastoreClient {

    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";

    private static final int BATCH_SIZE = 100;

    private static final int MAX_BUFFER_SIZE = 1000;

    private List<BaseEntity> entitiesSavingBuffer;

    private Logger logger;

    // Creates the folder that will contain the stored log.
    static {
        new File(BASE_LOG_URI).mkdir();
    }

    AtomicLong numberOfAffectedEntities;
    AtomicLong numberOfScannedKey;
    AtomicLong numberOfUpdatedEntities;

    private static final int MAX_RESPONSE_COUNT = -1;

    private VerifyCourseEntityAttributes verifier;

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

        // TODO: markOldCourseAsMigrated(courseId)

        log("Finish migrating course with id: " + oldCourse.getUniqueId());
    }

    private void migrateCourseDependencies(String newCourseId) {
        Map<String, User> userGoogleIdToUserMap = new HashMap<>();
        Map<String, Instructor> emailToInstructorMap = new HashMap<>();
        Map<String, Student> emailToStudentMap = new HashMap<>();
        migrateSectionChain(newCourseId, userGoogleIdToUserMap, emailToStudentMap);
        migrateFeedbackChain(newCourseId);
        // Map<String, teammates.storage.sqlentity.FeedbackSession> feedbackSessionNameToFeedbackSessionMap =
                // migrateFeedbackChain(newCourse, sectionNameToSectionMap);
        migrateInstructorEntities(newCourseId, userGoogleIdToUserMap);
        // migrateUserAccounts(newCourse, userGoogleIdToUserMap);
        migrateDeadlineExtensionEntities(newCourseId);
    }

    // methods for migrate section chain ----------------------------------------------------------------------------------
    // entities: Section, Team, Student

    private void migrateSectionChain(
            String courseId, Map<String, User> userGoogleIdToUserMap, Map<String, Student> emailToStudentMap) {
        log(String.format("Migrating section chain for %s", courseId));
        
        migrateSections(courseId);
        migrateTeams(courseId);
        migrateStudents(courseId);

        // Map<String, Section> sections = new HashMap<>();
        // Map<String, List<CourseStudent>> sectionToStuMap = oldStudents.stream()
        //         .collect(Collectors.groupingBy(CourseStudent::getSectionName));

        // for (Map.Entry<String, List<CourseStudent>> entry : sectionToStuMap.entrySet()) {
        //     String sectionName = entry.getKey();
        //     List<CourseStudent> stuList = entry.getValue();
        //     // Section newSection = createSection(newCourse, sectionName);
        //     sections.put(sectionName, newSection);
        //     saveEntityDeferred(newSection);
        //     // migrateTeams(newCourse, newSection, stuList, userGoogleIdToUserMap, emailToStudentMap);
        // }
    }

    // private void migrateTeams(Course newCourse,
    //         Section newSection, List<CourseStudent> studentsInSection,
    //         Map<String, User> userGoogleIdToUserMap, Map<String, Student> emailToStudentMap) {
    //     Map<String, List<CourseStudent>> teamNameToStuMap = studentsInSection.stream()
    //             .collect(Collectors.groupingBy(CourseStudent::getTeamName));
    //     for (Map.Entry<String, List<CourseStudent>> entry : teamNameToStuMap.entrySet()) {
    //         String teamName = entry.getKey();
    //         List<CourseStudent> stuList = entry.getValue();
    //         teammates.storage.sqlentity.Team newTeam = createTeam(newSection, teamName);
    //         saveEntityDeferred(newTeam);
    //         migrateStudents(newCourse, newTeam, stuList, userGoogleIdToUserMap, emailToStudentMap);
    //     }
    // }

    // private void migrateStudents(Course newCourse, teammates.storage.sqlentity.Team newTeam,
    //         List<CourseStudent> studentsInTeam, Map<String, User> userGoogleIdToUserMap, Map<String, Student> emailToStudentMap) {
    //     for (CourseStudent oldStudent : studentsInTeam) {
    //         teammates.storage.sqlentity.Student newStudent = migrateStudent(newCourse, newTeam, oldStudent);
    //         emailToStudentMap.put(newStudent.getEmail(), newStudent);
    //         if (oldStudent.getGoogleId() != null) {
    //             userGoogleIdToUserMap.put(oldStudent.getGoogleId(), newStudent);
    //         }
    //     }
    // }

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

        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId)
            .list();

        Course newCourse = getCourse(courseId);
        
        List<String> sectionNames = oldStudents.stream().map(student -> student.getSectionName())
            .distinct().collect(Collectors.toList());

        for (String sectionName : sectionNames) {
            Section newSection = createSection(newCourse, sectionName);
            HibernateUtil.persist(newSection);
        }
        HibernateUtil.commitTransaction();
    }

    private void migrateTeams(String courseId) {
        log(String.format("Migrating Teams for course %s", courseId));
        HibernateUtil.beginTransaction();

        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId)
            .list();

        // Assume that team names are unique within a section but not unique among all sections
        Map<String, HashSet<String>> oldSectionToTeamHashSet = new HashMap<String, HashSet<String>>();
        for (CourseStudent student : oldStudents) {
            String sectionName = student.getSectionName();
            oldSectionToTeamHashSet.putIfAbsent(sectionName, new HashSet<>());
            HashSet<String> teamHashSet = oldSectionToTeamHashSet.get(sectionName);
            teamHashSet.add(student.getTeamName());
        }

        for (Entry<String, HashSet<String>> entrySet : oldSectionToTeamHashSet.entrySet()) {
            String oldSectionName = entrySet.getKey();
            HashSet<String> oldTeams = entrySet.getValue();
            Section newSection = getSection(courseId, oldSectionName);
            for (String teamName : oldTeams) {
                Team newTeam = createTeam(newSection, teamName);
                HibernateUtil.persist(newTeam);
            }
        }

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
        Map<String, HashSet<String>> sectionToTeamNameMap = new HashMap<String, HashSet<String>>();
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
        Map<String, HashMap<String, Team>> newSectionToTeamEntityMap = new HashMap<String, HashMap<String, Team>>();
        for (Entry<String, HashSet<String>> entry :sectionToTeamNameMap.entrySet()) {
            String sectionName = entry.getKey();
            for (String teamName : entry.getValue()) {
                Team newTeam = getTeam(courseId, sectionName, teamName);

                newSectionToTeamEntityMap.putIfAbsent(sectionName, new HashMap<String, Team>());
                newSectionToTeamEntityMap.get(sectionName).putIfAbsent(teamName, newTeam);
            }
        }

        Map<String, Account> googleIdToAccountMap = new HashMap<String, Account>();

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

    private Section createSection(Course newCourse,
            String sectionName) {
        String truncatedName = truncateToLength255(sectionName);
        Section newSection = new Section(newCourse, truncatedName);
        newSection.setCreatedAt(Instant.now());
        return newSection;
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

    private Section getSection(String courseId, String sectionName)  {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Section> cr = cb.createQuery(Section.class);
        Root<Section> sectionRoot = cr.from(Section.class);
        cr.where(cb.and(cb.equal(sectionRoot.get("course").get("id"), courseId), 
            cb.equal(sectionRoot.get("name"), sectionName)));

        return HibernateUtil.createQuery(cr).getSingleResult();
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

    private teammates.storage.sqlentity.Team createTeam(Section section, String teamName) {
        String truncatedTeamName = truncateToLength255(teamName);
        Team newTeam = new teammates.storage.sqlentity.Team(section, truncatedTeamName);
        newTeam.setCreatedAt(Instant.now());
        return newTeam;
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

    // methods for migrate feedback chain ---------------------------------------------------------------------------------
    // entities: FeedbackSession, FeedbackQuestion, FeedbackResponse, FeedbackResponseComment

    private void migrateFeedbackChain(String courseId) {
        log("Migrating feedback chain");
        // Map<String, teammates.storage.sqlentity.FeedbackSession> feedbackSessionNameToFeedbackSessionMap =
        //         new HashMap<>();

        migrateFeedbackSessions(courseId);
        // Map<String, List<FeedbackQuestion>> sessionNameToQuestionsMap = ofy().load().type(FeedbackQuestion.class)
        //         .filter("courseId", newCourse.getId()).list().stream()
        //         .collect(Collectors.groupingBy(FeedbackQuestion::getFeedbackSessionName));

        // for (FeedbackSession oldSession : oldSessions) {
        //     teammates.storage.sqlentity.FeedbackSession newSession = migrateFeedbackSession(newCourse, oldSession,
        //             sessionNameToQuestionsMap, sectionNameToSectionMap);
        //     feedbackSessionNameToFeedbackSessionMap.put(oldSession.getFeedbackSessionName(), newSession);
        // }
        // return feedbackSessionNameToFeedbackSessionMap;
    }

    private void migrateFeedbackSessions(String courseId) {
        HibernateUtil.beginTransaction();
        List<teammates.storage.entity.FeedbackSession> oldFeedbackSessions = ofy().load().type(teammates.storage.entity.FeedbackSession.class)
            .filter("courseId", courseId).list();

        Course newCourse = getCourse(courseId);

        Map<String, List<FeedbackQuestion>> feedbackSessionNameToQuestionsMap = ofy().load().type(FeedbackQuestion.class)
                .filter("courseId", courseId)
                .list().stream()
                .collect(Collectors.groupingBy(FeedbackQuestion::getFeedbackSessionName));

        Map<String, Section> sectionNameToSectionMap = new HashMap<String, Section>();
        List<Section> newSections = getSections(courseId);
        for (Section newSection : newSections) {
            sectionNameToSectionMap.put(newSection.getName(), newSection);
        }
        
        for (teammates.storage.entity.FeedbackSession oldFeedbackSession : oldFeedbackSessions) {
            FeedbackSession newFeedbackSession = createFeedbackSession(newCourse, oldFeedbackSession);
            HibernateUtil.persist(newFeedbackSession);

            String oldFeedbackSessionName = oldFeedbackSession.getFeedbackSessionName();

            // Query<FeedbackResponse> responsesInSession = ofy().load().type(FeedbackResponse.class)
            //     .filter("courseId", courseId)
            //     .filter("feedbackSessionName", oldFeedbackSessionName);
                
            List<FeedbackQuestion> oldQuestions = feedbackSessionNameToQuestionsMap.get(oldFeedbackSessionName);
            for (FeedbackQuestion oldQuestion : oldQuestions) {
                migrateFeedbackQuestion(newFeedbackSession, oldQuestion, sectionNameToSectionMap);
            }

        }

        HibernateUtil.commitTransaction();
    }

    // private teammates.storage.sqlentity.FeedbackSession migrateFeedbackSession(
    //         Course newCourse, FeedbackSession oldSession,
    //         Map<String, List<FeedbackQuestion>> sessionNameToQuestionsMap,
    //         Map<String, Section> sectionNameToSectionMap) {
    //     teammates.storage.sqlentity.FeedbackSession newSession = createFeedbackSession(newCourse, oldSession);
    //     saveEntityDeferred(newSession);

    //     Map<String, List<FeedbackResponse>> questionIdToResponsesMap;
    //     Query<FeedbackResponse> responsesInSession = ofy().load().type(FeedbackResponse.class)
    //             .filter("courseId", oldSession.getCourseId())
    //             .filter("feedbackSessionName", oldSession.getFeedbackSessionName());
    //     if (responsesInSession.count() <= MAX_RESPONSE_COUNT) {
    //         questionIdToResponsesMap = responsesInSession.list().stream()
    //                 .collect(Collectors.groupingBy(FeedbackResponse::getFeedbackQuestionId));
    //     } else {
    //         questionIdToResponsesMap = null;
    //     }

    //     // cascade migrate questions
    //     List<FeedbackQuestion> oldQuestions = sessionNameToQuestionsMap.get(oldSession.getFeedbackSessionName());
    //     for (FeedbackQuestion oldQuestion : oldQuestions) {
    //         migrateFeedbackQuestion(newSession, oldQuestion, questionIdToResponsesMap, sectionNameToSectionMap);
    //     }
    //     return newSession;
    // }

    private void migrateFeedbackQuestion(teammates.storage.sqlentity.FeedbackSession newSession, FeedbackQuestion oldQuestion, Map<String, Section> sectionNameToSectionMap) {
        teammates.storage.sqlentity.FeedbackQuestion newFeedbackQuestion = createFeedbackQuestion(newSession,
                oldQuestion);
        HibernateUtil.persist(newFeedbackQuestion);

        Map<String, List<FeedbackResponseComment>> responseIdToCommentsMap = ofy().load()
                .type(FeedbackResponseComment.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list().stream()
                .collect(Collectors.groupingBy(FeedbackResponseComment::getFeedbackResponseId));

        // cascade migrate responses
        List<FeedbackResponse> oldResponses;
        oldResponses = ofy().load().type(FeedbackResponse.class)
            .filter("feedbackQuestionId", oldQuestion.getId()).list();

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
        HibernateUtil.persist(newResponse);

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
        HibernateUtil.persist(newComment);
    }

    private FeedbackSession createFeedbackSession(Course newCourse,
            teammates.storage.entity.FeedbackSession oldSession) {
        String truncatedSessionInstructions = truncateToLength2000(oldSession.getInstructions());
            
        teammates.storage.sqlentity.FeedbackSession newSession = new teammates.storage.sqlentity.FeedbackSession(
                oldSession.getFeedbackSessionName(),
                newCourse,
                oldSession.getCreatorEmail(),
                truncatedSessionInstructions,
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
        // newSession.setUpdatedAt(Instant.now()); // not present in datastore session
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
        switch (oldResponse.getFeedbackQuestionType()) {
        case MCQ:
            return new FeedbackTextResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        case MSQ:
            return new FeedbackMsqResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        case TEXT:
            // Response details for TEXT questions are not stored as json.
            // Refer to FeedbackResponseDetails#getJsonString
            return new FeedbackTextResponseDetails(oldResponse.getAnswer());
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
        String truncatedCommentText = truncateToLength2000(oldComment.getCommentText());

        teammates.storage.sqlentity.FeedbackResponseComment newComment =
                new teammates.storage.sqlentity.FeedbackResponseComment(
                        newResponse,
                        oldComment.getGiverEmail(),
                        oldComment.getCommentGiverType(),
                        giverSection,
                        recipientSection,
                        truncatedCommentText,
                        oldComment.getIsVisibilityFollowingFeedbackQuestion(),
                        oldComment.getIsCommentFromFeedbackParticipant(),
                        oldComment.getShowCommentTo(),
                        oldComment.getShowGiverNameTo(),
                        oldComment.getLastEditorEmail());

        newComment.setCreatedAt(oldComment.getCreatedAt());
        newComment.setUpdatedAt(oldComment.getLastEditedAt());

        return newComment;
    }

    // methods for misc migration methods ---------------------------------------------------------------------------------
    // entities: Instructor, DeadlineExtension

    private void migrateInstructorEntities(String courseId,
            Map<String, User> userGoogleIdToUserMap) {
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
            new HashMap<String, FeedbackSession>();
        
        for (FeedbackSession newFeedbackSession : getCourse(courseId).getFeedbackSessions()) {
            feedbackSessionNameToFeedbackSessionMap.put(newFeedbackSession.getName(), newFeedbackSession);
        }
        
        for (teammates.storage.entity.DeadlineExtension oldDeadlineExtension : oldDeadlineExtensions) {
            String userEmail = oldDeadlineExtension.getUserEmail();
            String feedbackSessionName = oldDeadlineExtension.getFeedbackSessionName();
            teammates.storage.sqlentity.FeedbackSession feedbackSession = feedbackSessionNameToFeedbackSessionMap.get(feedbackSessionName);
            User user = null;
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
        newDeadlineExtension.setClosingSoonEmailSent(oldDeadlineExtension.getSentClosingEmail());

        return newDeadlineExtension;
    }

    // Associate account to users(students and instructors) who have the same matching google id
    // private void migrateUserAccounts(Course newCourse, Map<String, User> userGoogleIdToUserMap) {
    //     List<Account> newAccounts = getAllAccounts(new ArrayList<String>(userGoogleIdToUserMap.keySet()));
    //     if (newAccounts.size() != userGoogleIdToUserMap.size()) {
    //         log("Mismatch in number of accounts: " + newAccounts.size() + " vs " + userGoogleIdToUserMap.size());
    //     }
    //     for (Account account: newAccounts) {
    //         User newUser = userGoogleIdToUserMap.get(account.getGoogleId());
    //         if (newUser == null) {
    //             log("User not found for account: " + account.getGoogleId());
    //             continue;
    //         }
    //         newUser.setGoogleId(account.getGoogleId());
    //         newUser.setAccount(account);
    //         saveEntityDeferred(newUser);
    //     }
    // }

    // private List<Account> getAllAccounts(List<String> userGoogleIds) {
    //     HibernateUtil.beginTransaction();
    //     CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
    //     CriteriaQuery<Account> cr = cb.createQuery(Account.class);
    //     Root<Account> accountRoot = cr.from(Account.class);
    //     cr.select(accountRoot).where(cb.in(accountRoot.get("googleId")).value(userGoogleIds));
    //     List<Account> newAccounts = HibernateUtil.createQuery(cr).getResultList();
    //     HibernateUtil.commitTransaction();
    //     return newAccounts;
    // }

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
                    currentOldCourse = (teammates.storage.entity.Course) iterator.next();
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
