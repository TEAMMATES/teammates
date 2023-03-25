package teammates.sqllogic.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
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
    private FeedbackQuestionsLogic fqLogic;
    private NotificationsLogic notificationsLogic;
    private UsersLogic usersLogic;

    private DataBundleLogic() {
        // prevent initialization
    }

    public static DataBundleLogic inst() {
        return instance;
    }

    void initLogicDependencies(AccountsLogic accountsLogic, AccountRequestsLogic accountRequestsLogic,
            CoursesLogic coursesLogic,
            DeadlineExtensionsLogic deadlineExtensionsLogic, FeedbackSessionsLogic fsLogic,
            FeedbackQuestionsLogic fqLogic,
            NotificationsLogic notificationsLogic, UsersLogic usersLogic) {
        this.accountsLogic = accountsLogic;
        this.accountRequestsLogic = accountRequestsLogic;
        this.coursesLogic = coursesLogic;
        this.deadlineExtensionsLogic = deadlineExtensionsLogic;
        this.fsLogic = fsLogic;
        this.fqLogic = fqLogic;
        this.notificationsLogic = notificationsLogic;
        this.usersLogic = usersLogic;
    }

    /**
     * Deserialize JSON into a data bundle. Replaces placeholder IDs with actual
     * IDs.
     *
     * @param jsonString serialized data bundle
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
        Collection<FeedbackQuestion> questions = dataBundle.feedbackQuestions.values();
        // Collection<FeedbackResponse> responses =
        // dataBundle.feedbackResponses.values();
        // Collection<FeedbackResponseComment> responseComments =
        // dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        // Mapping of IDs or placeholder IDs to actual entity
        Map<String, Course> coursesMap = new HashMap<>();
        Map<UUID, Section> sectionsMap = new HashMap<>();
        Map<UUID, Team> teamsMap = new HashMap<>();
        Map<UUID, FeedbackSession> sessionsMap = new HashMap<>();
        // Map<UUID, FeedbackQuestion> questionMap = new HashMap<>();
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
            // UUID placeholderId = question.getId();
            question.setId(UUID.randomUUID());
            // questionMap.put(placeholderId, question);
            FeedbackSession fs = sessionsMap.get(question.getFeedbackSession().getId());
            question.setFeedbackSession(fs);
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
     */
    public SqlDataBundle persistDataBundle(SqlDataBundle dataBundle)
            throws InvalidParametersException, EntityAlreadyExistsException {
        if (dataBundle == null) {
            throw new InvalidParametersException("Null data bundle");
        }

        Collection<Account> accounts = dataBundle.accounts.values();
        Collection<AccountRequest> accountRequests = dataBundle.accountRequests.values();
        Collection<Course> courses = dataBundle.courses.values();
        Collection<Section> sections = dataBundle.sections.values();
        Collection<Team> teams = dataBundle.teams.values();
        Collection<Instructor> instructors = dataBundle.instructors.values();
        Collection<Student> students = dataBundle.students.values();
        Collection<FeedbackSession> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackQuestion> questions = dataBundle.feedbackQuestions.values();
        // Collection<FeedbackResponse> responses =
        // dataBundle.feedbackResponses.values();
        // Collection<FeedbackResponseComment> responseComments =
        // dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();

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

        for (Account account : accounts) {
            accountsLogic.createAccount(account);
        }

        for (Instructor instructor : instructors) {
            usersLogic.createInstructor(instructor);
        }

        for (Student student : students) {
            usersLogic.createStudent(student);
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
        }

        return dataBundle;
    }

    // TODO: Incomplete
    // private void removeDataBundle(SqlDataBundle dataBundle) throws
    // InvalidParametersException {
    // // Cannot rely on generated IDs, might not be the same as the actual ID in
    // the db.
    // if (dataBundle == null) {
    // throw new InvalidParametersException("Null data bundle");
    // }
    // }

}
