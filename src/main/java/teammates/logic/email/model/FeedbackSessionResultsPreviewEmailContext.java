package teammates.logic.email.model;

import java.util.List;

/**
 * Data required to render a feedback session results-status preview email for co-owners.
 *
 * @param recipientEmailAddress the preview recipient
 * @param recipientName the preview recipient name
 * @param courseId the course ID
 * @param courseName the course name
 * @param feedbackSessionName the feedback session name
 * @param coOwnerContacts the contactable co-owner instructors for the course
 */
public record FeedbackSessionResultsPreviewEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String courseId,
        String courseName,
        String feedbackSessionName,
        List<EmailContact> coOwnerContacts) {
}
