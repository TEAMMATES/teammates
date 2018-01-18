package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

public class FeedbackResultsTable {

    private String studentName;
    private List<FeedbackResponsePersonRow> receivedResponses;
    private List<FeedbackResponsePersonRow> givenResponses;

    public FeedbackResultsTable(int fbIndex, String studentName, FeedbackSessionResultsBundle result) {
        this.studentName = studentName;

        this.receivedResponses = new ArrayList<>();
        Map<String, List<FeedbackResponseAttributes>> received =
                                        result.getResponsesSortedByRecipient().get(studentName);
        int giverIndex = 0;
        if (received != null) {
            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : received.entrySet()) {
                giverIndex++;
                this.receivedResponses.add(new FeedbackResponsePersonRow(fbIndex, giverIndex, entry.getKey(), "giver",
                                                                         entry.getValue(), result));
            }
        }

        this.givenResponses = new ArrayList<>();
        Map<String, List<FeedbackResponseAttributes>> given = result.getResponsesSortedByGiver().get(studentName);
        int recipientIndex = 0;
        if (given != null) {
            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : given.entrySet()) {
                recipientIndex++;
                this.givenResponses.add(new FeedbackResponsePersonRow(fbIndex, recipientIndex, entry.getKey(), "recipient",
                                                                      entry.getValue(), result));
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
