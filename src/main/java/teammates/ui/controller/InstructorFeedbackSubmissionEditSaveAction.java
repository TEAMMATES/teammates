package teammates.ui.controller;

import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				false);
		
		InstructorFeedbackSubmissionEditPageData data = new InstructorFeedbackSubmissionEditPageData(account);

		// Get instructor email instead of account email.
		String instructorEmail = logic.getInstructorForGoogleId(courseId, account.googleId).email;

		data.bundle = logic.getFeedbackSessionQuestionsBundleForInstructor(feedbackSessionName, courseId, instructorEmail);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		if (data.bundle.feedbackSession.isOpened() == false &&
			data.bundle.feedbackSession.isPrivateSession() == false &&
			data.bundle.feedbackSession.isInGracePeriod() == false) {
			throw new UnauthorizedAccessException(
					"This feedback session is not open for submission.");
		}
		
		int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();
		for(int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
			String totalResponsesForQuestion = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL+"-"+questionIndx);
			if (totalResponsesForQuestion == null) {
				continue; // question has been skipped (not displayed).
			}
			int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);			
			for(int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++){
				FeedbackResponseAttributes response = extractFeedbackResponseData(requestParameters, questionIndx,responseIndx);
				response.giverEmail = instructorEmail;
				saveResponse(response);
			}
		}
		
		if (isError == false) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
		}
		statusToAdmin = "Show instructor feedback submission edit&save page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
		
		// TODO: what happens if qn is deleted as response is being submitted?
		// what happens if team/etc change such that receiver / response in general is invalid?
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
	}

	private void saveResponse(FeedbackResponseAttributes response)
			throws EntityDoesNotExistException {
		if (response.getId() != null) {
			// Delete away response if any empty fields
			if (response.responseMetaData.getValue().isEmpty() || 
				response.recipientEmail.isEmpty()) {
				logic.deleteFeedbackResponse(response);
				return;
			}
			try {
				logic.updateFeedbackResponse(response);
			} catch (EntityAlreadyExistsException | InvalidParametersException e) {
				setStatusForException(e);
			}
		} else if (!response.responseMetaData.getValue().isEmpty() &&
					!response.recipientEmail.isEmpty()){
			try {
				logic.createFeedbackResponse(response);
			} catch (EntityAlreadyExistsException | InvalidParametersException e) {
				setStatusForException(e);
			}
		}
	}
	
	private static FeedbackResponseAttributes extractFeedbackResponseData(Map<String, String[]> requestParameters, int questionIndx, int responseIndx) {
		FeedbackResponseAttributes response = new FeedbackResponseAttributes();

		//Can be null if new response
		response.setId(HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
		
		response.feedbackSessionName = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull(response.feedbackSessionName);
		
		response.courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(response.courseId);
		
		response.feedbackQuestionId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_ID+"-"+questionIndx);
		Assumption.assertNotNull(response.feedbackQuestionId);

		response.recipientEmail = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"	+ questionIndx + "-" + responseIndx);
		Assumption.assertNotNull(response.recipientEmail);
		
		response.feedbackQuestionType = FeedbackQuestionType.valueOf(HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TYPE+"-"+questionIndx));
		Assumption.assertNotNull(response.feedbackQuestionType);
		
		//Can be null if question is skipped
		String answer = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx);
		
		if(answer != null) {
			FeedbackAbstractResponseDetails responseDetails = FeedbackAbstractResponseDetails.createResponseDetails(requestParameters, answer, response.feedbackQuestionType);
			response.setResponseDetails(responseDetails);
		} else {
			response.responseMetaData = new Text("");
		}
		
		return response;
	}

}
