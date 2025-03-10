package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.DeadlineExtensionsDb;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.User;

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

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(DeadlineExtensionsDb deadlineExtensionsDb, FeedbackSessionsLogic feedbackSessionsLogic) {
        this.deadlineExtensionsDb = deadlineExtensionsDb;
        this.feedbackSessionsLogic = feedbackSessionsLogic;
    }

    /**
     * Get extended deadline end time for this session and user if it exists, otherwise get the deadline of the session.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        Instant extendedDeadline =
                getExtendedDeadlineForUser(session, user);

        if (extendedDeadline == null) {
            return session.getEndTime();
        }

        return extendedDeadline;
    }

    /**
     * Get extended deadline end time for this session and user if it exists, otherwise return null.
     */
    public Instant getExtendedDeadlineForUser(FeedbackSession feedbackSession, User user) {
        DeadlineExtension deadlineExtension =
                deadlineExtensionsDb.getDeadlineExtension(user.getId(), feedbackSession.getId());
        if (deadlineExtension == null) {
            return null;
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
        return deadlineExtensionsDb.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     */
    public void deleteDeadlineExtension(DeadlineExtension de) {
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
        return deadlineExtensionsDb.updateDeadlineExtension(de);
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
            List<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();

            deadlineExtensions = deadlineExtensions
                    .stream()
                    .filter(deadlineExtension -> deadlineExtension.getUser().equals(user))
                    .collect(Collectors.toList());

            for (DeadlineExtension deadlineExtension : deadlineExtensions) {
                deleteDeadlineExtension(deadlineExtension);
            }
        });
    }
}
