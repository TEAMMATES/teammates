package teammates.ui.template;

import java.util.List;

import teammates.ui.template.FeedbackResponseComment;

public class InstructorFeedbackResponseComment {
    private List<FeedbackResponseComment> feedbackResponseComments;
    private String answerHtml;
    private String giverName;
    private String recipientName;
    private boolean isInstructorAllowedToSubmit;
    private FeedbackResponseComment feedbackResponseCommentAdd;

    public InstructorFeedbackResponseComment(String giverName, String recipientName,
            List<FeedbackResponseComment> feedbackResponseComments, String answerHtml,
            boolean isInstructorAllowedToSubmit, FeedbackResponseComment feedbackResponseCommentAdd) {
        this.giverName = giverName;
        this.recipientName = recipientName;
        this.feedbackResponseComments = feedbackResponseComments;
        this.answerHtml = answerHtml;
        this.isInstructorAllowedToSubmit = isInstructorAllowedToSubmit;
        this.feedbackResponseCommentAdd = feedbackResponseCommentAdd;
    }

    public List<FeedbackResponseComment> getFeedbackResponseComments() {
        return feedbackResponseComments;
    }

    public String getAnswerHtml() {
        return answerHtml;
    }

    public String getGiverName() {
        return giverName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public boolean isInstructorAllowedToSubmit() {
        return isInstructorAllowedToSubmit;
    }

    public FeedbackResponseComment getFeedbackResponseCommentAdd() {
        return feedbackResponseCommentAdd;
    }
}
