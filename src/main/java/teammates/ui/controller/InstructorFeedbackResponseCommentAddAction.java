package teammates.ui.controller;

import java.util.Date;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackResponseCommentAddAction extends Action {

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
		
		String commentText = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT);
		Assumption.assertNotNull("null comment text", commentText);
		if (commentText.trim().isEmpty()) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
			isError = true;
			return createRedirectToFeedbackResultsPageResult();
		}
		String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
		Assumption.assertNotNull("null feedback question id", feedbackQuestionId);
		String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
		Assumption.assertNotNull("null feedback response id", feedbackResponseId);
		
		FeedbackResponseCommentAttributes frc = new FeedbackResponseCommentAttributes(courseId,
			feedbackSessionName, feedbackQuestionId, instructor.email, feedbackResponseId, new Date(),
			new Text(commentText));
		
		try {
			logic.createFeedbackResponseComment(frc);
		} catch (EntityAlreadyExistsException e) {
			// Should only happen when the user pressed submit multiple times in
			// quick succession as the comments are differentiated using timestamps.
			// Alert admin in case the above assumption is not true but don't
			// show any error to user.
			setStatusForException(e, "");
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		if (!isError) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_SAVED);
			statusToAdmin += "InstructorFeedbackResponseCommentAddAction:<br>"
					+ "Adding comment to response: " + frc.feedbackResponseId + "<br>"
					+ "in course/feedback session: " + frc.courseId + "/" + frc.feedbackSessionName + "<br>"
					+ "by: " + frc.giverEmail + " at " + frc.createdAt + "<br>"
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
