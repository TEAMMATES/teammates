package teammates.ui.template;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.ui.controller.PageData;

public class InstructorFeedbackSessionActions {
    
    private boolean privateSession;

    private String courseId;
    private String fsName;

    private String resultsLink;
    private String editLink;
    private String deleteLink;
    private String submitLink;
    private String remindLink;
    private String remindParticularStudentsLink;
    private String editCopyLink;

    private boolean allowedToEdit;
    private boolean allowedToDelete;
    private boolean allowedToSubmit;
    private boolean allowedToRemind;

    private FeedbackSessionPublishButton publishButton;

    private static final String PUBLISH_BUTTON_TYPE = "btn-default btn-xs";

    public InstructorFeedbackSessionActions(PageData data, FeedbackSessionAttributes session, boolean isHome,
                                            InstructorAttributes instructor) {
        String courseId = session.courseId;
        String feedbackSessionName = session.feedbackSessionName;

        this.privateSession = session.isPrivateSession();

        this.courseId = Sanitizer.sanitizeForJs(courseId);
        this.fsName = Sanitizer.sanitizeForJs(feedbackSessionName);

        this.resultsLink = data.getInstructorFeedbackResultsLink(courseId, feedbackSessionName);
        this.editLink = data.getInstructorFeedbackEditLink(courseId, feedbackSessionName);
        this.deleteLink = data.getInstructorFeedbackDeleteLink(courseId, feedbackSessionName,
                                                                      (isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE 
                                                                              : Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE));
        this.submitLink = data.getInstructorFeedbackSubmissionEditLink(courseId, feedbackSessionName);
        this.remindLink = data.getInstructorFeedbackRemindLink(courseId, feedbackSessionName);
        this.remindParticularStudentsLink = data.getInstructorFeedbackRemindParticularStudentsLink(courseId,
                                                                                                     feedbackSessionName);
        this.editCopyLink = data.getInstructorFeedbackEditCopyLink();

        this.allowedToEdit = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        this.allowedToDelete = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        boolean shouldEnableSubmitLink = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        if (!shouldEnableSubmitLink) {
            shouldEnableSubmitLink = instructor.isAllowedForPrivilegeAnySection(session.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        }
        
        this.allowedToSubmit = (session.isVisible() || session.isPrivateSession()) && shouldEnableSubmitLink;
        this.allowedToRemind = session.isOpened() && instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            
        this.publishButton = new FeedbackSessionPublishButton(data, session, isHome, instructor,
                                                              PUBLISH_BUTTON_TYPE);
    }

    public boolean isPrivateSession() {
        return privateSession;
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

    public FeedbackSessionPublishButton getPublishButton() {
        return publishButton;
    }

}
