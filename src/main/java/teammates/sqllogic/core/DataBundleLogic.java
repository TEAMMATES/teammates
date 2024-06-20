package teammates.sqllogic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.User;

/**
 * Handles operations related to data bundles.
 *
 * @see DataBundle
 */
public final class DataBundleLogic {

    private static final DataBundleLogic instance = new DataBundleLogic();

    private AccountsLogic accountsLogic;
    private AccountRequestsLogic accountRequestsLogic;
    private CoursesLogic coursesLogic;
    private DeadlineExtensionsLogic deadlineExtensionsLogic;
    private FeedbackSessionsLogic fsLogic;
    private FeedbackSessionLogsLogic fslLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponsesLogic frLogic;
    private FeedbackResponseCommentsLogic frcLogic;
    private NotificationsLogic notificationsLogic;
    private UsersLogic usersLogic;

    private DataBundleLogic() {
        // prevent initialization
    }

    public static DataBundleLogic inst() {
        return instance;
    }

    void initLogicDependencies(AccountsLogic accountsLogic, AccountRequestsLogic accountRequestsLogic,
            CoursesLogic coursesLogic, DeadlineExtensionsLogic deadlineExtensionsLogic, FeedbackSessionsLogic fsLogic,
            FeedbackSessionLogsLogic fslLogic, FeedbackQuestionsLogic fqLogic, FeedbackResponsesLogic frLogic,
            FeedbackResponseCommentsLogic frcLogic, NotificationsLogic notificationsLogic, UsersLogic usersLogic) {
        this.accountsLogic = accountsLogic;
        this.accountRequestsLogic = accountRequestsLogic;
        this.coursesLogic = coursesLogic;
        this.deadlineExtensionsLogic = deadlineExtensionsLogic;
        this.fsLogic = fsLogic;
        this.fslLogic = fslLogic;
        this.fqLogic = fqLogic;
        this.frLogic = frLogic;
        this.frcLogic = frcLogic;
        this.notificationsLogic = notificationsLogic;
        this.usersLogic = usersLogic;
    }

