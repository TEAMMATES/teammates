package teammates.logic.email.model;

import java.time.Instant;

/**
 * Data required to render a feedback session owner reminder email.
 *
 * @param recipientEmailAddress the email recipient
 * @param recipientName the recipient name
 * @param courseId the course ID
 * @param courseName the course name
 * @param courseTimeZone the course time zone
 * @param feedbackSessionName the feedback session name
 * @param startTime the session start time
 * @param deadline the session deadline
 * @param sessionInstructions the feedback session instructions
 * @param sessionEditUrl the edit URL for opening soon reminders
 * @param reportUrl the report URL for closed reminders
 * @param joinUrl the course join URL for unregistered co-owners
 */
public record FeedbackSessionOwnerReminderEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String courseId,
        String courseName,
        String courseTimeZone,
        String feedbackSessionName,
        Instant startTime,
        Instant deadline,
        String sessionInstructions,
        String sessionEditUrl,
        String reportUrl,
        String joinUrl) {
}
