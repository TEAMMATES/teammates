package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

public class InstructorFeedbackResponseCommentDeleteAction extends FeedbackResponseCommentDeleteAction {

    @Override
    protected void verifyAccessibleForSpecificUser(FeedbackSessionAttributes fsa, FeedbackResponseAttributes response) {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        boolean isCreatorOnly = true;
        gateKeeper.verifyAccessible(instructor, fsa, !isCreatorOnly, response.giverSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, fsa, !isCreatorOnly, response.recipientSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
    }

    @Override
    protected void setStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment) {
        statusToAdmin += "InstructorFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + feedbackResponseComment.getId() + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
    }
}
