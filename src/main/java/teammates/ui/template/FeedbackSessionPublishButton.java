package teammates.ui.template;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.pagedata.PageData;

public class FeedbackSessionPublishButton {

    private String tooltipText;

    private String feedbackSessionName;
    private boolean isSendingPublishedEmail;

    private String actionName;
    private String actionLink;
    private boolean isActionAllowed;

    private String buttonType;

    public FeedbackSessionPublishButton(PageData data, FeedbackSessionAttributes session, String returnUrl,
                                        InstructorAttributes instructor, String buttonType) {
        String courseId = session.getCourseId();
        this.feedbackSessionName = session.getFeedbackSessionName();
        this.isSendingPublishedEmail = session.isPublishedEmailEnabled();

        boolean isUnpublishing = !session.isWaitingToOpen() && session.isPublished();
        this.isActionAllowed = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        if (isUnpublishing) {

            this.tooltipText = Const.Tooltips.FEEDBACK_SESSION_UNPUBLISH;
            this.actionName = "Unpublish";
            this.actionLink = data.getInstructorFeedbackUnpublishLink(courseId, feedbackSessionName, returnUrl);

        } else {

            boolean isReadyToPublish = !session.isWaitingToOpen() && !session.isPublished();
            this.tooltipText = isReadyToPublish ? Const.Tooltips.FEEDBACK_SESSION_PUBLISH
                                                : Const.Tooltips.FEEDBACK_SESSION_AWAITING;
            this.actionName = "Publish";
            this.actionLink = data.getInstructorFeedbackPublishLink(courseId, feedbackSessionName, returnUrl);
            this.isActionAllowed &= isReadyToPublish;
        }

        this.buttonType = buttonType;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isSendingPublishedEmail() {
        return isSendingPublishedEmail;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionNameLowercase() {
        return actionName.toLowerCase();
    }

    public String getActionLink() {
        return actionLink;
    }

    public boolean isActionAllowed() {
        return isActionAllowed;
    }

    public String getButtonType() {
        return buttonType;
    }

}
