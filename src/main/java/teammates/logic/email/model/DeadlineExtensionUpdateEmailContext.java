package teammates.logic.email.model;

import java.time.Instant;

import teammates.common.util.EmailType;

/**
 * Plain email model for a deadline extension update notification.
 *
 * @param recipientEmailAddress the email address of the recipient
 * @param recipientName the name of the recipient
 * @param isInstructor whether the recipient is an instructor
 * @param submitUrl the URL to the feedback session submission page
 * @param oldEndTime the old end time of the feedback session
 * @param newEndTime the new end time of the feedback session
 * @param emailType the type of the email
 */
public record DeadlineExtensionUpdateEmailContext(
        String recipientEmailAddress,
        String recipientName,
        boolean isInstructor,
        String submitUrl,
        Instant oldEndTime,
        Instant newEndTime,
        EmailType emailType
) {
}
