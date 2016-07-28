package teammates.ui.template;

import java.util.List;

public class InstructorFeedbackResponseComment {
    private List<FeedbackResponseCommentRow> feedbackResponseComments;
    private String answerHtml;
    private String giverName;
    private String recipientName;
    private boolean isInstructorAllowedToSubmit;
    private FeedbackResponseCommentRow feedbackResponseCommentAdd;

    public InstructorFeedbackResponseComment(String giverName, String recipientName,
                                             List<FeedbackResponseCommentRow> feedbackResponseComments,
                                             String answerHtml, boolean isInstructorAllowedToSubmit,
                                             FeedbackResponseCommentRow feedbackResponseCommentAdd) {
        this.giverName = giverName;
        this.recipientName = recipientName;
        this.feedbackResponseComments = feedbackResponseComments;
        this.answerHtml = answerHtml;
        this.isInstructorAllowedToSubmit = isInstructorAllowedToSubmit;
        this.feedbackResponseCommentAdd = feedbackResponseCommentAdd;
    }

    public List<FeedbackResponseCommentRow> getFeedbackResponseComments() {
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

    public FeedbackResponseCommentRow getFeedbackResponseCommentAdd() {
        return feedbackResponseCommentAdd;
    }
}
