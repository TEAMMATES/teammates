package teammates.ui.template;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
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
    private boolean isStatsShown;
    private boolean isMissingResponsesShown;

    public InstructorFeedbackResultsSessionPanel(FeedbackSessionAttributes session,
                                                 String editLink,
                                                 FeedbackSessionPublishButton feedbackSessionPublishButton,
                                                 String selectedSection,
                                                 boolean isMissingResponsesShown,
                                                 boolean isStatsShown) {
        this.courseId = SanitizationHelper.sanitizeForHtml(session.getCourseId());
        this.feedbackSessionName = SanitizationHelper.sanitizeForHtml(session.getFeedbackSessionName());
        this.editLink = editLink;
        this.startTime = TimeHelper.formatDateTimeForDisplay(session.getStartTime(), session.getTimeZone());
        this.endTime = TimeHelper.formatDateTimeForDisplay(session.getEndTime(), session.getTimeZone());
        this.resultsVisibleFrom = getResultsVisibleFromText(session);
        this.feedbackSessionPublishButton = feedbackSessionPublishButton;
        this.selectedSection = selectedSection;
        this.isStatsShown = isStatsShown;
        this.isMissingResponsesShown = isMissingResponsesShown;
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

    public boolean getIsStatsShown() {
        return isStatsShown;
    }

    public boolean getIsMissingResponsesShown() {
        return isMissingResponsesShown;
    }

    private String getResultsVisibleFromText(FeedbackSessionAttributes feedbackSession) {
        if (feedbackSession.getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            if (feedbackSession.getSessionVisibleFromTime().equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
                return TimeHelper.formatDateTimeForDisplay(
                        feedbackSession.getStartTime(), feedbackSession.getTimeZone());
            } else {
                return TimeHelper.formatDateTimeForDisplay(
                        feedbackSession.getSessionVisibleFromTime(), feedbackSession.getTimeZone());
            }
        } else if (feedbackSession.getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_LATER)) {
            return "I want to manually publish the results.";
        } else {
            return TimeHelper.formatDateTimeForDisplay(
                    feedbackSession.getResultsVisibleFromTime(), feedbackSession.getTimeZone());
        }
    }
}
