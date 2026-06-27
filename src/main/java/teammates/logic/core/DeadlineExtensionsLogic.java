package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.ExtensionUpdateType;
import teammates.common.datatransfer.UpdateExtensionsResult;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.LinksUtil;
import teammates.logic.email.DeadlineExtensionEmailsLogic;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.storage.api.DeadlineExtensionsDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Handles operations related to deadline extensions.
 *
 * @see DeadlineExtension
 * @see DeadlineExtensionsDb
 */
public final class DeadlineExtensionsLogic {

    private static final DeadlineExtensionsLogic instance = new DeadlineExtensionsLogic();

    private DeadlineExtensionsDb deadlineExtensionsDb;

    private FeedbackSessionsLogic feedbackSessionsLogic;
    private CoursesLogic coursesLogic;

    private UsersLogic usersLogic;
    private DeadlineExtensionEmailsLogic deadlineExtensionEmailsLogic;

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(DeadlineExtensionsDb deadlineExtensionsDb,
            FeedbackSessionsLogic feedbackSessionsLogic, CoursesLogic coursesLogic, UsersLogic usersLogic,
            DeadlineExtensionEmailsLogic deadlineExtensionsEmailsLogic) {
        this.deadlineExtensionsDb = deadlineExtensionsDb;
        this.feedbackSessionsLogic = feedbackSessionsLogic;
        this.coursesLogic = coursesLogic;
        this.usersLogic = usersLogic;
        this.deadlineExtensionEmailsLogic = deadlineExtensionsEmailsLogic;
    }

