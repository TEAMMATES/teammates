package teammates.ui.output;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackQuestionRecipientAttributes;

/**
 * API output for Feedback Question Recipients, which is a collection of {@link FeedbackQuestionRecipientData}.
 */
public class FeedbackQuestionRecipientsData extends ApiOutput {
    private List<FeedbackQuestionRecipientData> recipients;

    public FeedbackQuestionRecipientsData(List<FeedbackQuestionRecipientAttributes> recipients) {
        this.recipients = recipients.stream().map(FeedbackQuestionRecipientData::new)
                .sorted(Comparator.comparing(FeedbackQuestionRecipientData::getName))
                .collect(Collectors.toList());
    }

    public List<FeedbackQuestionRecipientData> getRecipients() {
        return recipients;
    }
}
