package teammates.client.scripts.testdataconversion;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * Helper class to convert entities from its noSQL to SQL format.
 */
public class DataStoreToSqlConverter {
    private String uuidPrefix = "00000000-0000-4000-8000-";
    private int initialAccountNumber = 1;
    private int initialAccountRequestNumber = 101;
    private int initialSectionNumber = 201;
    private int initialTeamNumber = 301;
    private int initialDeadlineExtensionNumber = 401;
    private int initialInstructorNumber = 501;
    private int initialStudentNumber = 601;
    private int intitialFeedbackSessionNumber = 701;
    private int initialFeedbackQuestionNumber = 801;
    private int intialFeedbackResponseNumber = 901;
    private int initialNotificationNumber = 1101;
    private int initialReadNotificationNumber = 1201;

    private UuidGenerator accountUuidGenerator = new UuidGenerator(initialAccountNumber, uuidPrefix);
    private UuidGenerator accounRequestUuidGenerator = new UuidGenerator(initialAccountRequestNumber, uuidPrefix);
    private UuidGenerator sectionUuidGenerator = new UuidGenerator(initialSectionNumber, uuidPrefix);
    private UuidGenerator teamUuidGenerator = new UuidGenerator(initialTeamNumber, uuidPrefix);
    private UuidGenerator deadlineExtensionUuidGenerator = new UuidGenerator(initialDeadlineExtensionNumber, uuidPrefix);
    private UuidGenerator instructorUuidGenerator = new UuidGenerator(initialInstructorNumber, uuidPrefix);
    private UuidGenerator studentUuidGenerator = new UuidGenerator(initialStudentNumber, uuidPrefix);
    private UuidGenerator feedbackSessionUuidGenerator = new UuidGenerator(intitialFeedbackSessionNumber, uuidPrefix);
    private UuidGenerator feedbackQuestionUuidGenerator = new UuidGenerator(initialFeedbackQuestionNumber, uuidPrefix);
    private UuidGenerator feedbackResponseUuidGenerator = new UuidGenerator(intialFeedbackResponseNumber, uuidPrefix);
    private UuidGenerator notificationUuidGenerator = new UuidGenerator(initialNotificationNumber, uuidPrefix);
    private UuidGenerator readNotificationUuidGenerator = new UuidGenerator(initialReadNotificationNumber, uuidPrefix);

    private long initialFeedbackResponseCommentId;

    // Maps google id to account
    private Map<String, Account> accounts = new HashMap<>();
    // Maps old id to courses
    private Map<String, Course> courses = new HashMap<>();

    // Maps question id to question
    private Map<String, FeedbackQuestion> feedbackQuestions = new HashMap<>();

    // Map course%feedbackSession to feedbackSection
    private Map<String, FeedbackSession> feedbackSessions = new HashMap<>();

    // Maps notification id to notification
    private Map<String, Notification> notifications = new HashMap<>();

    // Map course%section to section
    private Map<String, Section> sections = new HashMap<>();

    private long getNextFeedbackResponseCommentId() {
        long nextId = initialFeedbackResponseCommentId;
        initialFeedbackResponseCommentId += 1;
        return nextId;
    }

    private String generateSectionKey(StudentAttributes student) {
        return String.format("%s-%s", student.getCourse(), student.getSection());
    }

    private String generateSectionKey(String courseId, String sectionName) {
        return String.format("%s-%s", courseId, sectionName);
    }

    private String generatefeedbackSessionKey(FeedbackSessionAttributes feedbackSession) {
        return String.format("%s-%s", feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName());
    }

    private String generatefeedbackSessionKey(String courseId, String feedbackSessionName) {
        return String.format("%s-%s", courseId, feedbackSessionName);
    }

    /**
     * Converts Account from its noSQL to SQL entity.
     */
    protected Account convert(AccountAttributes accAttr) {
        Account sqlAccount = new Account(accAttr.getGoogleId(),
                accAttr.getName(),
                accAttr.getEmail());

        UUID uuid = accountUuidGenerator.generateUuid();
        sqlAccount.setId(uuid);

        accounts.put(accAttr.getGoogleId(), sqlAccount);

        return sqlAccount;
    }

    /**
     * Converts Account Request from its noSQL to SQL entity.
     */
    protected AccountRequest convert(AccountRequestAttributes accReqAttr) {
        AccountRequest sqlAccountRequest = new AccountRequest(accReqAttr.getEmail(),
                accReqAttr.getName(),
                accReqAttr.getInstitute());

        sqlAccountRequest.setCreatedAt(accReqAttr.getCreatedAt());
        sqlAccountRequest.setRegisteredAt(accReqAttr.getRegisteredAt());
        sqlAccountRequest.setRegistrationKey(accReqAttr.getRegistrationKey());

        UUID uuid = accounRequestUuidGenerator.generateUuid();
        sqlAccountRequest.setId(uuid);

        return sqlAccountRequest;
    }

