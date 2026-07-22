package teammates.logic.core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.Provider;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Institute;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.InstructorCoursePrivilege;
import teammates.storage.entity.InstructorSectionPrivilege;
import teammates.storage.entity.InstructorSessionPrivilege;
import teammates.storage.entity.MagicLink;
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
    private NotificationsLogic notificationsLogic;
    private InstitutesLogic institutesLogic;

    private DataBundleLogic() {
        // prevent initialization
    }

    public static DataBundleLogic inst() {
        return instance;
    }

    void initLogicDependencies(AccountsLogic accountsLogic,
            NotificationsLogic notificationsLogic, InstitutesLogic institutesLogic) {
        this.accountsLogic = accountsLogic;
        this.notificationsLogic = notificationsLogic;
        this.institutesLogic = institutesLogic;
    }

    /**
     * Deserialize JSON into a data bundle, replacing entity IDs with random UUIDs.
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
        return deserializeDataBundle(jsonString, null);
    }

    /**
     * Deserialize JSON into a data bundle with deterministic IDs derived from the seed.
     *
     * <p>When {@code seed} is provided, entity IDs are generated deterministically
     * using {@code UUID.nameUUIDFromBytes(seed + ":" + placeholderId)}. This ensures
     * IDs are stable across test runs and do not collide between test cases.</p>
     *
     * @param jsonString containing entities to persist at once to the database.
     * @param seed unique name that must not be shared with other tests.
     * @return newly created DataBundle
     */
    public static DataBundle deserializeDataBundle(String jsonString, String seed) {
        DataBundle dataBundle = JsonUtils.fromJson(jsonString, DataBundle.class);

        Collection<Institute> institutes = dataBundle.institutes.values();
        Collection<Account> accounts = dataBundle.accounts.values();
        Collection<AccountVerificationRequest> accountVerificationRequests = dataBundle.accountVerificationRequests.values();
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
        Collection<MagicLink> magicLinks = dataBundle.magicLinks.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        // Mapping of IDs or placeholder IDs to actual entity
        Map<UUID, Institute> institutesMap = new HashMap<>();
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
        for (Institute institute : institutes) {
            UUID placeholderId = institute.getId();
            institute.setId(generateId(placeholderId, seed));
            institutesMap.put(placeholderId, institute);
        }

        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
            course.setInstitute(institutesMap.get(course.getInstituteId()));
        }

        for (Section section : sections) {
            UUID placeholderId = section.getId();
            section.setId(generateId(placeholderId, seed));
            sectionsMap.put(placeholderId, section);
            Course course = coursesMap.get(section.getCourseId());
            course.addSection(section);
        }

        for (Team team : teams) {
            UUID placeholderId = team.getId();
            team.setId(generateId(placeholderId, seed));
            teamsMap.put(placeholderId, team);
            Section section = sectionsMap.get(team.getSectionId());
            section.addTeam(team);
        }

        for (Account account : accounts) {
            UUID placeholderId = account.getId();
            account.setId(generateId(placeholderId, seed));
            if (account.getProvider() == Provider.TEAMMATES_DEV) {
                account.setTenantId(Account.NO_TENANT);
            }
            accountsMap.put(placeholderId, account);
        }

        for (AccountVerificationRequest accountVerificationRequest : accountVerificationRequests) {
            UUID placeholderId = accountVerificationRequest.getId();
            accountVerificationRequest.setId(generateId(placeholderId, seed));
            accountVerificationRequest.setInstitute(institutesMap.get(accountVerificationRequest.getInstituteId()));
            if (accountVerificationRequest.getAccountId() != null) {
                Account account = accountsMap.get(accountVerificationRequest.getAccountId());
                accountVerificationRequest.setAccount(account);
            }
        }

        for (Instructor instructor : instructors) {
            UUID placeholderId = instructor.getId();
            instructor.setId(generateId(placeholderId, seed));
            usersMap.put(placeholderId, instructor);
            Course course = coursesMap.get(instructor.getCourseId());
            instructor.setCourse(course);
            if (instructor.getAccountId() != null) {
                Account account = accountsMap.get(instructor.getAccountId());
                instructor.setAccount(account);
            }
        }

        for (FeedbackSession session : sessions) {
            UUID placeholderId = session.getId();
            session.setId(generateId(placeholderId, seed));
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
            question.setId(generateId(placeholderId, seed));
            questionMap.put(placeholderId, question);
            FeedbackSession fs = sessionsMap.get(question.getSessionId());
            fs.addFeedbackQuestion(question);
        }

        for (FeedbackResponse response : responses) {
            UUID placeholderId = response.getId();
            response.setId(generateId(placeholderId, seed));
            responseMap.put(placeholderId, response);
            FeedbackQuestion fq = questionMap.get(response.getQuestionId());
            fq.addFeedbackResponse(response);
        }

        for (Student student : students) {
            UUID placeholderId = student.getId();
            student.setId(generateId(placeholderId, seed));
            usersMap.put(placeholderId, student);
            Course course = coursesMap.get(student.getCourseId());
            student.setCourse(course);
            Team team = teamsMap.get(student.getTeamId());
            student.setTeam(team);
            if (student.getAccountId() != null) {
                Account account = accountsMap.get(student.getAccountId());
                student.setAccount(account);
            }
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
            UUID placeholderId = log.getId();
            log.setId(generateId(placeholderId, seed));
            FeedbackSession fs = sessionsMap.get(log.getSessionId());
            log.setFeedbackSession(fs);
            User user = usersMap.get(log.getUserId());
            log.setUser(user);
        }

        for (Notification notification : notifications) {
            UUID placeholderId = notification.getId();
            notification.setId(generateId(placeholderId, seed));
            notificationsMap.put(placeholderId, notification);
        }

        for (MagicLink magicLink : magicLinks) {
            UUID placeholderId = magicLink.getId();
            magicLink.setId(generateId(placeholderId, seed));
        }

        for (ReadNotification readNotification : readNotifications) {
            UUID placeholderId = readNotification.getId();
            readNotification.setId(generateId(placeholderId, seed));
            Account account = accountsMap.get(readNotification.getAccountId());
            account.addReadNotification(readNotification);
            Notification notification = notificationsMap.get(readNotification.getNotificationId());
            notification.addReadNotification(readNotification);
        }

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            UUID placeholderId = deadlineExtension.getId();
            deadlineExtension.setId(generateId(placeholderId, seed));
            FeedbackSession session = sessionsMap.get(deadlineExtension.getSessionId());
            session.addDeadlineExtension(deadlineExtension);
            User user = usersMap.get(deadlineExtension.getUserId());
            deadlineExtension.setUser(user);
        }

        for (ResponseInstructorComment responseComment : responseComments) {
            UUID placeholderId = responseComment.getId();
            responseComment.setId(generateId(placeholderId, seed));
            FeedbackResponse fr = responseMap.get(responseComment.getResponseId());
            fr.addResponseInstructorComment(responseComment);

            if (responseComment.getGiverId() != null) {
                User userGiver = usersMap.get(responseComment.getGiverId());
                if (!(userGiver instanceof Instructor)) {
                    throw new IllegalArgumentException("ResponseInstructorComment giver must be an instructor");
                }
                responseComment.setGiver((Instructor) userGiver);
            }
        }

        // Re-point the placeholder ids in the combined instructor privileges section to the
        // newly generated entity ids so they can be resolved against the bundle at persist time.
        Map<String, InstructorPrivileges> remappedPrivileges = new HashMap<>();
        for (Entry<String, InstructorPrivileges> entry : dataBundle.instructorPrivileges.entrySet()) {
            InstructorPrivileges oldPrivileges = entry.getValue();
            User instructor = usersMap.get(oldPrivileges.getInstructorId());
            assert instructor != null
                    : "InstructorPrivileges contains instructorId that does not match any instructor in the bundle";

            // Create new InstructorPrivileges object with the actual instructor ID
            InstructorPrivileges newPrivileges = new InstructorPrivileges(instructor.getId());

            // Copy course level privileges
            InstructorPermissionSet courseLevel = oldPrivileges.getCourseLevelPrivileges();
            newPrivileges.updatePrivilege(
                    Const.InstructorPermissions.CAN_MODIFY_COURSE,
                    courseLevel.isCanModifyCourse());
            newPrivileges.updatePrivilege(
                    Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR,
                    courseLevel.isCanModifyInstructor());
            newPrivileges.updatePrivilege(
                    Const.InstructorPermissions.CAN_MODIFY_SESSION,
                    courseLevel.isCanModifySession());
            newPrivileges.updatePrivilege(
                    Const.InstructorPermissions.CAN_MODIFY_STUDENT,
                    courseLevel.isCanModifyStudent());
            newPrivileges.updatePrivilege(
                    Const.InstructorPermissions.CAN_VIEW_SESSION,
                    courseLevel.isCanViewSession());
            newPrivileges.updatePrivilege(
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION,
                    courseLevel.isCanSubmitSession());

            // Copy and remap section level privileges
            oldPrivileges.getSectionLevelPrivileges().forEach((sectionId, permissions) -> {
                Section section = sectionsMap.get(sectionId);
                if (section != null) {
                    newPrivileges.updatePrivilege(
                            section.getId(),
                            Const.InstructorPermissions.CAN_VIEW_SESSION,
                            permissions.isCanViewSession());
                    newPrivileges.updatePrivilege(
                            section.getId(),
                            Const.InstructorPermissions.CAN_SUBMIT_SESSION,
                            permissions.isCanSubmitSession());
                }
            });

            // Copy and remap session level privileges
            oldPrivileges.getSessionLevelPrivileges().forEach((sectionId, sessionPermissions) -> {
                Section section = sectionsMap.get(sectionId);
                if (section == null) {
                    return;
                }
                sessionPermissions.forEach((sessionId, permissions) -> {
                    FeedbackSession session = sessionsMap.get(sessionId);
                    if (session != null) {
                        newPrivileges.updatePrivilege(
                                section.getId(),
                                session.getId(),
                                Const.InstructorPermissions.CAN_VIEW_SESSION,
                                permissions.isCanViewSession());
                        newPrivileges.updatePrivilege(
                                section.getId(),
                                session.getId(),
                                Const.InstructorPermissions.CAN_SUBMIT_SESSION,
                                permissions.isCanSubmitSession());
                    }
                });
            });

            remappedPrivileges.put(entry.getKey(), newPrivileges);
        }
        dataBundle.instructorPrivileges = remappedPrivileges;

        return dataBundle;
    }

    private static UUID generateId(UUID placeholderId, String seed) {
        assert placeholderId != null : "placeholderId must be defined";
        if (seed == null) {
            return UUID.randomUUID();
        }
        return UUID.nameUUIDFromBytes((seed + ":" + placeholderId).getBytes(StandardCharsets.UTF_8));
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

        Collection<Institute> institutes = dataBundle.institutes.values();
        Collection<Account> accounts = dataBundle.accounts.values();
        Collection<AccountVerificationRequest> accountVerificationRequests = dataBundle.accountVerificationRequests.values();
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
        Collection<MagicLink> magicLinks = dataBundle.magicLinks.values();
        Collection<Notification> notifications = dataBundle.notifications.values();
        Collection<ReadNotification> readNotifications = dataBundle.readNotifications.values();

        persistEntities(institutes);
        persistEntities(magicLinks);
        persistEntities(notifications);
        persistEntities(accounts);
        persistEntities(accountVerificationRequests);
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

        dataBundle.institutes.values().forEach(institute ->
                institutesLogic.deleteInstitute(institute.getId())
        );
        dataBundle.notifications.values().forEach(notification ->
                notificationsLogic.deleteNotification(notification.getId())
        );
        dataBundle.accounts.values().forEach(account ->
                accountsLogic.deleteAccount(account.getId())
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
        for (InstructorPrivileges privileges : dataBundle.instructorPrivileges.values()) {
            Instructor instructor = instructorsById.get(privileges.getInstructorId());
            if (instructor == null) {
                continue;
            }
            entities.add(new InstructorCoursePrivilege(instructor, privileges.getCourseLevelPrivileges()));

            for (Entry<UUID, InstructorPermissionSet> entry : privileges.getSectionLevelPrivileges().entrySet()) {
                Section section = sectionsById.get(entry.getKey());
                if (section != null) {
                    entities.add(new InstructorSectionPrivilege(instructor, section, entry.getValue()));
                }
            }

            for (Entry<UUID, Map<UUID, InstructorPermissionSet>> sectionEntry
                    : privileges.getSessionLevelPrivileges().entrySet()) {
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
