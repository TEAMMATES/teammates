package teammates.ui.template;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.pagedata.PageData;

public class InstructorFeedbackSessionActions {

    private static final String PUBLISH_BUTTON_TYPE = "btn-default btn-xs";

    private boolean privateSession;

    private String courseId;
    private String fsName;

    private String resultsLink;
    private String editLink;
    private String deleteLink;
    private String submitLink;
    private String remindLink;
    private String remindParticularStudentsPageLink;
    private String editCopyLink;

    private boolean allowedToEdit;
    private boolean allowedToDelete;
    private boolean allowedToSubmit;
    private boolean allowedToRemind;

    private FeedbackSessionPublishButton publishButton;

    public InstructorFeedbackSessionActions(PageData data, FeedbackSessionAttributes session, String returnUrl,
                                            InstructorAttributes instructor) {
        String courseId = session.getCourseId();
        String feedbackSessionName = session.getFeedbackSessionName();

        this.privateSession = session.isPrivateSession();

        this.courseId = courseId;
        this.fsName = feedbackSessionName;

        this.resultsLink = data.getInstructorFeedbackResultsLink(courseId, feedbackSessionName);
        this.editLink = data.getInstructorFeedbackEditLink(courseId, feedbackSessionName);
        this.deleteLink = data.getInstructorFeedbackDeleteLink(courseId, feedbackSessionName, returnUrl);
        this.submitLink = data.getInstructorFeedbackSubmissionEditLink(courseId, feedbackSessionName);
        this.remindLink = data.getInstructorFeedbackRemindLink(courseId, feedbackSessionName, returnUrl);
        this.remindParticularStudentsPageLink =
                data.getInstructorFeedbackRemindParticularStudentsPageLink(courseId, feedbackSessionName);
        this.editCopyLink = data.getInstructorFeedbackEditCopyLink();

        this.allowedToEdit = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        this.allowedToDelete = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        boolean shouldEnableSubmitLink =
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        if (!shouldEnableSubmitLink) {
            shouldEnableSubmitLink =
                    instructor.isAllowedForPrivilegeAnySection(session.getFeedbackSessionName(),
                            Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        }

        this.allowedToSubmit = (session.isVisible() || session.isPrivateSession()) && shouldEnableSubmitLink;
        this.allowedToRemind =
                (session.isOpened() || session.isClosed() && !session.isPublished())
                && instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        this.publishButton = new FeedbackSessionPublishButton(data, session, returnUrl, instructor,
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

    public String getRemindParticularStudentsPageLink() {
        return remindParticularStudentsPageLink;
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
