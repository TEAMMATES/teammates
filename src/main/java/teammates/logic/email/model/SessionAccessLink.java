package teammates.logic.email.model;

/**
 * Access links for a feedback session.
 *
 * @param feedbackSessionName the feedback session name
 * @param submitUrl the URL for submitting responses to the session
 * @param resultsUrl the URL for viewing the session results
 */
public record SessionAccessLink(
        String feedbackSessionName,
        String submitUrl,
        String resultsUrl) {
}
