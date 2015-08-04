package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.util.Sanitizer;

public class FeedbackResultsTable {

    private String studentName;
    private List<FeedbackResponsePersonRow> receivedResponses;
    private List<FeedbackResponsePersonRow> givenResponses;

    public FeedbackResultsTable(int fbIndex, String studentName, FeedbackSessionResultsBundle result) {
        this.studentName = Sanitizer.sanitizeForHtml(studentName);

        this.receivedResponses = new ArrayList<FeedbackResponsePersonRow>();
        Map<String, List<FeedbackResponseAttributes>> received =
                                        result.getResponsesSortedByRecipient().get(studentName);
        int giverIndex = 0;
        if (received != null) {
            for (String giver : received.keySet()) {
                giverIndex++;
                this.receivedResponses.add(new FeedbackResponsePersonRow(fbIndex, giverIndex, giver, "giver",
                                                                         received.get(giver), result));
            }
        }

        this.givenResponses = new ArrayList<FeedbackResponsePersonRow>();
        Map<String, List<FeedbackResponseAttributes>> given = result.getResponsesSortedByGiver().get(studentName);
        int recipientIndex = 0;
        if (given != null) {
            for (String recipient : given.keySet()) {
                recipientIndex++;
                this.givenResponses.add(new FeedbackResponsePersonRow(fbIndex, recipientIndex, recipient, "recipient",
                                                                      given.get(recipient), result));
            }
        }
    }

    public String getStudentName() {
        return studentName;
    }

    public List<FeedbackResponsePersonRow> getReceivedResponses() {
        return receivedResponses;
    }

    public List<FeedbackResponsePersonRow> getGivenResponses() {
        return givenResponses;
    }

}
