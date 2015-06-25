package teammates.ui.template;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.StudentHomePageData;

public class StudentFeedbackSessionActions {

    private boolean hasSubmitted;
    private boolean sessionVisible;
    private boolean sessionPublished;
    private String studentFeedbackResultsLink;
    private String studentFeedbackResponseEditLink;
    private int index;
    private String tooltipText;
    private String buttonText;

    public StudentFeedbackSessionActions(StudentHomePageData data, FeedbackSessionAttributes fs, int index,
                                         boolean hasSubmitted) {
        this.hasSubmitted = hasSubmitted;
        this.sessionVisible = fs.isVisible();
        this.sessionPublished = fs.isPublished();
        this.studentFeedbackResultsLink = data.getStudentFeedbackResultsLink(fs.courseId, fs.feedbackSessionName);
        this.studentFeedbackResponseEditLink = data.getStudentFeedbackResponseEditLink(fs.courseId,
                                                                                       fs.feedbackSessionName);
        this.index = index;
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
                this.tooltipText= Const.Tooltips.FEEDBACK_SESSION_VIEW_SUBMITTED_RESPONSE;
                this.buttonText = fs.isOpened() ? "Edit Submission" : "View Submission";
            } else {
                this.tooltipText= fs.isWaitingToOpen() ? Const.Tooltips.FEEDBACK_SESSION_AWAITING
                                                       : Const.Tooltips.FEEDBACK_SESSION_SUBMIT;
                this.buttonText = "Start Submission";
            }
        }
    }

    public boolean isHasSubmitted() {
        return hasSubmitted;
    }

    public boolean isSessionVisible() {
        return sessionVisible;
    }

    public boolean isSessionPublished() {
        return sessionPublished;
    }

    public String getStudentFeedbackResultsLink() {
        return studentFeedbackResultsLink;
    }

    public String getStudentFeedbackResponseEditLink() {
        return studentFeedbackResponseEditLink;
    }

    public int getIndex() {
        return index;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    public String getButtonText() {
        return buttonText;
    }

}
