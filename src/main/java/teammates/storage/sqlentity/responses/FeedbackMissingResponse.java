package teammates.storage.sqlentity.responses;

import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * Represents a missing response.
 */
public class FeedbackMissingResponse extends FeedbackTextResponse {

    private String giverSectionName;
    private String recipientSectionName;

    protected FeedbackMissingResponse() {
        // required by Hibernate
    }

    public FeedbackMissingResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            String giverSectionName, String recipient, String recipientSectionName
    ) {
        super(feedbackQuestion, giver, null, recipient, null, new FeedbackTextResponseDetails(Const.MISSING_RESPONSE_TEXT));
        this.giverSectionName = giverSectionName;
        this.recipientSectionName = recipientSectionName;
    }

    @Override
    public String getGiverSectionName() {
        return giverSectionName;
    }

    @Override
    public String getRecipientSectionName() {
        return recipientSectionName;
    }
}