    /**
     * Deserialize JSON into a data bundle.
     *
     * <p>NOTE: apart from for Course, ids used in the jsonString may be any valid UUID
     * and are used only to link entities together. They will be replaced by a random
     * UUID when deserialized and hence do not need to be checked if they exist in the
     * database previously.</p>
     *
     * @param jsonString containing entities to persist at once to the database.
     *         CourseID must be a valid UUID not currently in use.
     *         For other entities, replaces the given ids with randomly generated UUIDs.
     * @return newly created DataBundle
     */
    public static SqlDataBundle deserializeDataBundle(String jsonString) {
        SqlDataBundle dataBundle = JsonUtils.fromJson(jsonString, SqlDataBundle.class);

        Collection<Account> accounts = dataBundle.accounts.values();
        Collection<AccountRequest> accountRequests = dataBundle.accountRequests.values();
        Collection<Course> courses = dataBundle.courses.values();
        Collection<Section> sections = dataBundle.sections.values();
        Collection<Team> teams = dataBundle.teams.values();
        Collection<Instructor> instructors = dataBundle.instructors.values();
        Collection<Student> students = dataBundle.students.values();
        Collection<FeedbackSession> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackSessionLog> sessionLogs = dataBundle.feedbackSessionLogs.values();
        Collection<FeedbackQuestion> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponse> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseComment> responseComments = dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        // Mapping of IDs or placeholder IDs to actual entity
        Map<String, Course> coursesMap = new HashMap<>();
        Map<UUID, Section> sectionsMap = new HashMap<>();
        Map<UUID, Team> teamsMap = new HashMap<>();
        Map<UUID, FeedbackSession> sessionsMap = new HashMap<>();
        Map<UUID, FeedbackQuestion> questionMap = new HashMap<>();
        Map<UUID, FeedbackResponse> responseMap = new HashMap<>();
        Map<UUID, Account> accountsMap = new HashMap<>();
        Map<UUID, User> usersMap = new HashMap<>();
        Map<UUID, Notification> notificationsMap = new HashMap<>();

        // Replace any placeholder IDs with newly generated UUIDs
        // Store mapping of placeholder ID to actual entity to keep track of
        // associations between entities
        for (AccountRequest accountRequest : accountRequests) {
            accountRequest.setId(UUID.randomUUID());
            accountRequest.generateNewRegistrationKey();
        }

        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        for (Section section : sections) {
            UUID placeholderId = section.getId();
            section.setId(UUID.randomUUID());
            sectionsMap.put(placeholderId, section);
            Course course = coursesMap.get(section.getCourse().getId());
            section.setCourse(course);
        }

        for (Team team : teams) {
            UUID placeholderId = team.getId();
            team.setId(UUID.randomUUID());
            teamsMap.put(placeholderId, team);
            Section section = sectionsMap.get(team.getSection().getId());
            team.setSection(section);
        }

        for (FeedbackSession session : sessions) {
            UUID placeholderId = session.getId();
            session.setId(UUID.randomUUID());
            sessionsMap.put(placeholderId, session);
            Course course = coursesMap.get(session.getCourse().getId());
            session.setCourse(course);
        }

        for (FeedbackQuestion question : questions) {
            UUID placeholderId = question.getId();
            question.setId(UUID.randomUUID());
            questionMap.put(placeholderId, question);
            FeedbackSession fs = sessionsMap.get(question.getFeedbackSession().getId());
            question.setFeedbackSession(fs);
        }

        for (FeedbackResponse response : responses) {
            UUID placeholderId = response.getId();
            response.setId(UUID.randomUUID());
            responseMap.put(placeholderId, response);
            FeedbackQuestion fq = questionMap.get(response.getFeedbackQuestion().getId());
            Section giverSection = sectionsMap.get(response.getGiverSection().getId());
            Section recipientSection = response.getRecipientSection() != null
                    ? sectionsMap.get(response.getRecipientSection().getId()) : null;
            response.setFeedbackQuestion(fq);
            response.setGiverSection(giverSection);
            response.setRecipientSection(recipientSection);
        }

        for (FeedbackResponseComment responseComment : responseComments) {
            FeedbackResponse fr = responseMap.get(responseComment.getFeedbackResponse().getId());
            Section giverSection = sectionsMap.get(responseComment.getGiverSection().getId());
            Section recipientSection = sectionsMap.get(responseComment.getRecipientSection().getId());
            responseComment.setFeedbackResponse(fr);
            responseComment.setGiverSection(giverSection);
            responseComment.setRecipientSection(recipientSection);
        }

        for (Account account : accounts) {
            UUID placeholderId = account.getId();
            account.setId(UUID.randomUUID());
            accountsMap.put(placeholderId, account);
        }

        for (Instructor instructor : instructors) {
            UUID placeholderId = instructor.getId();
            instructor.setId(UUID.randomUUID());
            usersMap.put(placeholderId, instructor);
            Course course = coursesMap.get(instructor.getCourse().getId());
            instructor.setCourse(course);
            if (instructor.getAccount() != null) {
                Account account = accountsMap.get(instructor.getAccount().getId());
                instructor.setAccount(account);
            }
            instructor.generateNewRegistrationKey();
        }

        for (Student student : students) {
            UUID placeholderId = student.getId();
            student.setId(UUID.randomUUID());
            usersMap.put(placeholderId, student);
            Course course = coursesMap.get(student.getCourse().getId());
            student.setCourse(course);
            Team team = teamsMap.get(student.getTeam().getId());
            student.setTeam(team);
            if (student.getAccount() != null) {
                Account account = accountsMap.get(student.getAccount().getId());
                student.setAccount(account);
            }
            student.generateNewRegistrationKey();
        }

        for (FeedbackSessionLog log : sessionLogs) {
            log.setId(UUID.randomUUID());
            FeedbackSession fs = sessionsMap.get(log.getFeedbackSession().getId());
            log.setFeedbackSession(fs);
            Student student = (Student) usersMap.get(log.getStudent().getId());
            log.setStudent(student);
        }

        for (Notification notification : notifications) {
            UUID placeholderId = notification.getId();
            notification.setId(UUID.randomUUID());
            notificationsMap.put(placeholderId, notification);
        }

        for (ReadNotification readNotification : readNotifications) {
            readNotification.setId(UUID.randomUUID());
            Account account = accountsMap.get(readNotification.getAccount().getId());
            readNotification.setAccount(account);
            account.addReadNotification(readNotification);
            Notification notification = notificationsMap.get(readNotification.getNotification().getId());
            readNotification.setNotification(notification);
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            deadlineExtension.setId(UUID.randomUUID());
            FeedbackSession session = sessionsMap.get(deadlineExtension.getFeedbackSession().getId());
            deadlineExtension.setFeedbackSession(session);
            User user = usersMap.get(deadlineExtension.getUser().getId());
            deadlineExtension.setUser(user);
        }

        return dataBundle;
    }

