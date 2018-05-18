package teammates.ui.template;

import java.util.List;

public class FeedbackSubmissionEditResponse {
    private int responseIndx;
    private boolean isExistingResponse;
    private List<String> recipientOptionsForQuestion;
    private String submissionFormHtml;
    private String responseId;
    private List<FeedbackResponseCommentRow> commentsOnResponses;
    private FeedbackResponseCommentRow feedbackResponseCommentAdd;;

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
                                    List<String> recipientOptionsForQuestion, String submissionFormHtml, String responseId) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
    }

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
                                          List<String> recipientOptionsForQuestion, String submissionFormHtml,
                                          String responseId, List<FeedbackResponseCommentRow> commentsOnResponses,
                                          FeedbackResponseCommentRow feedbackResponseCommentAdd) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
        this.commentsOnResponses = commentsOnResponses;
        this.feedbackResponseCommentAdd = feedbackResponseCommentAdd;
    }

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
                                          List<String> recipientOptionsForQuestion, String submissionFormHtml,
                                          String responseId, FeedbackResponseCommentRow feedbackResponseCommentAdd) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
        this.feedbackResponseCommentAdd = feedbackResponseCommentAdd;
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

    public List<FeedbackResponseCommentRow> getCommentsOnResponses() {
        return commentsOnResponses;
    }

    public FeedbackResponseCommentRow getFeedbackResponseCommentAdd() {
        return feedbackResponseCommentAdd;
    }
}
