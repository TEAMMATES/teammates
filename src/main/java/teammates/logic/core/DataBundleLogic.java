package teammates.logic.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;

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
    private NotificationsLogic notificationsLogic;

    private DataBundleLogic() {
        // prevent initialization
    }

    public static DataBundleLogic inst() {
        return instance;
    }

    void initLogicDependencies(AccountsLogic accountsLogic, AccountRequestsLogic accountRequestsLogic,
            CoursesLogic coursesLogic, NotificationsLogic notificationsLogic) {
        this.accountsLogic = accountsLogic;
        this.accountRequestsLogic = accountRequestsLogic;
        this.coursesLogic = coursesLogic;
        this.notificationsLogic = notificationsLogic;
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
    public static DataBundle deserializeDataBundle(String jsonString) {
        DataBundle dataBundle = JsonUtils.fromJson(jsonString, DataBundle.class);

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
            Course course = coursesMap.get(section.getCourseId());
            section.setCourse(course);
            course.addSection(section);
        }

        for (Team team : teams) {
            UUID placeholderId = team.getId();
            team.setId(UUID.randomUUID());
            teamsMap.put(placeholderId, team);
            Section section = sectionsMap.get(team.getSectionId());
            team.setSection(section);
            section.addTeam(team);
        }

        for (FeedbackSession session : sessions) {
            UUID placeholderId = session.getId();
            session.setId(UUID.randomUUID());
            sessionsMap.put(placeholderId, session);
            Course course = coursesMap.get(session.getCourseId());
            session.setCourse(course);
            course.addFeedbackSession(session);
        }

        for (FeedbackQuestion question : questions) {
            UUID placeholderId = question.getId();
            question.setId(UUID.randomUUID());
            questionMap.put(placeholderId, question);
            FeedbackSession fs = sessionsMap.get(question.getSessionId());
            question.setFeedbackSession(fs);
            fs.addFeedbackQuestion(question);
        }

        for (FeedbackResponse response : responses) {
            UUID placeholderId = response.getId();
            response.setId(UUID.randomUUID());
            responseMap.put(placeholderId, response);
            FeedbackQuestion fq = questionMap.get(response.getQuestionId());
            Section giverSection = sectionsMap.get(response.getGiverSectionId());
            Section recipientSection = response.getRecipientSectionId() != null
                    ? sectionsMap.get(response.getRecipientSectionId()) : null;
            response.setFeedbackQuestion(fq);
            response.setGiverSection(giverSection);
            response.setRecipientSection(recipientSection);
            fq.addFeedbackResponse(response);
        }

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setId(UUID.randomUUID());
            FeedbackResponse fr = responseMap.get(responseComment.getResponseId());
            Section giverSection = sectionsMap.get(responseComment.getGiverSectionId());
            Section recipientSection = sectionsMap.get(responseComment.getRecipientSectionId());
            responseComment.setFeedbackResponse(fr);
            responseComment.setGiverSection(giverSection);
            responseComment.setRecipientSection(recipientSection);
            fr.addFeedbackResponseComment(responseComment);
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
            Course course = coursesMap.get(instructor.getCourseId());
            instructor.setCourse(course);
            if (instructor.getAccountId() != null) {
                Account account = accountsMap.get(instructor.getAccountId());
                instructor.setAccount(account);
            }
            instructor.generateNewRegistrationKey();
        }

        for (Student student : students) {
            UUID placeholderId = student.getId();
            student.setId(UUID.randomUUID());
            usersMap.put(placeholderId, student);
            Course course = coursesMap.get(student.getCourseId());
            student.setCourse(course);
            Team team = teamsMap.get(student.getTeamId());
            student.setTeam(team);
            if (student.getAccountId() != null) {
                Account account = accountsMap.get(student.getAccountId());
                student.setAccount(account);
            }
            student.generateNewRegistrationKey();
        }

        for (FeedbackSessionLog log : sessionLogs) {
            log.setId(UUID.randomUUID());
            FeedbackSession fs = log.getSessionId() == null
                    ? null : sessionsMap.get(log.getSessionId());
            log.setFeedbackSession(fs);
            Student student = log.getStudentId() == null
                    ? null : (Student) usersMap.get(log.getStudentId());
            log.setStudent(student);
        }

        for (Notification notification : notifications) {
            UUID placeholderId = notification.getId();
            notification.setId(UUID.randomUUID());
            notificationsMap.put(placeholderId, notification);
        }

        for (ReadNotification readNotification : readNotifications) {
            readNotification.setId(UUID.randomUUID());
            Account account = accountsMap.get(readNotification.getAccountId());
            readNotification.setAccount(account);
            account.addReadNotification(readNotification);
            Notification notification = notificationsMap.get(readNotification.getNotificationId());
            readNotification.setNotification(notification);
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            deadlineExtension.setId(UUID.randomUUID());
            FeedbackSession session = sessionsMap.get(deadlineExtension.getSessionId());
            deadlineExtension.setFeedbackSession(session);
            session.addDeadlineExtension(deadlineExtension);
            User user = usersMap.get(deadlineExtension.getUserId());
            deadlineExtension.setUser(user);
        }

        return dataBundle;
    }

    /**
     * Persists data in the given {@link DataBundle} to the database.
     *
     * @throws InvalidParametersException if invalid data is encountered.
     */
    public DataBundle persistDataBundle(DataBundle dataBundle)
            throws InvalidParametersException {
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
        Collection<FeedbackSessionLog> sessionLogs = dataBundle.feedbackSessionLogs.values();
        Collection<FeedbackQuestion> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponse> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseComment> responseComments = dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        persistEntities(accountRequests);
        persistEntities(notifications);
        persistEntities(accounts);
        persistEntities(courses);
        persistEntities(sections);
        persistEntities(teams);
        persistEntities(sessions);
        persistEntities(questions);
        persistEntities(responses);
        persistEntities(responseComments);
        persistEntities(instructors);
        persistEntities(students);
        persistEntities(sessionLogs);
        persistEntities(deadlineExtensions);
        persistEntities(readNotifications);

        return dataBundle;
    }

    /**
     * Removes the items in the data bundle from the database.
     */
    public void removeDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        if (dataBundle == null) {
            throw new InvalidParametersException("Data bundle is null");
        }

        dataBundle.courses.values().forEach(course ->
                coursesLogic.deleteCourseCascade(course.getId())
        );
        dataBundle.readNotifications.values().forEach(readNotification ->
                notificationsLogic.deleteReadNotification(readNotification.getId())
        );
        dataBundle.notifications.values().forEach(notification ->
                notificationsLogic.deleteNotification(notification.getId())
        );
        dataBundle.accounts.values().forEach(account ->
                accountsLogic.deleteAccount(account.getGoogleId())
        );
        dataBundle.accountRequests.values().forEach(accountRequest ->
                accountRequestsLogic.deleteAccountRequest(accountRequest.getId())
        );
    }

    private void persistEntities(Collection<? extends BaseEntity> entities) throws InvalidParametersException {
        for (BaseEntity entity : entities) {
            if (!entity.isValid()) {
                throw new InvalidParametersException(entity.getInvalidityInfo());
            }
            HibernateUtil.persist(entity);
        }
        HibernateUtil.flushSession();
    }
}
