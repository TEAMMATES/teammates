package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivilegesBundle;
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
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.InstructorCoursePrivilege;
import teammates.storage.entity.InstructorSectionPrivilege;
import teammates.storage.entity.InstructorSessionPrivilege;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
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
        Collection<ResponseInstructorComment> responseComments = dataBundle.responseInstructorComments.values();
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
            course.addSection(section);
        }

        for (Team team : teams) {
            UUID placeholderId = team.getId();
            team.setId(UUID.randomUUID());
            teamsMap.put(placeholderId, team);
            Section section = sectionsMap.get(team.getSectionId());
            section.addTeam(team);
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

        for (FeedbackSession session : sessions) {
            UUID placeholderId = session.getId();
            session.setId(UUID.randomUUID());
            sessionsMap.put(placeholderId, session);
            Course course = coursesMap.get(session.getCourseId());
            session.setCourse(course);
            User creator = usersMap.get(session.getCreatorId());
            if (creator instanceof Instructor instructor) {
                session.setSessionCreator(instructor);
            }
            course.addFeedbackSession(session);
        }

        for (FeedbackQuestion question : questions) {
            UUID placeholderId = question.getId();
            question.setId(UUID.randomUUID());
            questionMap.put(placeholderId, question);
            FeedbackSession fs = sessionsMap.get(question.getSessionId());
            fs.addFeedbackQuestion(question);
        }

        for (FeedbackResponse response : responses) {
            UUID placeholderId = response.getId();
            response.setId(UUID.randomUUID());
            responseMap.put(placeholderId, response);
            FeedbackQuestion fq = questionMap.get(response.getQuestionId());
            fq.addFeedbackResponse(response);
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

        for (FeedbackResponse response : responses) {
            ResponseGiver giver = response.getGiver();
            if (giver != null) {
                if (giver.getGiverTeamId() != null) {
                    Team team = teamsMap.get(giver.getGiverTeamId());
                    response.setGiver(team == null ? giver : new ResponseGiver(team));
                } else if (giver.getGiverUserId() != null) {
                    User user = usersMap.get(giver.getGiverUserId());
                    response.setGiver(user == null ? giver : new ResponseGiver(user));
                }
            }

            ResponseRecipient recipient = response.getRecipient();
            if (recipient != null) {
                if (recipient.isRecipientTeam()) {
                    Team team = teamsMap.get(recipient.getRecipientTeamId());
                    response.setRecipient(team == null ? recipient : new ResponseRecipient(team));
                } else if (recipient.isRecipientUser()) {
                    User user = usersMap.get(recipient.getRecipientUserId());
                    response.setRecipient(user == null ? recipient : new ResponseRecipient(user));
                } else if (recipient.isNoSpecificRecipient()) {
                    response.setRecipient(new ResponseRecipient());
                }
            }
        }

        for (FeedbackSessionLog log : sessionLogs) {
            log.setId(UUID.randomUUID());
            FeedbackSession fs = sessionsMap.get(log.getSessionId());
            log.setFeedbackSession(fs);
            User user = usersMap.get(log.getUserId());
            log.setUser(user);
        }

        for (Notification notification : notifications) {
            UUID placeholderId = notification.getId();
            notification.setId(UUID.randomUUID());
            notificationsMap.put(placeholderId, notification);
        }

        for (ReadNotification readNotification : readNotifications) {
            readNotification.setId(UUID.randomUUID());
            Account account = accountsMap.get(readNotification.getAccountId());
            account.addReadNotification(readNotification);
            Notification notification = notificationsMap.get(readNotification.getNotificationId());
            notification.addReadNotification(readNotification);
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            deadlineExtension.setId(UUID.randomUUID());
            FeedbackSession session = sessionsMap.get(deadlineExtension.getSessionId());
            session.addDeadlineExtension(deadlineExtension);
            User user = usersMap.get(deadlineExtension.getUserId());
            deadlineExtension.setUser(user);
        }

        for (ResponseInstructorComment responseComment : responseComments) {
            responseComment.setId(UUID.randomUUID());
            FeedbackResponse fr = responseMap.get(responseComment.getResponseId());
            fr.addResponseInstructorComment(responseComment);

            if (responseComment.getGiverId() != null) {
                User userGiver = usersMap.get(responseComment.getGiverId());
                if (!(userGiver instanceof Instructor)) {
                    throw new IllegalArgumentException("ResponseInstructorComment giver must be an instructor");
                }
                responseComment.setGiver((Instructor) userGiver);
            }

            if (responseComment.getLastEditedById() != null) {
                User userLastEditedBy = usersMap.get(responseComment.getLastEditedById());
                if (!(userLastEditedBy instanceof Instructor)) {
                    throw new IllegalArgumentException("ResponseInstructorComment last editor must be an instructor");
                }
                responseComment.setLastEditedBy((Instructor) userLastEditedBy);
            }
        }

        // Re-point the placeholder ids in the combined instructor privileges section to the
        // newly generated entity ids so they can be resolved against the bundle at persist time.
        for (InstructorPrivilegesBundle privileges : dataBundle.instructorPrivileges.values()) {
            User instructor = usersMap.get(privileges.getInstructorId());
            assert instructor != null
                    : "InstructorPrivilegesBundle contains instructorId that does not match any instructor in the bundle";
            privileges.setInstructorId(instructor.getId());

            Map<UUID, InstructorPermissionSet> remappedSectionLevel = new HashMap<>();
            privileges.getSectionLevel().forEach((sectionId, permissions) -> {
                Section section = sectionsMap.get(sectionId);
                if (section != null) {
                    remappedSectionLevel.put(section.getId(), permissions);
                }
            });
            privileges.setSectionLevel(remappedSectionLevel);

            Map<UUID, Map<UUID, InstructorPermissionSet>> remappedSessionLevel = new HashMap<>();
            privileges.getSessionLevel().forEach((sectionId, sessionPermissions) -> {
                Section section = sectionsMap.get(sectionId);
                if (section == null) {
                    return;
                }
                Map<UUID, InstructorPermissionSet> remappedSessions = new HashMap<>();
                sessionPermissions.forEach((sessionId, permissions) -> {
                    FeedbackSession session = sessionsMap.get(sessionId);
                    if (session != null) {
                        remappedSessions.put(session.getId(), permissions);
                    }
                });
                remappedSessionLevel.put(section.getId(), remappedSessions);
            });
            privileges.setSessionLevel(remappedSessionLevel);
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
        Collection<ResponseInstructorComment> responseComments = dataBundle.responseInstructorComments.values();
        Collection<DeadlineExtension> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        persistEntities(accountRequests);
        persistEntities(notifications);
        persistEntities(accounts);
        persistEntities(courses);
        persistEntities(sections);
        persistEntities(teams);
        persistEntities(instructors);
        persistEntities(students);
        persistEntities(sessions);
        persistEntities(expandInstructorPrivileges(dataBundle));
        persistEntities(questions);
        persistEntities(responses);
        persistEntities(responseComments);
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
                coursesLogic.deleteCourse(course.getId())
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

    /**
     * Expands the combined instructor privileges section of the bundle into the corresponding
     * course/section/session privilege entities, resolving references against the bundle's entities.
     */
    private List<BaseEntity> expandInstructorPrivileges(DataBundle dataBundle) {
        Map<UUID, Instructor> instructorsById = new HashMap<>();
        dataBundle.instructors.values().forEach(i -> instructorsById.put(i.getId(), i));
        Map<UUID, Section> sectionsById = new HashMap<>();
        dataBundle.sections.values().forEach(s -> sectionsById.put(s.getId(), s));
        Map<UUID, FeedbackSession> sessionsById = new HashMap<>();
        dataBundle.feedbackSessions.values().forEach(fs -> sessionsById.put(fs.getId(), fs));

        List<BaseEntity> entities = new ArrayList<>();
        for (InstructorPrivilegesBundle privileges : dataBundle.instructorPrivileges.values()) {
            Instructor instructor = instructorsById.get(privileges.getInstructorId());
            if (instructor == null) {
                continue;
            }
            entities.add(new InstructorCoursePrivilege(instructor, privileges.getCourseLevel()));

            for (Entry<UUID, InstructorPermissionSet> entry : privileges.getSectionLevel().entrySet()) {
                Section section = sectionsById.get(entry.getKey());
                if (section != null) {
                    entities.add(new InstructorSectionPrivilege(instructor, section, entry.getValue()));
                }
            }

            for (Entry<UUID, Map<UUID, InstructorPermissionSet>> sectionEntry
                    : privileges.getSessionLevel().entrySet()) {
                Section section = sectionsById.get(sectionEntry.getKey());
                if (section == null) {
                    continue;
                }
                for (Entry<UUID, InstructorPermissionSet> sessionEntry : sectionEntry.getValue().entrySet()) {
                    FeedbackSession session = sessionsById.get(sessionEntry.getKey());
                    if (session != null) {
                        entities.add(new InstructorSessionPrivilege(
                                instructor, section, session, sessionEntry.getValue()));
                    }
                }
            }
        }
        return entities;
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
