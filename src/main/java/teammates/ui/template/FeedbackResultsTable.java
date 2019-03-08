package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;

public class FeedbackResultsTable {

    private String studentName;
    private List<FeedbackResponsePersonRow> receivedResponses;
    private List<FeedbackResponsePersonRow> givenResponses;

    public FeedbackResultsTable(String studentName, String studentEmail, FeedbackSessionResultsBundle result) {
        this.studentName = studentName;

        this.receivedResponses = new ArrayList<>();
        Map<String, List<FeedbackResponseAttributes>> received =
                                        result.getResponsesSortedByRecipient().get(studentName);
        if (received != null) {
            FeedbackResponseAttributes contribFeedbackResponse = findContribFeedbackResponse(received);
            if (contribFeedbackResponse != null && contribFeedbackResponse.recipient.equals(studentEmail)) {
                received.computeIfAbsent(studentName, k -> new ArrayList<>());
            }

            Map<String, List<FeedbackResponseAttributes>> receivedSorted = new TreeMap<>(received);
            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : receivedSorted.entrySet()) {
                if (contribFeedbackResponse != null && entry.getKey().equals(this.studentName)) {
                    List<FeedbackResponseAttributes> newResponseReceived = entry.getValue();
                    newResponseReceived.add(contribFeedbackResponse);
                    FeedbackResponseAttributes.sortFeedbackResponses(newResponseReceived);

                    this.receivedResponses.add(new FeedbackResponsePersonRow(entry.getKey(),
                                    "giver", newResponseReceived, result, true));
                } else {
                    this.receivedResponses.add(new FeedbackResponsePersonRow(entry.getKey(),
                                    "giver", entry.getValue(), result, false));
                }
            }
        }

        this.givenResponses = new ArrayList<>();
        Map<String, List<FeedbackResponseAttributes>> given = result.getResponsesSortedByGiver().get(studentName);
        if (given != null) {
            for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : given.entrySet()) {
                this.givenResponses.add(new FeedbackResponsePersonRow(entry.getKey(), "recipient",
                                                                      entry.getValue(), result, false));
            }
        }
    }

    private FeedbackResponseAttributes findContribFeedbackResponse(
            Map<String, List<FeedbackResponseAttributes>> receivedFeedbacks) {
        FeedbackResponseAttributes[] newResponseDetails = new FeedbackResponseAttributes[1];
        boolean[] hasContriFeedbackResponseRow = {false};

        // Checks and find the perceived contrib response for a student without submission
        receivedFeedbacks.entrySet().forEach(entry -> entry.getValue().forEach(questionResponses -> {
            if (questionResponses.getFeedbackQuestionType().equals(FeedbackQuestionType.CONTRIB)) {
                newResponseDetails[0] = new FeedbackResponseAttributes(questionResponses);
                if (entry.getKey().equals(this.studentName)) {
                    hasContriFeedbackResponseRow[0] = true;
                }
            }
        }));

        if (newResponseDetails[0] != null && !hasContriFeedbackResponseRow[0]) {
            return newResponseDetails[0];
        }
        return null;
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
