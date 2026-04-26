package teammates.common.datatransfer;

import java.util.UUID;

import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * DTO representing a missing response.
 *
 * @param id the unique identifier for this missing response
 * @param feedbackQuestion the question for which the response is missing
 * @param giver the identifier of the response giver
 * @param giverSectionName the section name of the response giver
 * @param recipient the identifier of the response recipient
 * @param recipientSectionName the section name of the response recipient
 */
public record FeedbackMissingResponse(
        UUID id,
        FeedbackQuestion feedbackQuestion,
        String giver,
        String giverSectionName,
        String recipient,
        String recipientSectionName) {
    public FeedbackMissingResponse(
            FeedbackQuestion feedbackQuestion,
            String giver,
            String giverSectionName,
            String recipient,
            String recipientSectionName) {
        this(UUID.randomUUID(), feedbackQuestion, giver, giverSectionName, recipient, recipientSectionName);
    }
}