    /**
     * Converts Course from its noSQL to SQL entity.
     */
    protected Course convert(CourseAttributes courseAttr) {
        Course sqlCourse = new Course(courseAttr.getId(),
                courseAttr.getName(),
                courseAttr.getTimeZone(),
                courseAttr.getInstitute());

        sqlCourse.setDeletedAt(courseAttr.getDeletedAt());
        sqlCourse.setCreatedAt(courseAttr.getCreatedAt());

        courses.put(courseAttr.getId(), sqlCourse);

        return sqlCourse;
    }

    /**
     * Converts Notification from its noSQL to SQL entity.
     */
    protected Notification convert(NotificationAttributes notifAttr) {
        Notification sqlNotification = new Notification(notifAttr.getStartTime(),
                notifAttr.getEndTime(),
                notifAttr.getStyle(),
                notifAttr.getTargetUser(),
                notifAttr.getTitle(),
                notifAttr.getMessage());

        sqlNotification.setCreatedAt(notifAttr.getCreatedAt());

        if (notifAttr.isShown()) {
            sqlNotification.setShown();
        }

        UUID uuid = notificationUuidGenerator.generateUuid();
        sqlNotification.setId(uuid);

        notifications.put(notifAttr.getNotificationId(), sqlNotification);

        return sqlNotification;
    }

    /**
     * Converts Feedback Session from its noSQL to SQL entity.
     */
    protected FeedbackSession convert(FeedbackSessionAttributes fsAttr) {
        Duration gracePeriod = Duration.ofMinutes(fsAttr.getGracePeriodMinutes());
        Course sqlCourse = courses.get(fsAttr.getCourseId());
        FeedbackSession sqlFs = new FeedbackSession(
                fsAttr.getFeedbackSessionName(),
                sqlCourse,
                fsAttr.getCreatorEmail(),
                fsAttr.getInstructions(),
                fsAttr.getStartTime(),
                fsAttr.getEndTime(),
                fsAttr.getSessionVisibleFromTime(),
                fsAttr.getResultsVisibleFromTime(),
                gracePeriod,
                fsAttr.isOpeningEmailEnabled(),
                fsAttr.isClosingEmailEnabled(),
                fsAttr.isPublishedEmailEnabled());

        sqlFs.setCreatedAt(fsAttr.getCreatedTime());
        sqlFs.setDeletedAt(fsAttr.getDeletedTime());
        sqlFs.setId(feedbackSessionUuidGenerator.generateUuid());

        feedbackSessions.put(generatefeedbackSessionKey(fsAttr), sqlFs);

        return sqlFs;
    }

    /**
     * Converts Instructor from its noSQL to SQL entity.
     */
    protected Instructor convert(InstructorAttributes instructor) {
        Course sqlCourse = courses.get(instructor.getCourseId());
        Account sqlAccount = accounts.get(instructor.getGoogleId());

        InstructorPermissionRole role = InstructorPermissionRole.getEnum(instructor.getRole());

        Instructor sqlInstructor = new Instructor(sqlCourse,
                instructor.getName(),
                instructor.getEmail(),
                instructor.isDisplayedToStudents(),
                instructor.getDisplayedName(),
                role,
                instructor.getPrivileges());
        sqlInstructor.setId(instructorUuidGenerator.generateUuid());
        sqlInstructor.setAccount(sqlAccount);

        return sqlInstructor;
    }

    /**
     * Converts Student from its noSQL to SQL entity.
     */
    protected Student convert(StudentAttributes student) {
        Course sqlCourse = courses.get(student.getCourse());
        Account sqlAccount = accounts.get(student.getGoogleId());

        Student sqlStudent = new Student(sqlCourse,
                student.getName(),
                student.getEmail(),
                student.getComments());

        sqlStudent.setId(studentUuidGenerator.generateUuid());
        sqlStudent.setAccount(sqlAccount);

        return sqlStudent;
    }

    /**
     * Converts Deadline Extension from its noSQL to SQL entity.
     */
    protected DeadlineExtension convert(DeadlineExtensionAttributes deadlineExtension) {
        FeedbackSession sqlFeedbackSession = feedbackSessions.get(
                generatefeedbackSessionKey(deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName()));

        // User is not included since DataBundleLogic.java does not read users from this attribute
        DeadlineExtension sqlDE = new DeadlineExtension(null,
                sqlFeedbackSession,
                deadlineExtension.getEndTime());

        sqlDE.setClosingSoonEmailSent(deadlineExtension.getSentClosingEmail());
        sqlDE.setCreatedAt(deadlineExtension.getCreatedAt());
        sqlDE.setId(deadlineExtensionUuidGenerator.generateUuid());

        return sqlDE;
    }

