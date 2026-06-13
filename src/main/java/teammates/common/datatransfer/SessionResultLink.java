package teammates.common.datatransfer;

import java.util.UUID;

/**
 * Represents a submission link for a feedback session.
 *
 * @param feedbackSessionId        the ID of the feedback session
 * @param name                     the name of the feedback session
 * @param submissionStartTimestamp the start timestamp of the submission period
 * @param submissionEndTimestamp   the end timestamp of the submission period
 * @param timeZone                 the time zone of the feedback session
 * @param url                      the URL of the results page for the feedback
 *                                 session
 */
public record SessionResultLink(
        UUID feedbackSessionId,
        String name,
        long submissionStartTimestamp,
        long submissionEndTimestamp,
        String timeZone,
        String url) {
}
