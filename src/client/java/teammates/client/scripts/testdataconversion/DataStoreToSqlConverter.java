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

public class DataStoreToSqlConverter {
    private String UUIDPrefix = "00000000-0000-4000-8000-";
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

    private UUIDGenerator accountUUIDGenerator = new UUIDGenerator(initialAccountNumber, UUIDPrefix);
    private UUIDGenerator accounRequestUUIDGenerator = new UUIDGenerator(initialAccountRequestNumber, UUIDPrefix);
    private UUIDGenerator sectionUUIDGenerator = new UUIDGenerator(initialSectionNumber, UUIDPrefix);
    private UUIDGenerator teamUUIDGenerator = new UUIDGenerator(initialTeamNumber, UUIDPrefix);
    private UUIDGenerator deadlineExtensionUUIDGenerator = new UUIDGenerator(initialDeadlineExtensionNumber, UUIDPrefix);
    private UUIDGenerator instructorUUIDGenerator = new UUIDGenerator(initialInstructorNumber, UUIDPrefix);
    private UUIDGenerator studentUUIDGenerator = new UUIDGenerator(initialStudentNumber, UUIDPrefix);
    private UUIDGenerator feedbackSessionUUIDGenerator = new UUIDGenerator(intitialFeedbackSessionNumber, UUIDPrefix);
    private UUIDGenerator feedbackQuestionUUIDGenerator = new UUIDGenerator(initialFeedbackQuestionNumber, UUIDPrefix);
    private UUIDGenerator feedbackResponseUUIDGenerator = new UUIDGenerator(intialFeedbackResponseNumber, UUIDPrefix);
    private UUIDGenerator notificationUUIDGenerator = new UUIDGenerator(initialNotificationNumber, UUIDPrefix);
    private UUIDGenerator readNotificationUUIDGenerator = new UUIDGenerator(initialReadNotificationNumber, UUIDPrefix);

    private long initialFeedbackResponseCommentId = 0;
    private long getNextFeedbackResponseCommentId() {
        long nextId = initialFeedbackResponseCommentId;
        initialFeedbackResponseCommentId += 1;
        return nextId;
    }


    // Maps google id to account
    Map<String, Account> accounts = new HashMap<>();
    // Maps old id to courses
    Map<String, Course> courses = new HashMap<>();
    // Map course%section to section
    Map<String, Section> sections = new HashMap<>();

    // Map course%feedbackSession to feedbackSection
    Map<String, FeedbackSession> feedbackSessions = new HashMap<>();

    // Maps notification id to notification
    Map<String, Notification> notifications = new HashMap<>();

    // Maps question id to question
    Map<String, FeedbackQuestion> feedbackQuestions = new HashMap<>();

    protected DataStoreToSqlConverter() {
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

    // private String generatefeedbackQuestionKey(FeedbackResponse feedbackQuestion) {
    //     return String.format("%s-%s-%s", feedbackQuestion.getCourseId(),
    //         feedbackQuestion.getFeedbackSessionName(), feedbackQuestion.getQuestionNumber());
    // }


    protected Account convert(AccountAttributes accAttr) {
        Account sqlAccount = new Account(accAttr.getGoogleId(),
            accAttr.getName(),
            accAttr.getEmail());

            UUID uuid = accountUUIDGenerator.generateUUID();
            sqlAccount.setId(uuid);

            accounts.put(accAttr.getGoogleId(), sqlAccount);
        return sqlAccount;
    }
    
    protected AccountRequest convert(AccountRequestAttributes accReqAttr) {
        AccountRequest sqlAccountRequest = new AccountRequest(accReqAttr.getEmail(),
            accReqAttr.getName(),
            accReqAttr.getInstitute());

            sqlAccountRequest.setCreatedAt(accReqAttr.getCreatedAt());
            sqlAccountRequest.setRegisteredAt(accReqAttr.getRegisteredAt());
            sqlAccountRequest.setRegistrationKey(accReqAttr.getRegistrationKey());

            UUID uuid = accounRequestUUIDGenerator.generateUUID();
            sqlAccountRequest.setId(uuid);
            return sqlAccountRequest;
    }

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

            UUID uuid = notificationUUIDGenerator.generateUUID();
            sqlNotification.setId(uuid);

            notifications.put(notifAttr.getNotificationId(), sqlNotification);
        return sqlNotification;
    }

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
            sqlFs.setId(feedbackSessionUUIDGenerator.generateUUID());

