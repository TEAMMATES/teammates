package teammates.ui.controller;

import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

public abstract class FeedbackSubmissionEditSaveAction extends Action {
	protected String courseId;
	protected String feedbackSessionName;
	protected FeedbackSubmissionEditPageData data;
	
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		verifyAccesibleForSpecificUser();
		
		String userEmailForCourse = getUserEmailForCourse();
		data = new FeedbackSubmissionEditPageData(account);
		data.bundle = getDataBundle(userEmailForCourse);		
		Assumption.assertNotNull("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".", data.bundle);
		
		setStatusToAdmin();
		
		if (isSessionOpenForSpecificUser() == false) {
			throw new UnauthorizedAccessException("This feedback session is not open for submission.");
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
				response.giverEmail = userEmailForCourse;
				saveResponse(response);
			}
		}
		
		if (isError == false) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
		}
		
		// TODO: what happens if qn is deleted as response is being submitted?
		// what happens if team/etc change such that receiver / response in general is invalid?
		return createSpecificRedirectResult();
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
		
		response.feedbackQuestionId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_ID+"-"+questionIndx);
		Assumption.assertNotNull("Null feedbackQuestionId", response.feedbackQuestionId);
		
		response.recipientEmail = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
		Assumption.assertNotNull("Null feedback recipientEmail", response.recipientEmail);
		
		String feedbackQuestionType = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TYPE+"-"+questionIndx);
		Assumption.assertNotNull("Null feedbackQuestionType", feedbackQuestionType);
		response.feedbackQuestionType = FeedbackQuestionType.valueOf(feedbackQuestionType);
		
		//This field can be null if the question is skipped
		String[] answer = HttpRequestHelper.getValuesFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx);
		
		if(answer != null && !answer[0].trim().isEmpty()) {
			FeedbackAbstractResponseDetails responseDetails = 
					FeedbackAbstractResponseDetails.createResponseDetails(requestParameters, answer, response.feedbackQuestionType);
			response.setResponseDetails(responseDetails);
		} else {
			response.responseMetaData = new Text("");
		}
		
		return response;
	}
	
	protected abstract void verifyAccesibleForSpecificUser();

	protected abstract String getUserEmailForCourse();

	protected abstract FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException;

	protected abstract void setStatusToAdmin();

	protected abstract boolean isSessionOpenForSpecificUser();

	protected abstract RedirectResult createSpecificRedirectResult();
}
