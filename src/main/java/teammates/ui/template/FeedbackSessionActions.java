package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.ui.controller.PageData;

public class FeedbackSessionActions {
    
    private boolean hasActions;
    private boolean privateSession;

    private boolean hasUnpublish;
    private boolean hasPublish;
    
    private boolean hasSubmit;
    private boolean hasRemind;
    
    private String courseId;
    private String fsName;

    private String resultsLink;
    private String editLink;
    private String deleteLink;
    private String submitLink;
    private String remindLink;
    private String remindParticularStudentsLink;
    private String editCopyLink;
    private String unpublishLink;
    private String publishLink;

    private boolean allowedToEdit;
    private boolean allowedToDelete;
    private boolean allowedToSubmit;
    private boolean allowedToRemind;
    private boolean allowedToUnpublish;
    private boolean allowedToPublish;

    private String toggleDeleteFeedbackSessionParams;
    private String toggleRemindStudentsParams;
    private String toggleUnpublishSessionParams;
    private String togglePublishSessionParams;

    public FeedbackSessionActions(boolean hasActions, PageData data, FeedbackSessionAttributes session,
                                  boolean isHome, InstructorAttributes instructor,
                                  List<String> sectionsInCourse) {
        String courseId = session.courseId;
        String feedbackSessionName = session.feedbackSessionName;
        this.hasActions = hasActions;
        if (hasActions) {
            this.privateSession = session.isPrivateSession();

            this.hasUnpublish = !session.isWaitingToOpen() && session.isPublished();
            this.hasPublish = !session.isWaitingToOpen() && !session.isPublished();

            this.hasSubmit = session.isVisible() || session.isPrivateSession();
            this.hasRemind = session.isOpened();

            this.courseId = Sanitizer.sanitizeForHtml(courseId);
            this.fsName = Sanitizer.sanitizeForHtml(feedbackSessionName);

            this.resultsLink = data.getInstructorFeedbackSessionResultsLink(courseId, feedbackSessionName);
            this.editLink = data.getInstructorFeedbackSessionEditLink(courseId, feedbackSessionName);
            this.deleteLink = data.getInstructorFeedbackSessionDeleteLink(courseId, feedbackSessionName,
                                                                          (isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE 
                                                                                  : Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE));
            this.submitLink = data.getInstructorFeedbackSessionSubmitLink(courseId, feedbackSessionName);
            this.remindLink = data.getInstructorFeedbackSessionRemindLink(courseId, feedbackSessionName);
            this.remindParticularStudentsLink = data.getInstructorFeedbackSessionRemindParticularStudentsPageLink(courseId,
                                                                                                       feedbackSessionName);
            this.editCopyLink = data.getFeedbackSessionEditCopyLink();
            this.unpublishLink = data.getInstructorFeedbackSessionUnpublishLink(courseId, feedbackSessionName, isHome);
            this.publishLink = data.getInstructorFeedbackSessionPublishLink(courseId, feedbackSessionName, isHome);

            this.allowedToEdit = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            this.allowedToDelete = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            boolean shouldEnableSubmitLink = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            for (String section : sectionsInCourse) {
                if (instructor.isAllowedForPrivilege(section, 
                                                     session.feedbackSessionName, 
                                                     Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                    shouldEnableSubmitLink = true;
                    break;
                }
            }
            this.allowedToSubmit = shouldEnableSubmitLink;
            this.allowedToRemind = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) && hasRemind;
            this.allowedToUnpublish = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            this.allowedToPublish = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

            this.toggleDeleteFeedbackSessionParams = "'" + Sanitizer.sanitizeForJs(courseId) + "','"
                                                   + Sanitizer.sanitizeForJs(feedbackSessionName) + "'";
            this.toggleRemindStudentsParams = "'" + Sanitizer.sanitizeForJs(feedbackSessionName) + "'";
            this.toggleUnpublishSessionParams = "'" + Sanitizer.sanitizeForJs(feedbackSessionName) + "'";
            this.togglePublishSessionParams = "'" + Sanitizer.sanitizeForJs(feedbackSessionName) + "', "
                                            + session.isPublishedEmailEnabled;
        }
    }

    public boolean isHasActions() {
        return hasActions;
    }

    public boolean isPrivateSession() {
        return privateSession;
    }

    public boolean isHasUnpublish() {
        return hasUnpublish;
    }

    public boolean isHasPublish() {
        return hasPublish;
    }

    public boolean isHasSubmit() {
        return hasSubmit;
    }

    public boolean isHasRemind() {
        return hasRemind;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }

    public String getResultsLink() {
        return resultsLink;
    }

    public String getEditLink() {
        return editLink;
    }

    public String getDeleteLink() {
        return deleteLink;
    }

    public String getSubmitLink() {
        return submitLink;
    }

    public String getRemindLink() {
        return remindLink;
    }

    public String getRemindParticularStudentsLink() {
        return remindParticularStudentsLink;
    }

    public String getEditCopyLink() {
        return editCopyLink;
    }

    public String getUnpublishLink() {
        return unpublishLink;
    }

    public String getPublishLink() {
        return publishLink;
    }

    public boolean isAllowedToEdit() {
        return allowedToEdit;
    }

    public boolean isAllowedToDelete() {
        return allowedToDelete;
    }

    public boolean isAllowedToSubmit() {
        return allowedToSubmit;
    }

    public boolean isAllowedToRemind() {
        return allowedToRemind;
    }

    public boolean isAllowedToUnpublish() {
        return allowedToUnpublish;
    }

    public boolean isAllowedToPublish() {
        return allowedToPublish;
    }

    public String getToggleDeleteFeedbackSessionParams() {
        return toggleDeleteFeedbackSessionParams;
    }

    public String getToggleRemindStudentsParams() {
        return toggleRemindStudentsParams;
    }

    public String getToggleUnpublishSessionParams() {
        return toggleUnpublishSessionParams;
    }

    public String getTogglePublishSessionParams() {
        return togglePublishSessionParams;
    }

}
