package teammates.ui.template;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.ui.controller.PageData;

public class FeedbackSessionPublishButton {

    private String tooltipText;

    private String actionName;
    private String actionLink;
    private boolean actionAllowed;

    private String onclickAction;
    private String buttonType;
    
    public FeedbackSessionPublishButton(PageData data, FeedbackSessionAttributes session, boolean isHome,
                                        InstructorAttributes instructor, String buttonType) {
        String courseId = session.courseId;
        String feedbackSessionName = session.feedbackSessionName;

        boolean isUnpublishing = !session.isWaitingToOpen() && session.isPublished();
        this.actionAllowed = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        if (isUnpublishing) {
            
            this.tooltipText = Const.Tooltips.FEEDBACK_SESSION_UNPUBLISH;
            this.actionName = "Unpublish";
            this.actionLink = data.getInstructorFeedbackUnpublishLink(courseId, feedbackSessionName, isHome);
            
            this.onclickAction = "toggleUnpublishEvaluation('" + Sanitizer.sanitizeForJs(feedbackSessionName) + "');";
            
        } else {
            
            boolean isReadyToPublish = !session.isWaitingToOpen() && !session.isPublished();
            this.tooltipText = isReadyToPublish ? Const.Tooltips.FEEDBACK_SESSION_PUBLISH
                                                : Const.Tooltips.FEEDBACK_SESSION_AWAITING;            
            this.actionName = "Publish";
            this.actionLink = data.getInstructorFeedbackPublishLink(courseId, feedbackSessionName, isHome);
            this.actionAllowed &= isReadyToPublish;
            
            this.onclickAction = "togglePublishEvaluation('" + Sanitizer.sanitizeForJs(feedbackSessionName) + "', "
                               + session.isPublishedEmailEnabled + ");";
            
        }

        this.buttonType = buttonType;
    }

    public String getTooltipText() {
        return tooltipText;
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
        return actionAllowed;
    }

    public String getOnclickAction() {
        return onclickAction;
    }

    public String getButtonType() {
        return buttonType;
    }

}
