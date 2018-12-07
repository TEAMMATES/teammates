package teammates.ui.template;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

public class StudentFeedbackSessionActions {

    private boolean isSubmitted;
    private boolean isSessionVisible;
    private boolean isSessionPublished;
    private String studentFeedbackResultsLink;
    private String studentFeedbackResponseEditLink;
    private String tooltipText;
    private String buttonText;

    public StudentFeedbackSessionActions(FeedbackSessionAttributes fs,
            String feedbackResultsLink, String feedbackResponseEditLink, boolean hasSubmitted) {
        this.isSubmitted = hasSubmitted;
        this.isSessionVisible = fs.isVisible();
        this.isSessionPublished = fs.isPublished();
        this.studentFeedbackResultsLink = feedbackResultsLink;
        this.studentFeedbackResponseEditLink = feedbackResponseEditLink;

        if (hasSubmitted) {
            if (fs.isOpened()) {
                this.tooltipText = Const.Tooltips.FEEDBACK_SESSION_EDIT_SUBMITTED_RESPONSE;
                this.buttonText = "Edit Submission";
            } else {
                this.tooltipText = Const.Tooltips.FEEDBACK_SESSION_VIEW_SUBMITTED_RESPONSE;
                this.buttonText = "View Submission";
            }
        } else {
            if (fs.isClosed()) {
                this.tooltipText = Const.Tooltips.FEEDBACK_SESSION_VIEW_SUBMITTED_RESPONSE;
                this.buttonText = fs.isOpened() ? "Edit Submission" : "View Submission";
            } else {
                this.tooltipText = fs.isWaitingToOpen() ? Const.Tooltips.FEEDBACK_SESSION_AWAITING
                                                       : Const.Tooltips.FEEDBACK_SESSION_SUBMIT;
                this.buttonText = "Start Submission";
            }
        }
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public boolean isSessionVisible() {
        return isSessionVisible;
    }

    public boolean isSessionPublished() {
        return isSessionPublished;
    }

    public String getStudentFeedbackResultsLink() {
        return studentFeedbackResultsLink;
    }

    public String getStudentFeedbackResponseEditLink() {
        return studentFeedbackResponseEditLink;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    public String getButtonText() {
        return buttonText;
    }

}
