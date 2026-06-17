package teammates.logic.email.model;

import java.time.Instant;

/**
 * Access links for a feedback session.
 *
 * @param feedbackSessionName the feedback session name
 * @param endTime the feedback session end time
 * @param submitUrl the URL for submitting responses to the session
 * @param resultsUrl the URL for viewing the session results
 */
public record SessionAccessLink(
        String feedbackSessionName,
        Instant endTime,
        String submitUrl,
        String resultsUrl) {
}
