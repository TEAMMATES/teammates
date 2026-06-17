package teammates.logic.email.model;

/**
 * Plain email model for the post-join course registration confirmation email.
 *
 * @param recipientEmailAddress the email address of the recipient
 * @param recipientName the name of the recipient
 * @param isInstructor whether the recipient is an instructor
 * @param appUrl the URL of the application
 */
public record UserCourseRegisteredEmailContext(
        String recipientEmailAddress,
        String recipientName,
        boolean isInstructor,
        String appUrl
) {
}
