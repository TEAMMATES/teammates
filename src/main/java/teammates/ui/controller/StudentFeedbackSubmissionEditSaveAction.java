package teammates.ui.controller;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);

		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		StudentFeedbackSubmissionEditPageData data = new StudentFeedbackSubmissionEditPageData(account);
		
		statusToAdmin = "Show student feedback edit result page<br>" +
				"Session Name: " + feedbackSessionName + "<br>" + 
				"Course ID: " + courseId;
		// Get student email instead of account email.
		String studentEmail = logic.getStudentForGoogleId(courseId, account.googleId).email;

		data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(feedbackSessionName, courseId, studentEmail);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		if (data.bundle.feedbackSession.isOpened() == false && data.bundle.feedbackSession.isInGracePeriod() == false) {
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
				FeedbackResponseAttributes response = extractFeedbackResponseData(questionIndx,responseIndx);
				response.giverEmail = studentEmail;
				saveResponse(response);
			}
		}
		
		if (isError == false) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
		}
		
		// TODO: what happens if qn is deleted as response is being submitted?
		// what happens if team/etc change such that receiver / response in general is invalid?
		return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
	}
	
	private void saveResponse(FeedbackResponseAttributes response)
			throws EntityDoesNotExistException {
		if (response.getId() != null) {
			// Delete away response if any empty fields
			if (response.answer.getValue().isEmpty() || 
				response.recipientEmail.isEmpty()) {
				logic.deleteFeedbackResponse(response);
				return;
			}
			try {
				logic.updateFeedbackResponse(response);
			} catch (EntityAlreadyExistsException | InvalidParametersException e) {
				setStatusForException(e);
			}
		} else if (!response.answer.getValue().isEmpty() &&
					!response.recipientEmail.isEmpty()){
			try {
				logic.createFeedbackResponse(response);
			} catch (EntityAlreadyExistsException | InvalidParametersException e) {
				setStatusForException(e);
			}
		}
	}
	
	private FeedbackResponseAttributes extractFeedbackResponseData(int questionIndx, int responseIndx) {
		//TODO Assert parameter values are not null as they're retrieved
		FeedbackResponseAttributes response = new FeedbackResponseAttributes();

		response.setId(getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
		response.feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		response.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		response.feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID+"-"+questionIndx);
		response.recipientEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
		
		response.feedbackQuestionType = FeedbackQuestionType.valueOf(getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TYPE+"-"+questionIndx));
		String answer = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx);
		
		switch(response.feedbackQuestionType) {
		case TEXT:
			//For essay questions the response is saved as plain-text due to legacy format before there were multiple question types
			response.answer = new Text(answer);
			break;
		case MCQ:
			//TODO check whether other is chosen and construct accordingly when implementing other field
			FeedbackMcqResponseDetails mcqResponseDetails = new FeedbackMcqResponseDetails(answer, false);
			if (answer != null) {
				response.storeQuestionDetails(mcqResponseDetails);
			} else {  
				//question was skipped
				response.answer = new Text(new String());
			}
			break;
		default:
			Assumption.fail("Question type not supported");
			break;
		}
		
		return response;
	}

}
