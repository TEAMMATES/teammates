package teammates.logic.email.model;

import java.util.List;

/**
 * Data required to render a feedback session published email for a participant.
 *
 * @param recipientEmailAddress the email recipient
 * @param recipientName the recipient name
 * @param courseId the course ID
 * @param courseName the course name
 * @param feedbackSessionName the feedback session name
 * @param reportUrl the recipient-specific results URL
 * @param isInstructor whether the recipient is an instructor
 * @param coOwnerContacts the contactable co-owner instructors for the course
 */
public record FeedbackSessionPublishedParticipantEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String courseId,
        String courseName,
        String feedbackSessionName,
        String reportUrl,
        boolean isInstructor,
        List<EmailContact> coOwnerContacts) {
}
