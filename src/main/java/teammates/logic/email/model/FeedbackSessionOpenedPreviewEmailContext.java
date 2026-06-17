package teammates.logic.email.model;

import java.time.Instant;
import java.util.List;

/**
 * Data required to render a feedback session opened preview email for co-owners.
 *
 * @param recipientEmailAddress the preview recipient
 * @param recipientName the preview recipient name
 * @param courseId the course ID
 * @param courseName the course name
 * @param courseTimeZone the course time zone
 * @param feedbackSessionName the feedback session name
 * @param deadline the session deadline
 * @param sessionInstructions the feedback session instructions
 * @param coOwnerContacts the contactable co-owner instructors for the course
 */
public record FeedbackSessionOpenedPreviewEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String courseId,
        String courseName,
        String courseTimeZone,
        String feedbackSessionName,
        Instant deadline,
        String sessionInstructions,
        List<EmailContact> coOwnerContacts) {
}
