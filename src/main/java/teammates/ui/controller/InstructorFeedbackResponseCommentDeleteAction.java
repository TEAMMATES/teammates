package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResponseCommentDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull("null course id", courseId);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull("null feedback session name", feedbackSessionName);
		
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
		
		new GateKeeper().verifyAccessible(
				instructor, 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				false);
		
		String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
		Assumption.assertNotNull("null feedback response comment id", feedbackResponseCommentId);
		
		FeedbackResponseCommentAttributes frc = new FeedbackResponseCommentAttributes();
		frc.setId(Long.parseLong(feedbackResponseCommentId));
		
		logic.deleteFeedbackResponseComment(frc);
		
		statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_DELETED);
		statusToAdmin += "InstructorFeedbackResponseCommentDeleteAction:<br>"
				+ "Deleting feedback response comment: " + frc.getId() + "<br>"
				+ "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
		
		String sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);
		Assumption.assertNotNull("null sort type", sortType);
		
		String redirectUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.COURSE_ID, courseId);
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.USER_ID, account.googleId);
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, sortType);
		return createRedirectResult(redirectUrl);
	}

}
