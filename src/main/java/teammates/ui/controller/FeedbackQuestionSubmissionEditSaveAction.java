package teammates.ui.controller;

import java.util.Map;

import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionBundle;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

import com.google.appengine.api.datastore.Text;

public abstract class FeedbackQuestionSubmissionEditSaveAction extends Action {
	protected String courseId;
	protected String feedbackSessionName;
	protected String feedbackQuestionId;
	protected FeedbackQuestionSubmissionEditPageData data;
	
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull(feedbackSessionName);
		feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
		Assumption.assertNotNull(feedbackQuestionId);
		
		verifyAccesibleForSpecificUser();
		
		setStatusToAdmin();
		
		FeedbackSessionAttributes fs = logic.getFeedbackSession(feedbackSessionName, courseId);
		if (isSessionOpenForSpecificUser(fs) == false) {
			throw new UnauthorizedAccessException("This feedback session is not currently open for submission.");
		}
		
		String userEmailForCourse = getUserEmailForCourse();
		
		String totalResponsesForQuestion = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL);
		int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);
		for(int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++){
			FeedbackResponseAttributes response = extractFeedbackResponseData(requestParameters, 1, responseIndx);
			response.giverEmail = userEmailForCourse;
			
			saveResponse(response);
		}
		
		data = new FeedbackQuestionSubmissionEditPageData(account);
		data.bundle = getDataBundle(userEmailForCourse);
		
		if (isError == false) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
		}
		
		data.isSessionOpenForSubmission = isSessionOpenForSpecificUser(fs);
		if (!data.isSessionOpenForSubmission) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN);
		}
		
		return createSpecificShowPageResult();
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
		
		//This field can be null if the response is new
		response.setId(HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
				
		response.feedbackSessionName = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull("Null feedback session name", response.feedbackSessionName);
		
		response.courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull("Null feedback courseId", response.courseId);
		
		response.feedbackQuestionId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_ID);
		Assumption.assertNotNull("Null feedbackQuestionId", response.feedbackQuestionId);
		
		response.recipientEmail = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
		Assumption.assertNotNull("Null feedback recipientEmail", response.recipientEmail);
		
		String feedbackQuestionType = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
		Assumption.assertNotNull("Null feedbackQuestionType", feedbackQuestionType);
		response.feedbackQuestionType = FeedbackQuestionType.valueOf(feedbackQuestionType);
		
		//This field can be null if the question is skipped
		String[] answer = HttpRequestHelper.getValuesFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx);
		
		if(answer != null && !answer[0].trim().isEmpty()) {
			FeedbackAbstractResponseDetails responseDetails = 
					FeedbackAbstractResponseDetails.createResponseDetails(
							requestParameters, answer,
							response.feedbackQuestionType,
							questionIndx, responseIndx);
			response.setResponseDetails(responseDetails);
		} else {
			response.responseMetaData = new Text("");
		}
		
		return response;
	}

	protected abstract void verifyAccesibleForSpecificUser();
	
	protected abstract String getUserEmailForCourse();

	protected abstract FeedbackQuestionBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;
	
	protected abstract void setStatusToAdmin();
	
	protected abstract boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs);
		
	protected abstract ShowPageResult createSpecificShowPageResult();
}
