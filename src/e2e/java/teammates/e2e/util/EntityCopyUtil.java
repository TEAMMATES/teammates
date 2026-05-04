package teammates.e2e.util;

import teammates.logic.entity.FeedbackSession;

/**
 * Utility class for copying entities for E2E tests.
 *
 * <p>This creates a copy with similar fields, but a null ID.
 * The copy is not persisted to the database, and is only used for testing
 * purposes.
 */
public final class EntityCopyUtil {
    private EntityCopyUtil() {
        // Utility class should not be instantiated
    }

    /**
     * Creates a copy of the given {@link FeedbackSession} with a null ID.
     * The copy is not persisted to the database, and is only used for testing purposes.
     */
    public static FeedbackSession copyFeedbackSession(FeedbackSession original) {
        FeedbackSession fs = new FeedbackSession(
                original.getName(), original.getCourse(), original.getCreatorEmail(), original.getInstructions(),
                original.getStartTime(),
                original.getEndTime(), original.getSessionVisibleFromTime(), original.getResultsVisibleFromTime(),
                original.getGracePeriod(), original.isClosingSoonEmailEnabled(),
                original.isPublishedEmailEnabled());
        fs.setId(null);
        fs.setCreatedAt(original.getCreatedAt());
        fs.setUpdatedAt(original.getUpdatedAt());
        fs.setDeletedAt(original.getDeletedAt());

        return fs;
    }
}
