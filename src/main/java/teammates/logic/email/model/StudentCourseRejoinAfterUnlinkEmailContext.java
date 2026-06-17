package teammates.logic.email.model;

/**
 * Plain email model for the student course rejoin email sent after account unlink.
 *
 * @param recipientEmailAddress the email address of the recipient
 * @param recipientName the name of the recipient
 * @param joinUrl the course join URL for the recipient
 */
public record StudentCourseRejoinAfterUnlinkEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String joinUrl
) {
}
