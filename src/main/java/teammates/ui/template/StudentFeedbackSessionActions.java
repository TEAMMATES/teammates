package teammates.ui.template;

import teammates.common.datatransfer.FeedbackSessionAttributes;
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
