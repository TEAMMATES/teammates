package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
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
import teammates.storage.api.DeadlineExtensionsDb;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
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

    private UsersLogic usersLogic;

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(DeadlineExtensionsDb deadlineExtensionsDb,
            FeedbackSessionsLogic feedbackSessionsLogic, UsersLogic usersLogic) {
        this.deadlineExtensionsDb = deadlineExtensionsDb;
        this.feedbackSessionsLogic = feedbackSessionsLogic;
        this.usersLogic = usersLogic;
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

        return deadlineExtensionsDb.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Updates the deadline extensions for a feedback session based on the provided extensions map.
     *
     * <p>The method will create new deadline extensions, update existing ones,
     * and delete any deadline extensions that are not present in the provided map.
     */
    public List<UpdateExtensionsResult> updateDeadlineExtensions(
            FeedbackSession feedbackSession, Map<UUID, Instant> extensions) throws InvalidParametersException {
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
                deadlineExtensionsDb.createDeadlineExtension(newDeadlineExtension);
                results.add(new UpdateExtensionsResult(user, sessionDeadline, newDeadline, ExtensionUpdateType.CREATED));
            } else if (!existingDeadlineExtension.getEndTime().equals(newDeadline)) {
                Instant oldDeadline = existingDeadlineExtension.getEndTime();
                existingDeadlineExtension.setEndTime(newDeadline);

                validateDeadlineExtension(existingDeadlineExtension);
                results.add(new UpdateExtensionsResult(user, oldDeadline, newDeadline, ExtensionUpdateType.UPDATED));
            }
        }

        for (DeadlineExtension existingDeadlineExtension : existingDeadlineExtensions.values()) {
            if (!extensions.containsKey(existingDeadlineExtension.getUserId())) {
                User user = existingDeadlineExtension.getUser();
                deleteDeadlineExtension(existingDeadlineExtension);
                feedbackSession.removeDeadlineExtension(existingDeadlineExtension);
                results.add(new UpdateExtensionsResult(user, existingDeadlineExtension.getEndTime(),
                        sessionDeadline, ExtensionUpdateType.DELETED));
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

        deadlineExtensionsDb.deleteDeadlineExtension(de);
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

    /**
     * Deletes a user's deadline extensions.
     */
    public void deleteDeadlineExtensionsForUser(User user) {
        String courseId = user.getCourseId();
        List<FeedbackSession> feedbackSessions = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);

        feedbackSessions.forEach(feedbackSession -> {
            Set<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();

            deadlineExtensions = deadlineExtensions
                    .stream()
                    .filter(deadlineExtension -> deadlineExtension.getUser().equals(user))
                    .collect(Collectors.toSet());

            for (DeadlineExtension deadlineExtension : deadlineExtensions) {
                deleteDeadlineExtension(deadlineExtension);
            }
        });
    }

    private void validateDeadlineExtension(DeadlineExtension deadlineExtension) throws InvalidParametersException {
        if (!deadlineExtension.isValid()) {
            throw new InvalidParametersException(deadlineExtension.getInvalidityInfo());
        }
    }
}
