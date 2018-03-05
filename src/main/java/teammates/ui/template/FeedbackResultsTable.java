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
            boolean submissionStatus) {
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

            boolean hasAnsweredContrib = false;
            boolean contribExistsFromOtherStudent = false;
            FeedbackResponseAttributes response = new FeedbackResponseAttributes();

            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : received.entrySet()) {
                for (int j = 0; j < entry.getValue().size(); j++) {
                    FeedbackQuestionType questionType = entry.getValue().get(j).feedbackQuestionType;
                    String recipient = entry.getValue().get(j).recipient;
                    String giver = entry.getValue().get(j).giver;
                    if (questionType.equals(FeedbackQuestionType.CONTRIB)) {
                        response = new FeedbackResponseAttributes(entry.getValue().get(j));
                        contribExistsFromOtherStudent = true;
                    }
                    if (giver.equals(recipient) && questionType.equals(FeedbackQuestionType.CONTRIB)) {
                        hasAnsweredContrib = true;
                    }
                }
            }
            /*
             * if student has not submitted the session or CONTRIB response
             * about himself then student's submission to CONTRIB is shown No
             * Response and PC is shown next to it
             */
            if ((!submissionStatus || !hasAnsweredContrib) && contribExistsFromOtherStudent) {
                giverIndex++;
                response.giver = response.recipient;
                response.giverSection = response.recipientSection;
                List<FeedbackResponseAttributes> responses = new ArrayList<>();
                responses.add(response);
                FeedbackResponsePersonRow contribPcRow = new FeedbackResponsePersonRow(fbIndex, giverIndex, studentName,
                                                                                        "giver", responses, result);
                String responseText = contribPcRow.getResponses().get(0).getResponseText();
                String newResponseText = "<span>No Response</span>"
                        + responseText.substring(responseText.indexOf("</span>") + 7);
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
