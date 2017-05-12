package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackResponseCommentAjaxPageData;

/**
 * Action: Delete {@link FeedbackResponseCommentAttributes}.
 */
public class InstructorFeedbackResponseCommentDeleteAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("null feedback session name", feedbackSessionName);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertNotNull("null feedback response id", feedbackResponseId);
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertNotNull("null feedback response comment id", feedbackResponseCommentId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        verifyAccessibleForInstructorToFeedbackResponseComment(feedbackResponseCommentId,
                                                               instructor, session, response);

        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes();
        feedbackResponseComment.setId(Long.parseLong(feedbackResponseCommentId));

        logic.deleteDocument(feedbackResponseComment);
        logic.deleteFeedbackResponseComment(feedbackResponseComment);

        statusToAdmin += "InstructorFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + feedbackResponseComment.getId() + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";

        InstructorFeedbackResponseCommentAjaxPageData data =
                new InstructorFeedbackResponseCommentAjaxPageData(account);

        return createAjaxResult(data);
    }

    private void verifyAccessibleForInstructorToFeedbackResponseComment(
            String feedbackResponseCommentId, InstructorAttributes instructor,
            FeedbackSessionAttributes session, FeedbackResponseAttributes response) {
        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(Long.parseLong(feedbackResponseCommentId));
        if (frc == null) {
            return;
        }
        if (instructor != null && frc.giverEmail.equals(instructor.email)) { // giver, allowed by default
            return;
        }
        gateKeeper.verifyAccessible(instructor, session, false, response.giverSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        gateKeeper.verifyAccessible(instructor, session, false, response.recipientSection,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

}