    /**
     * Gets the deadline extensions for a feedback session.
     */
    public Set<DeadlineExtension> getDeadlineExtensions(UUID feedbackSessionId) throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = feedbackSessionsLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityDoesNotExistException("Feedback session does not exist: " + feedbackSessionId);
        }

        return feedbackSession.getDeadlineExtensions();
    }

    /**
     * Gets a deadline extension by its id.
     */
    public DeadlineExtension getDeadlineExtension(UUID id) {
        return deadlineExtensionsDb.getDeadlineExtension(id);
    }

    /**
     * Gets the deadline extension for a specific user in a feedback session, or null if none exists.
     */
    public DeadlineExtension getDeadlineExtension(UUID feedbackSessionId, UUID userId) {
        return deadlineExtensionsDb.getDeadlineExtension(userId, feedbackSessionId);
    }

    /**
     * Get extended deadline end time for this session and user if it exists, otherwise get the deadline of the session.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        DeadlineExtension deadlineExtension =
                deadlineExtensionsDb.getDeadlineExtension(user.getId(), session.getId());

        if (deadlineExtension == null) {
            return session.getEndTime();
        }

        return deadlineExtension.getEndTime();
    }

    /**
     * Gets deadlines for the given users and their feedback sessions.
     */
    public Map<FeedbackSession, Instant> getDeadlinesForUsers(
            Map<User, List<FeedbackSession>> sessionsByUser) {
        Map<FeedbackSession, Instant> deadlines = new LinkedHashMap<>();
        if (sessionsByUser.isEmpty()) {
            return deadlines;
        }

        List<UUID> userIds = sessionsByUser.keySet().stream()
                .map(User::getId)
                .distinct()
                .toList();
        List<UUID> sessionIds = sessionsByUser.values().stream()
                .flatMap(List::stream)
                .map(FeedbackSession::getId)
                .distinct()
                .toList();

        Map<UserSession, Instant> extensionDeadlines = new LinkedHashMap<>();
        List<DeadlineExtension> extensions =
                deadlineExtensionsDb.getDeadlineExtensionsForUsersAndSessions(userIds, sessionIds);
        for (DeadlineExtension extension : extensions) {
            extensionDeadlines.put(
                    new UserSession(extension.getUserId(), extension.getSessionId()),
                    extension.getEndTime());
        }

        sessionsByUser.forEach((user, sessions) -> {
            sessions.forEach(session -> {
                UserSession userSession = new UserSession(user.getId(), session.getId());
                deadlines.put(session, extensionDeadlines.getOrDefault(userSession, session.getEndTime()));
            });
        });
        return deadlines;
    }

    /**
     * Get deadline entity for this session and user if it exists, otherwise return null.
     */
    public DeadlineExtension getDeadlineExtensionEntityForUser(FeedbackSession feedbackSession, User user) {
        return deadlineExtensionsDb.getDeadlineExtension(user.getId(), feedbackSession.getId());
    }

    /**
     * Creates a deadline extension.
     *
     * @return created deadline extension
     * @throws InvalidParametersException if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension already exist
     */
    public DeadlineExtension createDeadlineExtension(DeadlineExtension deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert deadlineExtension != null;

        validateDeadlineExtension(deadlineExtension);

        if (deadlineExtensionsDb.getDeadlineExtension(deadlineExtension.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS, deadlineExtension.toString()));
        }

        return deadlineExtensionsDb.persistDeadlineExtension(deadlineExtension);
    }

    /**
     * Updates the deadline extensions for a feedback session and enqueues
     * notification emails for the changed users.
     */
    public List<UpdateExtensionsResult> updateDeadlineExtensionsAndNotify(
            FeedbackSession feedbackSession, Map<UUID, Instant> extensions) throws InvalidParametersException {
        List<DeadlineExtensionUpdateEmailContext> emailContexts = new ArrayList<>();
        FeedbackSessionEmailContext feedbackSessionContext = buildFeedbackSessionEmailContext(feedbackSession);
        List<UpdateExtensionsResult> updateResults = updateDeadlineExtensions(feedbackSession, extensions, emailContexts);
        deadlineExtensionEmailsLogic.enqueueDeadlineExtensionUpdateEmails(feedbackSessionContext, emailContexts);
        return updateResults;
    }

    /**
     * Updates the deadline extensions for a feedback session based on the provided extensions map.
     *
     * <p>The method will create new deadline extensions, update existing ones,
     * and delete any deadline extensions that are not present in the provided map.
     */
    public List<UpdateExtensionsResult> updateDeadlineExtensions(
            FeedbackSession feedbackSession, Map<UUID, Instant> extensions) throws InvalidParametersException {
        return updateDeadlineExtensions(feedbackSession, extensions, null);
    }

    private List<UpdateExtensionsResult> updateDeadlineExtensions(
            FeedbackSession feedbackSession, Map<UUID, Instant> extensions,
            List<DeadlineExtensionUpdateEmailContext> emailContexts) throws InvalidParametersException {
        Instant sessionDeadline = feedbackSession.getEndTime();
        Map<UUID, User> userMap = usersLogic.getUsersForCourse(feedbackSession.getCourseId())
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<UUID, DeadlineExtension> existingDeadlineExtensions = feedbackSession.getDeadlineExtensions()
                .stream()
                .collect(Collectors.toMap(DeadlineExtension::getUserId, deadlineExtension -> deadlineExtension));

        List<UpdateExtensionsResult> results = new ArrayList<>();
        for (Map.Entry<UUID, Instant> entry : extensions.entrySet()) {
            UUID userId = entry.getKey();
            User user = userMap.get(userId);
            if (user == null) {
                throw new InvalidParametersException("User with ID " + userId
                        + " does not exist in course " + feedbackSession.getCourseId());
            }

            Instant newDeadline = entry.getValue();
            DeadlineExtension existingDeadlineExtension = existingDeadlineExtensions.get(userId);
            if (existingDeadlineExtension == null) {
                // Create new deadline extension
                DeadlineExtension newDeadlineExtension = new DeadlineExtension(user, newDeadline);
                feedbackSession.addDeadlineExtension(newDeadlineExtension);

                validateDeadlineExtension(newDeadlineExtension);
                deadlineExtensionsDb.persistDeadlineExtension(newDeadlineExtension);
                results.add(new UpdateExtensionsResult(userId, sessionDeadline, newDeadline, ExtensionUpdateType.CREATED));
                addEmailContext(emailContexts, feedbackSession, user, sessionDeadline, newDeadline,
                        EmailType.DEADLINE_EXTENSION_GRANTED);
            } else if (!existingDeadlineExtension.getEndTime().equals(newDeadline)) {
                Instant oldDeadline = existingDeadlineExtension.getEndTime();
                existingDeadlineExtension.setEndTime(newDeadline);

                validateDeadlineExtension(existingDeadlineExtension);
                results.add(new UpdateExtensionsResult(userId, oldDeadline, newDeadline, ExtensionUpdateType.UPDATED));
                addEmailContext(emailContexts, feedbackSession, user, oldDeadline, newDeadline,
                        EmailType.DEADLINE_EXTENSION_UPDATED);
            } else {
                results.add(new UpdateExtensionsResult(userId, newDeadline, newDeadline, ExtensionUpdateType.UNCHANGED));
            }
        }

        for (DeadlineExtension existingDeadlineExtension : existingDeadlineExtensions.values()) {
            if (!extensions.containsKey(existingDeadlineExtension.getUserId())) {
                User user = existingDeadlineExtension.getUser();
                Instant oldDeadline = existingDeadlineExtension.getEndTime();
                deleteDeadlineExtension(existingDeadlineExtension);
                feedbackSession.removeDeadlineExtension(existingDeadlineExtension);
                results.add(new UpdateExtensionsResult(user.getId(), oldDeadline, sessionDeadline,
                        ExtensionUpdateType.DELETED));
                addEmailContext(emailContexts, feedbackSession, user, oldDeadline, sessionDeadline,
                        EmailType.DEADLINE_EXTENSION_REVOKED);
            }
        }

        return results;
    }

    /**
     * Deletes a deadline extension.
     *
     * <p>Fails silently if the deadline extension does not exist</p>
     */
    public void deleteDeadlineExtension(DeadlineExtension de) {
        if (de == null) {
            return;
        }

        deadlineExtensionsDb.removeDeadlineExtension(de);
    }

    /**
     * Updates a deadline extension.
     *
     * @throws EntityDoesNotExistException if the deadline extension does not exist
     * @throws InvalidParametersException if the deadline extension is not valid
     */
    public DeadlineExtension updateDeadlineExtension(DeadlineExtension de)
            throws InvalidParametersException, EntityDoesNotExistException {
        DeadlineExtension existing = deadlineExtensionsDb.getDeadlineExtension(de.getId());
        if (existing == null) {
            throw new EntityDoesNotExistException("Trying to update non-existent Entity: " + de);
        }

        validateDeadlineExtension(de);
        return de;
    }

    /**
     * Gets a list of deadline extensions with endTime coming up soon
     * and possibly need a closing soon email to be sent.
     */
    public List<DeadlineExtension> getDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        return deadlineExtensionsDb.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
    }

    private void validateDeadlineExtension(DeadlineExtension deadlineExtension) throws InvalidParametersException {
        if (!deadlineExtension.isValid()) {
            throw new InvalidParametersException(deadlineExtension.getInvalidityInfo());
        }
    }

    private void addEmailContext(List<DeadlineExtensionUpdateEmailContext> emailContexts,
            FeedbackSession feedbackSession, User user, Instant oldEndTime, Instant newEndTime, EmailType emailType) {
        if (emailContexts == null) {
            return;
        }

        emailContexts.add(new DeadlineExtensionUpdateEmailContext(
                user.getEmail(),
                user.getName(),
                user instanceof Instructor,
                getSubmitUrl(feedbackSession, user),
                oldEndTime,
                newEndTime,
                emailType));
    }

    private FeedbackSessionEmailContext buildFeedbackSessionEmailContext(FeedbackSession feedbackSession) {
        Course course = coursesLogic.getCourse(feedbackSession.getCourseId());
        return new FeedbackSessionEmailContext(
                feedbackSession.getId(),
                course.getId(),
                course.getName(),
                course.getTimeZone(),
                feedbackSession.getName(),
                feedbackSession.getInstructionsString(),
                usersLogic.getCoOwnerContacts(course.getId()));
    }

    private String getSubmitUrl(FeedbackSession feedbackSession, User user) {
        return switch (user) {
        case Student student -> LinksUtil.getStudentSessionSubmitUrl(feedbackSession.getId(), student.getId(),
                student.getLinkVersion());
        case @SuppressWarnings("unused") Instructor instructor ->
                LinksUtil.getInstructorSessionSubmitUrl(feedbackSession.getId());
        default -> throw new AssertionError("User must be either an instructor or a student: " + user);
        };
    }

    private record UserSession(UUID userId, UUID sessionId) {
    }

}
