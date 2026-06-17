package teammates.logic.email.model;

/**
 * Plain email model for the instructor course join invitation email.
 *
 * @param recipientEmailAddress the email address of the recipient
 * @param recipientName the name of the recipient
 * @param joinUrl the course join URL for the recipient
 * @param inviterName the name of the inviter
 * @param inviterEmail the email address of the inviter
 */
public record InstructorCourseJoinEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String joinUrl,
        String inviterName,
        String inviterEmail
) {
}
