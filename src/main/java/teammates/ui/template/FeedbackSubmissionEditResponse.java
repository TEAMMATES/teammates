package teammates.ui.template;

import java.util.List;

public class FeedbackSubmissionEditResponse {
    private int responseIndx;
    private boolean isExistingResponse;
    private List<String> recipientOptionsForQuestion;
    private String submissionFormHtml;
    private String responseId;
    private List<FeedbackResponseCommentRow> commentsOnResponses;
    private FeedbackResponseCommentRow responseExplainationComment;

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
                                    List<String> recipientOptionsForQuestion, String submissionFormHtml, String responseId) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
    }

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
            List<String> recipientOptionsForQuestion, String submissionFormHtml, String responseId,
            List<FeedbackResponseCommentRow> commentsOnResponses, FeedbackResponseCommentRow responseExplaiantionComment) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
        this.commentsOnResponses = commentsOnResponses;
        this.responseExplainationComment = responseExplaiantionComment;
    }

    public FeedbackSubmissionEditResponse(int responseIndx, boolean isExistingResponse,
            List<String> recipientOptionsForQuestion, String submissionFormHtml, String responseId,
            FeedbackResponseCommentRow responseExplaiantionComment) {

        this.responseIndx = responseIndx;
        this.isExistingResponse = isExistingResponse;
        this.recipientOptionsForQuestion = recipientOptionsForQuestion;
        this.submissionFormHtml = submissionFormHtml;
        this.responseId = responseId;
        this.responseExplainationComment = responseExplaiantionComment;
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

    public FeedbackResponseCommentRow getResponseExplainationComment() {
        return responseExplainationComment;
    }

    public void setResponseExplainationComment(
            FeedbackResponseCommentRow responseExplainationComment) {
        this.responseExplainationComment = responseExplainationComment;
    }

    public List<FeedbackResponseCommentRow> getCommentsOnResponses() {
        return commentsOnResponses;
    }

    public void setCommentsOnResponses(List<FeedbackResponseCommentRow> commentsOnResponses) {
        this.commentsOnResponses = commentsOnResponses;
    }
}
