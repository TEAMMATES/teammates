package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.DeadlineExtensionsDb;

/**
 * Handles the logic related to deadline extensions.
 */
public final class DeadlineExtensionsLogic {

    private static final DeadlineExtensionsLogic instance = new DeadlineExtensionsLogic();

    private final DeadlineExtensionsDb deadlineExtensionsDb = DeadlineExtensionsDb.inst();

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    /**
     * Updates a deadline extension.
     *
     * @return the updated deadline extension
     * @throws InvalidParametersException if the updated deadline extension is not valid
     * @throws EntityDoesNotExistException if the deadline extension to update does not exist
     */
    public DeadlineExtensionAttributes updateDeadlineExtension(DeadlineExtensionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        return deadlineExtensionsDb.updateDeadlineExtension(updateOptions);
    }

    /**
     * Updates all deadline extensions of a user in a course with new email.
     */
    public void updateDeadlineExtensionsWithNewEmail(String courseId, String oldEmail,
            String newEmail, boolean isInstructor) throws InvalidParametersException {
        deadlineExtensionsDb.updateDeadlineExtensionsWithNewEmail(courseId, oldEmail, newEmail, isInstructor);
    }

    /**
     * Creates a deadline extension.
     *
     * @return the created deadline extension
     * @throws InvalidParametersException if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension to create already exists
     */
    public DeadlineExtensionAttributes createDeadlineExtension(DeadlineExtensionAttributes deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return deadlineExtensionsDb.createEntity(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        deadlineExtensionsDb.deleteDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Deletes all deadline extensions for a user in a course.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtensions(String courseId, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert userEmail != null;

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withUserEmail(userEmail)
                .withIsInstructor(isInstructor)
                .build();

        deadlineExtensionsDb.deleteDeadlineExtensions(query);
    }

    /**
     * Deletes deadline extensions using {@link AttributesDeletionQuery}.
     */
    public void deleteDeadlineExtensions(AttributesDeletionQuery query) {
        assert query != null;
        deadlineExtensionsDb.deleteDeadlineExtensions(query);
    }

    /**
     * Gets a deadline extension by {@code courseId}, {@code feedbackSessionName},
     * {@code userEmail} and {@code isInstructor}.
     *
     * @return the deadline extension if it exists, null otherwise
     */
    public DeadlineExtensionAttributes getDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        return deadlineExtensionsDb.getDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Gets a list of deadline extensions with end time within the next 24 hours
     * and possibly need a closing email to be sent.
     */
    public List<DeadlineExtensionAttributes> getDeadlineExtensionsPossiblyNeedingClosingEmail() {
        return deadlineExtensionsDb.getDeadlineExtensionsPossiblyNeedingClosingEmail();
    }

}
