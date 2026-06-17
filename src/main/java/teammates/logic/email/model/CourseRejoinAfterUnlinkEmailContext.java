package teammates.logic.email.model;

/**
 * Plain email model for the course rejoin email sent after account unlink.
 *
 * @param recipientEmailAddress the email address of the recipient
 * @param recipientName the name of the recipient
 * @param joinUrl the course join URL for the recipient
 */
public record CourseRejoinAfterUnlinkEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String joinUrl
) {
}
