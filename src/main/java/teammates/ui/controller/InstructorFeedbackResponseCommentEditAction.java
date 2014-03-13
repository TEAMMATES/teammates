package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackResponseCommentEditAction extends Action {
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
		Assumption.assertNotNull("null response comment id", feedbackResponseCommentId);
		
		String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
		Assumption.assertNotNull("null comment text", commentText);
		if (commentText.trim().isEmpty()) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
			isError = true;
			return createRedirectToFeedbackResultsPageResult();
		}
		
		FeedbackResponseCommentAttributes frc = new FeedbackResponseCommentAttributes(
				courseId, feedbackSessionName, null, instructor.email, null, null,
				new Text(commentText));
		frc.setId(Long.parseLong(feedbackResponseCommentId));
		
		try {
			logic.updateFeedbackResponseComment(frc);
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		if (!isError) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EDITED);
			statusToAdmin += "InstructorFeedbackResponseCommentEditAction:<br>"
					+ "Editing feedback response comment: " + frc.getId() + "<br>"
					+ "in course/feedback session: " + frc.courseId + "/" + frc.feedbackSessionName + "<br>"
					+ "by: " + frc.giverEmail + "<br>"
					+ "comment text: " + frc.commentText.getValue();
		}

		return createRedirectToFeedbackResultsPageResult();
	}

	private RedirectResult createRedirectToFeedbackResultsPageResult() {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
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
