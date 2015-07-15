package teammates.ui.template;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;

public class InstructorFeedbackResponseComment {
    private Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    private Map<String, String> giverNames;
    private Map<String, String> recipientNames;

    public InstructorFeedbackResponseComment(Map<String, FeedbackSessionResultsBundle> feedbackResultBundles) {
        this.feedbackResultBundles = feedbackResultBundles;

        initializeGiverAndRecipientNames();
    }

    private void initializeGiverAndRecipientNames() {
        for (String bundleKey : feedbackResultBundles.keySet()) {
            FeedbackSessionResultsBundle bundle = feedbackResultBundles.get(bundleKey);
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseComments = 
                bundle.getResponseComments();

            for (FeedbackQuestionAttributes attributeKey : responseComments.keySet()) {
                List<FeedbackResponseAttributes> responseEntries = responseComments.get(attributeKey);

                for (FeedbackResponseAttributes responseEntry : responseEntries) {
                    String giverEmail = responseEntry.giverEmail;
                    String giverName = bundle.emailNameTable.get(giverEmail);
                    String giverTeamName = bundle.emailTeamNameTable.get(giverEmail);

                    String recipientEmail = responseEntry.recipientEmail;
                    String recipientName = bundle.emailNameTable.get(recipientEmail);
                    String recipientTeamName = bundle.emailTeamNameTable.get(recipientEmail);

                    String appendedGiverName = bundle.appendTeamNameToName(giverName, giverTeamName);
                    String appendedRecipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);

                    giverNames.put(giverEmail, appendedGiverName);
                    recipientNames.put(recipientEmail, appendedRecipientName);
                }
            }
        }
    }

    public Map<String, String> getGiverNames() {
        return giverNames;
    }

    public Map<String, String> getRecipientNames() {
        return recipientNames;
    }
}