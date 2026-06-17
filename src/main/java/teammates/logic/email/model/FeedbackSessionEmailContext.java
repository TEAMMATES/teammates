package teammates.logic.email.model;

import java.util.List;
import java.util.UUID;

/**
 * Shared email context for feedback session-related emails.
 *
 * @param feedbackSessionId the unique identifier of the feedback session
 * @param courseId the unique identifier of the course
 * @param courseName the name of the course
 * @param courseTimeZone the time zone of the course
 * @param feedbackSessionName the name of the feedback session
 * @param sessionInstructions the instructions for the feedback session
 * @param coOwnerContacts the list of co-owner contacts for the feedback session
 */
public record FeedbackSessionEmailContext(
        UUID feedbackSessionId,
        String courseId,
        String courseName,
        String courseTimeZone,
        String feedbackSessionName,
        String sessionInstructions,
        List<EmailContact> coOwnerContacts
) {
}