    /**
     * Persists data in the given {@link DataBundle} to the database.
     *
     * @throws InvalidParametersException if invalid data is encountered.
     * @throws EntityDoesNotExistException if an entity was not found.
     *         (ReadNotification requires Account and Notification to be created)
     */
    public SqlDataBundle persistDataBundle(SqlDataBundle dataBundle)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        if (dataBundle == null) {
            throw new InvalidParametersException("Null data bundle");
        }

        linkEntities(dataBundle);

        Collection<Account> accounts = dataBundle.accounts.values();
        Collection<AccountRequest> accountRequests = dataBundle.accountRequests.values();
        Collection<Course> courses = dataBundle.courses.values();
        Collection<Section> sections = dataBundle.sections.values();
        Collection<Team> teams = dataBundle.teams.values();
        Collection<Instructor> instructors = dataBundle.instructors.values();
        Collection<Student> students = dataBundle.students.values();
        Collection<FeedbackSession> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackSessionLog> sessionLogs = dataBundle.feedbackSessionLogs.values();
        Collection<FeedbackQuestion> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponse> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseComment> responseComments = dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        for (AccountRequest accountRequest : accountRequests) {
            accountRequestsLogic.createAccountRequest(accountRequest);
        }

        for (Notification notification : notifications) {
            notificationsLogic.createNotification(notification);
        }

        for (Course course : courses) {
            coursesLogic.createCourse(course);
        }

        for (Section section : sections) {
            coursesLogic.createSection(section);
        }

        for (Team team : teams) {
            coursesLogic.createTeam(team);
        }

        for (FeedbackSession session : sessions) {
            fsLogic.createFeedbackSession(session);
        }

        for (FeedbackQuestion question : questions) {
            fqLogic.createFeedbackQuestion(question);
        }

