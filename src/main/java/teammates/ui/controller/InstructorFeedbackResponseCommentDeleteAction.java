package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResponseCommentDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("null feedback session name", feedbackSessionName);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(
                instructor, 
                session,
                !isCreatorOnly);
        
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertNotNull("null feedback response comment id", feedbackResponseCommentId);
        
        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes();
        feedbackResponseComment.setId(Long.parseLong(feedbackResponseCommentId));
        
        logic.deleteFeedbackResponseComment(feedbackResponseComment);
        
        statusToAdmin += "InstructorFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + feedbackResponseComment.getId() + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
        
        InstructorFeedbackResponseCommentAjaxPageData data = 
                new InstructorFeedbackResponseCommentAjaxPageData(account);
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
    }

}
