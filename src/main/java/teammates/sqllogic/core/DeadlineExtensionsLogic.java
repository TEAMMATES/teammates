package teammates.sqllogic.core;

import java.time.Instant;

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
    private DeadlineExtensionsDb deDb;

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code DeadlineExtensionsLogic}.
     */
    void initLogicDependencies(DeadlineExtensionsDb deDb) {
        this.deDb = deDb;
    }

    /**
     * Get extended deadline for this session and user if it exists, otherwise get the deadline of the session.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        DeadlineExtension de = deDb.getDeadlineExtensionForUser(session.getId(), user.getId());

        if (de == null) {
            return session.getEndTime();
        }
        return de.getEndTime();
    }
}