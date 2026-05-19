package teammates.common.datatransfer;

import java.util.UUID;

import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * DTO representing a missing response.
 *
 * @param id the unique identifier for this missing response
 * @param feedbackQuestion the question for which the response is missing
 * @param giver the response giver
 * @param recipient response recipient
 */
public record FeedbackMissingResponse(
        UUID id,
        FeedbackQuestion feedbackQuestion,
        ResponseGiver giver,
        ResponseRecipient recipient) {

    public FeedbackMissingResponse(
            FeedbackQuestion feedbackQuestion,
            ResponseGiver giver,
            ResponseRecipient recipient) {
        this(UUID.randomUUID(), feedbackQuestion, giver, recipient);
    }
}
