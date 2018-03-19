package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;

public class FeedbackResultsTable {

    private String studentName;
    private List<FeedbackResponsePersonRow> receivedResponses;
    private List<FeedbackResponsePersonRow> givenResponses;

    public FeedbackResultsTable(int fbIndex, String studentName, FeedbackSessionResultsBundle result,
                                boolean hasStudentSubmitted) {
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

            boolean[] hasAnsweredContrib = { false };
            boolean[] contribExistsFromOtherStudent = { false };
            FeedbackResponseAttributes[] response = new FeedbackResponseAttributes[1];

            received.forEach((key, value) -> {
                for (FeedbackResponseAttributes feedbackResponseAttributes : value) {
                    FeedbackQuestionType questionType = feedbackResponseAttributes.feedbackQuestionType;
                    String recipient = feedbackResponseAttributes.recipient;
                    String giver = feedbackResponseAttributes.giver;
                    if (questionType.equals(FeedbackQuestionType.CONTRIB)) {
                        response[0] = new FeedbackResponseAttributes(feedbackResponseAttributes);
                        contribExistsFromOtherStudent[0] = true;
                    }
                    if (giver.equals(recipient) && questionType.equals(FeedbackQuestionType.CONTRIB)) {
                        hasAnsweredContrib[0] = true;
                    }
                }
            });
            // if student has not submitted the session or CONTRIB response
            // about himself then student's submission to CONTRIB is shown No
            // Response and PC is shown next to it
            if ((!hasStudentSubmitted || !hasAnsweredContrib[0]) && contribExistsFromOtherStudent[0]) {
                giverIndex++;
                response[0].giver = response[0].recipient;
                response[0].giverSection = response[0].recipientSection;
                List<FeedbackResponseAttributes> responses = new ArrayList<>();
                responses.add(response[0]);
                FeedbackResponsePersonRow contribPcRow = new FeedbackResponsePersonRow(fbIndex, giverIndex, studentName,
                                                                                        "giver", responses, result);
                String responseText = contribPcRow.getResponses().get(0).getResponseText();
                String newResponseText = "<span>No Response</span>"
                        + responseText.substring(responseText.indexOf("</span>") + "</span>".length());
                contribPcRow.getResponses().get(0).setResponseText(newResponseText);
                this.receivedResponses.add(contribPcRow);
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
