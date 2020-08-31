package teammates.ui.output;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.StringHelper;

/**
 * The API output format of {@link FeedbackResponseAttributes}.
 */
public class FeedbackResponseData extends ApiOutput {

    private final String feedbackResponseId;

    private final String giverIdentifier;

    private final String recipientIdentifier;

    private final FeedbackResponseDetails responseDetails;

    public FeedbackResponseData(FeedbackResponseAttributes feedbackResponseAttributes) {
        this.feedbackResponseId = StringHelper.encrypt(feedbackResponseAttributes.getId());
        this.giverIdentifier = feedbackResponseAttributes.giver;
        this.recipientIdentifier = feedbackResponseAttributes.recipient;
        this.responseDetails = feedbackResponseAttributes.getResponseDetails();
    }

    public String getFeedbackResponseId() {
        return feedbackResponseId;
    }

    public String getGiverIdentifier() {
        return giverIdentifier;
    }

    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    public FeedbackResponseDetails getResponseDetails() {
        return responseDetails;
    }
}
