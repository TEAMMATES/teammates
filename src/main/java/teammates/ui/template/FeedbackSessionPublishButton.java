package teammates.ui.template;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.ui.controller.PageData;

public class FeedbackSessionPublishButton {

    private boolean hasUnpublish;
    private boolean hasPublish;

    private String tooltipText;

    private String unpublishLink;
    private String publishLink;

    private boolean allowedToUnpublish;
    private boolean allowedToPublish;

    private String toggleUnpublishSessionParams;
    private String togglePublishSessionParams;

    private String buttonType;
    
    public FeedbackSessionPublishButton(PageData data, FeedbackSessionAttributes session, boolean isHome,
                                        InstructorAttributes instructor, String buttonType) {
        String courseId = session.courseId;
        String feedbackSessionName = session.feedbackSessionName;

        this.hasUnpublish = !session.isWaitingToOpen() && session.isPublished();
        this.hasPublish = !session.isWaitingToOpen() && !session.isPublished();

        this.tooltipText = hasPublish ? Const.Tooltips.FEEDBACK_SESSION_PUBLISH
                                      : Const.Tooltips.FEEDBACK_SESSION_AWAITING;

        this.unpublishLink = data.getInstructorFeedbackUnpublishLink(courseId, feedbackSessionName, isHome);
        this.publishLink = data.getInstructorFeedbackPublishLink(courseId, feedbackSessionName, isHome);

        this.allowedToUnpublish = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        this.allowedToPublish = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        this.toggleUnpublishSessionParams = "'" + Sanitizer.sanitizeForJs(feedbackSessionName) + "'";
        this.togglePublishSessionParams = "'" + Sanitizer.sanitizeForJs(feedbackSessionName) + "', "
                                        + session.isPublishedEmailEnabled;

        this.buttonType = buttonType;
    }

    public boolean isHasUnpublish() {
        return hasUnpublish;
    }

    public boolean isHasPublish() {
        return hasPublish;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    public String getUnpublishLink() {
        return unpublishLink;
    }

    public String getPublishLink() {
        return publishLink;
    }

    public boolean isAllowedToUnpublish() {
        return allowedToUnpublish;
    }

    public boolean isAllowedToPublish() {
        return allowedToPublish;
    }

    public String getToggleUnpublishSessionParams() {
        return toggleUnpublishSessionParams;
    }

    public String getTogglePublishSessionParams() {
        return togglePublishSessionParams;
    }

    public String getButtonType() {
        return buttonType;
    }

}
