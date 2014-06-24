package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackResponseCommentEditAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("null feedback session name", feedbackSessionName);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertNotNull("null feedback response id", feedbackResponseId);
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertNotNull("null response comment id", feedbackResponseCommentId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);
        boolean isCreatorOnly = true;
        
        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly, 
                response.giverSection, feedbackSessionName,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        new GateKeeper().verifyAccessible(instructor, session, !isCreatorOnly, 
                response.recipientSection, feedbackSessionName,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        
        InstructorFeedbackResponseCommentAjaxPageData data = 
                new InstructorFeedbackResponseCommentAjaxPageData(account);
        
        String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
        Assumption.assertNotNull("null comment text", commentText);
        if (commentText.trim().isEmpty()) {
            data.errorMessage = Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY;
            data.isError = true;
            return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }
        
        FeedbackResponseCommentAttributes feedbackResponseComment = new FeedbackResponseCommentAttributes(
                courseId, feedbackSessionName, null, instructor.email, null, null,
                new Text(commentText));
        feedbackResponseComment.setId(Long.parseLong(feedbackResponseCommentId));
        
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(response.feedbackQuestionId);
        if(question.showResponsesTo.size() > 0
            && (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                || question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                || question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS))){
            feedbackResponseComment.isPending = true;
        }
        
        try {
            logic.updateFeedbackResponseComment(feedbackResponseComment);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.errorMessage = e.getMessage();
            data.isError = true;
        }
        
        if (!data.isError) {
            statusToAdmin += "InstructorFeedbackResponseCommentEditAction:<br>"
                    + "Editing feedback response comment: " + feedbackResponseComment.getId() + "<br>"
                    + "in course/feedback session: " + feedbackResponseComment.courseId + "/" + feedbackResponseComment.feedbackSessionName + "<br>"
                    + "by: " + feedbackResponseComment.giverEmail + "<br>"
                    + "comment text: " + feedbackResponseComment.commentText.getValue();
        }
        
        data.comment = feedbackResponseComment;

        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
    }
}