        for (FeedbackResponse response : responses) {
            frLogic.createFeedbackResponse(response);
        }

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setId(null);
            frcLogic.createFeedbackResponseComment(responseComment);
        }

        for (Account account : accounts) {
            accountsLogic.createAccount(account);
        }

        for (Instructor instructor : instructors) {
            usersLogic.createInstructor(instructor);
        }

        for (Student student : students) {
            usersLogic.createStudent(student);
        }

        fslLogic.createFeedbackSessionLogs(new ArrayList<>(sessionLogs));

        for (ReadNotification readNotification : readNotifications) {
            accountsLogic.updateReadNotifications(readNotification.getAccount().getGoogleId(),
                    readNotification.getNotification().getId(), readNotification.getNotification().getEndTime());
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
        }

        return dataBundle;
    }

    /**
     * Removes the items in the data bundle from the database.
     */
    public void removeDataBundle(SqlDataBundle dataBundle) throws InvalidParametersException {
        if (dataBundle == null) {
            throw new InvalidParametersException("Data bundle is null");
        }

        linkEntities(dataBundle);
        dataBundle.courses.values().forEach(course -> {
            coursesLogic.deleteCourseCascade(course.getId());
        });
        dataBundle.notifications.values().forEach(notification -> {
            notificationsLogic.deleteNotification(notification.getId());
        });
        dataBundle.accounts.values().forEach(account -> {
            accountsLogic.deleteAccount(account.getGoogleId());
        });
        dataBundle.accountRequests.values().forEach(accountRequest -> {
            accountRequestsLogic.deleteAccountRequest(accountRequest.getId());
        });
    }

    /**
     * Creates document for entities that have document, i.e. searchable.
     */
    public void putDocuments(SqlDataBundle dataBundle) throws SearchServiceException {
        Map<String, Student> students = dataBundle.students;
        for (Student student : students.values()) {
            usersLogic.putStudentDocument(student);
        }

        Map<String, Instructor> instructors = dataBundle.instructors;
        for (Instructor instructor : instructors.values()) {
            usersLogic.putInstructorDocument(instructor);
        }

        Map<String, AccountRequest> accountRequests = dataBundle.accountRequests;
        for (AccountRequest accountRequest : accountRequests.values()) {
            accountRequestsLogic.putDocument(accountRequest);
        }
    }

    private static void linkEntities(SqlDataBundle dataBundle) {
        Collection<Account> accounts = dataBundle.accounts.values();
        Collection<Course> courses = dataBundle.courses.values();
        Collection<Section> sections = dataBundle.sections.values();
        Collection<Team> teams = dataBundle.teams.values();
        Collection<Instructor> instructors = dataBundle.instructors.values();
        Collection<Student> students = dataBundle.students.values();
        Collection<FeedbackSession> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackQuestion> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponse> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseComment> responseComments = dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        // Mapping of IDs or placeholder IDs to actual entity
        Map<String, Course> coursesMap = new HashMap<>();
        Map<UUID, Section> sectionsMap = new HashMap<>();
        Map<UUID, Team> teamsMap = new HashMap<>();
        Map<UUID, FeedbackSession> sessionsMap = new HashMap<>();
        Map<UUID, FeedbackQuestion> questionMap = new HashMap<>();
        Map<UUID, FeedbackResponse> responseMap = new HashMap<>();
        Map<UUID, Account> accountsMap = new HashMap<>();
        Map<UUID, User> usersMap = new HashMap<>();
        Map<UUID, Notification> notificationsMap = new HashMap<>();

        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        for (Section section : sections) {
            sectionsMap.put(section.getId(), section);
            Course course = coursesMap.get(section.getCourse().getId());
            section.setCourse(course);
        }

        for (Team team : teams) {
            teamsMap.put(team.getId(), team);
            Section section = sectionsMap.get(team.getSection().getId());
            team.setSection(section);
        }

        for (FeedbackSession session : sessions) {
            sessionsMap.put(session.getId(), session);
            Course course = coursesMap.get(session.getCourse().getId());
            session.setCourse(course);
        }

        for (FeedbackQuestion question : questions) {
            questionMap.put(question.getId(), question);
            FeedbackSession fs = sessionsMap.get(question.getFeedbackSession().getId());
            question.setFeedbackSession(fs);
        }

        for (FeedbackResponse response : responses) {
            UUID placeholderId = response.getId();
            responseMap.put(placeholderId, response);
            FeedbackQuestion fq = questionMap.get(response.getFeedbackQuestion().getId());
            Section giverSection = sectionsMap.get(response.getGiverSection().getId());
            Section recipientSection = response.getRecipientSection() != null
                    ? sectionsMap.get(response.getRecipientSection().getId()) : null;
            response.setFeedbackQuestion(fq);
            response.setGiverSection(giverSection);
            response.setRecipientSection(recipientSection);
        }

        for (FeedbackResponseComment responseComment : responseComments) {
            FeedbackResponse fr = responseMap.get(responseComment.getFeedbackResponse().getId());
            Section giverSection = sectionsMap.get(responseComment.getGiverSection().getId());
            Section recipientSection = sectionsMap.get(responseComment.getRecipientSection().getId());
            responseComment.setFeedbackResponse(fr);
            responseComment.setGiverSection(giverSection);
            responseComment.setRecipientSection(recipientSection);
        }

        for (Account account : accounts) {
            accountsMap.put(account.getId(), account);
        }

        for (Instructor instructor : instructors) {
            usersMap.put(instructor.getId(), instructor);
            Course course = coursesMap.get(instructor.getCourse().getId());
            instructor.setCourse(course);
            if (instructor.getAccount() != null) {
                Account account = accountsMap.get(instructor.getAccount().getId());
                instructor.setAccount(account);
            }
            instructor.generateNewRegistrationKey();
        }

        for (Student student : students) {
            usersMap.put(student.getId(), student);
            Course course = coursesMap.get(student.getCourse().getId());
            student.setCourse(course);
            Team team = teamsMap.get(student.getTeam().getId());
            student.setTeam(team);
            if (student.getAccount() != null) {
                Account account = accountsMap.get(student.getAccount().getId());
                student.setAccount(account);
            }
            student.generateNewRegistrationKey();
        }

        for (Notification notification : notifications) {
            notificationsMap.put(notification.getId(), notification);
        }

        for (ReadNotification readNotification : readNotifications) {
            Account account = accountsMap.get(readNotification.getAccount().getId());
            readNotification.setAccount(account);
            Notification notification = notificationsMap.get(readNotification.getNotification().getId());
            readNotification.setNotification(notification);
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            FeedbackSession session = sessionsMap.get(deadlineExtension.getFeedbackSession().getId());
            deadlineExtension.setFeedbackSession(session);
            User user = usersMap.get(deadlineExtension.getUser().getId());
            deadlineExtension.setUser(user);
        }
    }
}
