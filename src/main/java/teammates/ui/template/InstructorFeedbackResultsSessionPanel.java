package teammates.ui.template;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;

public class InstructorFeedbackResultsSessionPanel {
    private String courseId;
    private String feedbackSessionName;
    private String editLink;
    private String startTime;
    private String endTime;
    private String resultsVisibleFrom;
    private FeedbackSessionPublishButton feedbackSessionPublishButton;
    private String selectedSection;

    public InstructorFeedbackResultsSessionPanel(FeedbackSessionAttributes session,
                                                 String editLink,
                                                 FeedbackSessionPublishButton feedbackSessionPublishButton,
                                                 String selectedSection) {
        this.courseId = Sanitizer.sanitizeForHtml(session.getCourseId());
        this.feedbackSessionName = Sanitizer.sanitizeForHtml(session.getFeedbackSessionName());
        this.editLink = editLink;
        this.startTime = TimeHelper.formatTime12H(session.getStartTime());
        this.endTime = TimeHelper.formatTime12H(session.getEndTime());
        this.resultsVisibleFrom = getResultsVisibleFromText(session);
        this.feedbackSessionPublishButton = feedbackSessionPublishButton;
        this.selectedSection = selectedSection;
    }
    
    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getEditLink() {
        return editLink;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getResultsVisibleFrom() {
        return resultsVisibleFrom;
    }

    public FeedbackSessionPublishButton getFeedbackSessionPublishButton() {
        return feedbackSessionPublishButton;
    }

    public String getSelectedSection() {
        return selectedSection;
    }
    
    private String getResultsVisibleFromText(FeedbackSessionAttributes feedbackSession) {
        if (feedbackSession.getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            if (feedbackSession.getSessionVisibleFromTime().equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
                return TimeHelper.formatTime12H(feedbackSession.getStartTime());
            } else if (feedbackSession.getSessionVisibleFromTime().equals(Const.TIME_REPRESENTS_NEVER)) {
                return "Never";
            } else {
                return TimeHelper.formatTime12H(feedbackSession.getSessionVisibleFromTime());
            }
        } else if (feedbackSession.getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_LATER)) {
            return "I want to manually publish the results.";
        } else if (feedbackSession.getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_NEVER)) {
            return "Never";
        } else {
            return TimeHelper.formatTime12H(feedbackSession.getResultsVisibleFromTime());
        }
    }
}
