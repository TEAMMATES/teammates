package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackResponseCommentDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
		
		new GateKeeper().verifyAccessible(
				instructor, 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				false);
		
		Long feedbackResponseCommentId = Long.parseLong(getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID));
		
		FeedbackResponseCommentAttributes frc = new FeedbackResponseCommentAttributes();
		frc.setId(feedbackResponseCommentId);
		
		logic.deleteFeedbackResponseComment(frc);
		
		statusToUser.add("Your comment has been deleted successfully");
		statusToAdmin += "InstructorFeedbackResponseCommentDeleteAction:<br>"
				+ "Deleting feedback response comment: " + frc.getId() + "<br>"
				+ "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
		
		String sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);
		
		String redirectUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.COURSE_ID, courseId);
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.USER_ID, account.googleId);
		redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, sortType);
		return createRedirectResult(redirectUrl);
	}

}
