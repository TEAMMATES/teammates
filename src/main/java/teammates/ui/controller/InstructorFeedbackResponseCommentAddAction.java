package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: Create a new {@link FeedbackResponseCommentAttributes}.
 */
public class InstructorFeedbackResponseCommentAddAction extends FeedbackResponseCommentAddAction {

    @Override
    protected boolean isSpecificUserJoinedCourse() {
        return true;
    }

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
    protected String getUserEmailForCourse() {
        return logic.getInstructorForGoogleId(courseId, account.googleId).email;
    }

    @Override
    protected FeedbackSessionResultsBundle getDataBundle(String userEmail) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, userEmail);
    }

    @Override
    protected void setStatusToAdmin(FeedbackResponseCommentAttributes feedbackResponseComment) {
        statusToAdmin += "InstructorFeedbackResponseCommentAddAction:<br>"
                + "Adding comment to response: " + feedbackResponseComment.feedbackResponseId + "<br>"
                + "in course/feedback session: " + feedbackResponseComment.courseId + "/"
                + feedbackResponseComment.feedbackSessionName + "<br>"
                + "by: " + feedbackResponseComment.giverEmail + " at "
                + feedbackResponseComment.createdAt + "<br>"
                + "comment text: " + feedbackResponseComment.commentText.getValue();
    }

    @Override
    protected boolean isInstructor() {
        return true;
    }
}
