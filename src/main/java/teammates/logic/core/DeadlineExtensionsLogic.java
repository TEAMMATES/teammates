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

    private final DeadlineExtensionsDb deDb = DeadlineExtensionsDb.inst();

    private DeadlineExtensionsLogic() {
        // prevent initialization
    }

    public static DeadlineExtensionsLogic inst() {
        return instance;
    }

    /**
     * Updates a deadline extension.
     *
     * <p>If {@code endTimeOption} is present and {@code sentClosingSoonEmailOption}
     * is not explicitly set, update {@code sentClosingSoonEmailOption} to false.
     *
     * @return the updated deadline extension
     * @throws InvalidParametersException if the updated deadline extension is not valid
     * @throws EntityDoesNotExistException if the deadline extension to update does not exist
     */
    public DeadlineExtensionAttributes updateDeadlineExtension(DeadlineExtensionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {

        // reset sentClosingSoonEmail if the session deadline is updated and sentClosingSoonEmailOption is not explicitly set
        if (updateOptions.isEndTimeOptionPresent() && !updateOptions.isSentClosingSoonEmailOptionPresent()) {
            return deDb.updateDeadlineExtension(DeadlineExtensionAttributes.updateOptionsBuilder(updateOptions)
                    .withSentClosingSoonEmail(false)
                    .build());
        }

        return deDb.updateDeadlineExtension(updateOptions);
    }

    /**
     * Updates all deadline extensions of a user in a course with new email.
     */
    public void updateDeadlineExtensionsWithNewEmail(String courseId, String oldEmail,
            String newEmail, boolean isInstructor) throws InvalidParametersException {
        deDb.updateDeadlineExtensionsWithNewEmail(courseId, oldEmail, newEmail, isInstructor);
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
        return deDb.createEntity(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        deDb.deleteDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
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

        deDb.deleteDeadlineExtensions(query);
    }

    /**
     * Deletes deadline extensions using {@link AttributesDeletionQuery}.
     */
    public void deleteDeadlineExtensions(AttributesDeletionQuery query) {
        assert query != null;
        deDb.deleteDeadlineExtensions(query);
    }

    /**
     * Gets a deadline extension by {@code courseId}, {@code feedbackSessionName},
     * {@code userEmail} and {@code isInstructor}.
     *
     * @return the deadline extension if it exists, null otherwise
     */
    public DeadlineExtensionAttributes getDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        return deDb.getDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Gets a list of deadline extensions with end time coming up soon
     * and possibly need a closing soon email to be sent.
     */
    public List<DeadlineExtensionAttributes> getDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        return deDb.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
    }

}
