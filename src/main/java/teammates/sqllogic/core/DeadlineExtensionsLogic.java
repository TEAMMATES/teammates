package teammates.sqllogic.core;

import java.time.Instant;

import teammates.common.exception.EntityAlreadyExistsException;
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

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(DeadlineExtensionsDb deadlineExtensionsDb) {
        this.deadlineExtensionsDb = deadlineExtensionsDb;
    }

    /**
     * Get extended deadline for this session and user if it exists, otherwise get the deadline of the session.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        DeadlineExtension deadlineExtension =
                deadlineExtensionsDb.getDeadlineExtensionForUser(session.getId(), user.getId());

        if (deadlineExtension == null) {
            return session.getEndTime();
        }

        return deadlineExtension.getEndTime();
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
}
