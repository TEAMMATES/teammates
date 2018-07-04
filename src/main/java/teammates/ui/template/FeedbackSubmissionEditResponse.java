package teammates.ui.template;

import java.util.List;

public class FeedbackSubmissionEditResponse {
    private int responseIndx;
    private boolean isExistingResponse;
    private List<String> recipientOptionsForQuestion;
    private String submissionFormHtml;
    private String responseId;
    private FeedbackResponseCommentRow feedbackParticipantCommentOnResponse;
    private FeedbackResponseCommentRow feedbackResponseCommentAdd;

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
                                          List<String> recipientOptionsForQuestion, String submissionFormHtml,
                                          String responseId) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
    }

    public int getResponseIndx() {
        return responseIndx;
    }

    public boolean isExistingResponse() {
        return isExistingResponse;
    }

    public List<String> getRecipientOptionsForQuestion() {
        return recipientOptionsForQuestion;
    }

    public String getSubmissionFormHtml() {
        return submissionFormHtml;
    }

    public String getResponseId() {
        return responseId;
    }

    public FeedbackResponseCommentRow getFeedbackParticipantCommentOnResponse() {
        return feedbackParticipantCommentOnResponse;
    }

    public FeedbackResponseCommentRow getFeedbackResponseCommentAdd() {
        return feedbackResponseCommentAdd;
    }

    public void setFeedbackParticipantCommentOnResponse(FeedbackResponseCommentRow feedbackParticipantCommentOnResponse) {
        this.feedbackParticipantCommentOnResponse = feedbackParticipantCommentOnResponse;
    }

    public void setFeedbackResponseCommentAdd(FeedbackResponseCommentRow feedbackResponseCommentAdd) {
        this.feedbackResponseCommentAdd = feedbackResponseCommentAdd;
    }
}