            feedbackSessions.put(generatefeedbackSessionKey(fsAttr) ,sqlFs);
        return sqlFs;
    }

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
        sqlInstructor.setId(instructorUUIDGenerator.generateUUID());
        sqlInstructor.setAccount(sqlAccount);

        return sqlInstructor;
    }

    protected Student convert(StudentAttributes student) {
        Course sqlCourse = courses.get(student.getCourse());
        Account sqlAccount = accounts.get(student.getGoogleId());


        Student sqlStudent = new Student(sqlCourse,
            student.getName(),
            student.getEmail(),
            student.getComments());
        sqlStudent.setId(studentUUIDGenerator.generateUUID());
        sqlStudent.setAccount(sqlAccount);

        return sqlStudent;
    }

    protected DeadlineExtension convert(DeadlineExtensionAttributes deadlineExtension) {
        FeedbackSession sqlFeedbackSession = feedbackSessions.get(
            generatefeedbackSessionKey(deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName()));

        // User is not included since DataBundleLogic.java does not read users from this attribute
        DeadlineExtension sqlDE = new DeadlineExtension(null,
            sqlFeedbackSession,
            deadlineExtension.getEndTime());
        sqlDE.setClosingSoonEmailSent(deadlineExtension.getSentClosingEmail());
        sqlDE.setCreatedAt(deadlineExtension.getCreatedAt());
        sqlDE.setId(deadlineExtensionUUIDGenerator.generateUUID());

        return sqlDE;
    }

    public FeedbackQuestion convert(FeedbackQuestionAttributes feedbackQuestion) {
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
        sqlFq.setId(feedbackQuestionUUIDGenerator.generateUUID());

        return sqlFq;
    }

    public FeedbackResponse convert(FeedbackResponseAttributes feedbackResponse) {
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
        
        sqlFeedbackResponse.setId(feedbackResponseUUIDGenerator.generateUUID());
        sqlFeedbackResponse.setCreatedAt(feedbackResponse.getCreatedAt());
        
        return sqlFeedbackResponse;
    }

    public FeedbackResponseComment convert(FeedbackResponseCommentAttributes feedbackReponseComment) {
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

    protected Section createSection(StudentAttributes student) {
        Course sqlCourse = courses.get(student.getCourse());
        Section sqlSection = new Section(sqlCourse, student.getSection());
        sqlSection.setId(sectionUUIDGenerator.generateUUID());

        sections.put(generateSectionKey(student), sqlSection);
        
        return sqlSection;
    }


    protected Team createTeam(StudentAttributes student) {
        Section sqlSection = sections.get(generateSectionKey(student));
        Team sqlTeam = new Team(sqlSection, student.getTeam());
        sqlTeam.setId(teamUUIDGenerator.generateUUID());
        
        return sqlTeam;
    }

    protected List<ReadNotification> createReadNotifications(AccountAttributes account) {
        List<ReadNotification> sqlReadNotifications = new ArrayList<>();
        Account sqlAccount = accounts.get(account.getGoogleId());

        account.getReadNotifications().forEach((notifId, endTime) -> {
            Notification sqlNotification = notifications.get(notifId);
            ReadNotification sqlReadNotification = new ReadNotification(sqlAccount, sqlNotification);
            sqlReadNotification.setId(readNotificationUUIDGenerator.generateUUID());
            sqlReadNotifications.add(sqlReadNotification);
        });

        return sqlReadNotifications;
    }
}
