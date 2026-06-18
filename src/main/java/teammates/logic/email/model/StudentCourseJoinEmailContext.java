package teammates.logic.email.model;

/**
 * Plain email model for the student course join invitation email.
 *
 * @param recipientEmailAddress the email address of the recipient
 * @param recipientName the name of the recipient
 * @param joinUrl the course join URL for the recipient
 */
public record StudentCourseJoinEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String joinUrl
) {
}