    /**
     * Converts Feedback Question from its noSQL to SQL entity.
     */
    protected FeedbackQuestion convert(FeedbackQuestionAttributes feedbackQuestion) {
        FeedbackSession sqlFeedbackSession = feedbackSessions.get(
                generatefeedbackSessionKey(feedbackQuestion.getCourseId(), feedbackQuestion.getFeedbackSessionName()));

        FeedbackQuestion sqlFq = FeedbackQuestion.makeQuestion(sqlFeedbackSession,
                feedbackQuestion.getQuestionNumber(),
                feedbackQuestion.getQuestionDescription(),
                feedbackQuestion.getGiverType(),
                feedbackQuestion.getRecipientType(),
                feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo(),
                feedbackQuestion.getShowResponsesTo(),
                feedbackQuestion.getShowGiverNameTo(),
                feedbackQuestion.getShowRecipientNameTo(),
                feedbackQuestion.getQuestionDetails());

        sqlFq.setCreatedAt(feedbackQuestion.getCreatedAt());
        sqlFq.setId(feedbackQuestionUuidGenerator.generateUuid());

        return sqlFq;
    }

    /**
     * Converts Feedback Response from its noSQL to SQL entity.
     */
    protected FeedbackResponse convert(FeedbackResponseAttributes feedbackResponse) {
        FeedbackQuestion sqlFeedbackQuestion = feedbackQuestions.get(feedbackResponse.getFeedbackQuestionId());

        Section sqlGiverSection = sections.get(generateSectionKey(feedbackResponse.getCourseId(),
                feedbackResponse.getGiverSection()));

        Section sqlReceiverSection = sections.get(generateSectionKey(feedbackResponse.getCourseId(),
                feedbackResponse.getRecipientSection()));

        FeedbackResponse sqlFeedbackResponse = FeedbackResponse.makeResponse(
                sqlFeedbackQuestion,
                feedbackResponse.getGiver(),
                sqlGiverSection,
                feedbackResponse.getRecipient(),
                sqlReceiverSection,
                feedbackResponse.getResponseDetails());

        sqlFeedbackResponse.setId(feedbackResponseUuidGenerator.generateUuid());
        sqlFeedbackResponse.setCreatedAt(feedbackResponse.getCreatedAt());

        return sqlFeedbackResponse;
    }

    /**
     * Converts Feedback Response Comment from its noSQL to SQL entity.
     */
    protected FeedbackResponseComment convert(FeedbackResponseCommentAttributes feedbackReponseComment) {
        Section sqlGiverSection = sections.get(generateSectionKey(feedbackReponseComment.getCourseId(),
                feedbackReponseComment.getGiverSection()));

        Section sqlReceiverSection = sections.get(generateSectionKey(feedbackReponseComment.getCourseId(),
                feedbackReponseComment.getReceiverSection()));

        FeedbackResponseComment sqlFrc = new FeedbackResponseComment(null,
                feedbackReponseComment.getCommentGiver(),
                feedbackReponseComment.getCommentGiverType(),
                sqlGiverSection,
                sqlReceiverSection,
                feedbackReponseComment.getCommentText(),
                feedbackReponseComment.isVisibilityFollowingFeedbackQuestion(),
                feedbackReponseComment.isCommentFromFeedbackParticipant(),
                feedbackReponseComment.getShowCommentTo(),
                feedbackReponseComment.getShowGiverNameTo(),
                feedbackReponseComment.getLastEditorEmail());

        sqlFrc.setId(getNextFeedbackResponseCommentId());
        sqlFrc.setCreatedAt(feedbackReponseComment.getCreatedAt());

        return sqlFrc;
    }

    /**
     * Creates SQL Section from noSQL Student attribute.
     */
    protected Section createSection(StudentAttributes student) {
        Course sqlCourse = courses.get(student.getCourse());
        Section sqlSection = new Section(sqlCourse, student.getSection());

        sqlSection.setId(sectionUuidGenerator.generateUuid());

        sections.put(generateSectionKey(student), sqlSection);

        return sqlSection;
    }

    /**
     * Creates SQL Team from noSQL Student attribute.
     */
    protected Team createTeam(StudentAttributes student) {
        Section sqlSection = sections.get(generateSectionKey(student));
        Team sqlTeam = new Team(sqlSection, student.getTeam());
        sqlTeam.setId(teamUuidGenerator.generateUuid());

        return sqlTeam;
    }

    /**
     * Creates SQL Read Notifications from Account attributes.
     */
    protected List<ReadNotification> createReadNotifications(AccountAttributes account) {
        List<ReadNotification> sqlReadNotifications = new ArrayList<>();
        Account sqlAccount = accounts.get(account.getGoogleId());

        account.getReadNotifications().forEach((notifId, endTime) -> {
            Notification sqlNotification = notifications.get(notifId);
            ReadNotification sqlReadNotification = new ReadNotification(sqlAccount, sqlNotification);
            sqlReadNotification.setId(readNotificationUuidGenerator.generateUuid());
            sqlReadNotifications.add(sqlReadNotification);
        });

        return sqlReadNotifications;
    }
}
