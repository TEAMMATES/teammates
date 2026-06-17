package teammates.logic.email.model;

import java.time.Instant;
import java.util.List;

/**
 * Data required to render a feedback session opened email for a participant.
 *
 * @param recipientEmailAddress the email recipient
 * @param recipientName the recipient name
 * @param courseId the course ID
 * @param courseName the course name
 * @param courseTimeZone the course time zone
 * @param feedbackSessionName the feedback session name
 * @param deadline the recipient-specific deadline
 * @param hasDeadlineExtension whether the deadline differs from the session default deadline
 * @param sessionInstructions the feedback session instructions
 * @param submitUrl the recipient-specific submit URL
 * @param isInstructor whether the recipient is an instructor
 * @param coOwnerContacts the contactable co-owner instructors for the course
 */
public record FeedbackSessionOpenedParticipantEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String courseId,
        String courseName,
        String courseTimeZone,
        String feedbackSessionName,
        Instant deadline,
        boolean hasDeadlineExtension,
        String sessionInstructions,
        String submitUrl,
        boolean isInstructor,
        List<EmailContact> coOwnerContacts) {
}
