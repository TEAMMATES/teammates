package teammates.logic.email.model;

/**
 * Recoverable links for a feedback session.
 *
 * @param feedbackSessionName the feedback session name
 * @param submitUrl the URL for submitting responses to the session
 * @param resultsUrl the URL for viewing the session results
 */
public record RecoverableSessionLink(
        String feedbackSessionName,
        String submitUrl,
        String resultsUrl) {
}
