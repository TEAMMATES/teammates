package teammates.ui.controller;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;
import teammates.storage.entity.FeedbackQuestion.QuestionType;

public class StudentFeedbackSubmitSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Config.PARAM_FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);

		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		StudentFeedbackSubmitPageData data = new StudentFeedbackSubmitPageData(account);

		// Get student email instead of account email.
		String studentEmail = logic.getStudentForGoogleId(courseId, account.googleId).email;
				
		data.bundle = logic.getFeedbackSessionQuestionsBundle(feedbackSessionName, courseId, studentEmail);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		if (data.bundle.feedbackSession.isOpened() == false) {
			throw new UnauthorizedAccessException(
					"This feedback session is not yet opened.");
		}
		
		int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();
		
		for(int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
			String totalResponsesForQuestion = getRequestParam(Config.PARAM_FEEDBACK_QUESTION_RESPONSETOTAL+"-"+questionIndx);
			if (totalResponsesForQuestion == null) {
				continue; // question has been skipped (not displayed).
			}
			int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);			
			for(int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++){
				FeedbackResponseAttributes response = extractFeedbackResponseData(questionIndx,responseIndx);
				response.giverEmail = studentEmail;
				if (response.getId() != null) {
					try {
						logic.updateFeedbackResponse(response);
					} catch (InvalidParametersException e) {
						statusToUser.add(e.getMessage());
						statusToAdmin = e.getMessage();
						isError = true;
					}
				} else if (response.answer.getValue().isEmpty() == false){
					try {
						logic.createFeedbackResponse(response);
					} catch (EntityAlreadyExistsException | InvalidParametersException e) {
						statusToUser.add(e.getMessage());
						statusToAdmin = e.getMessage();
						isError = true;
					}
				}
			}
		}
		
		if (isError == false) {
			statusToUser.add(Config.MESSAGE_FEEDBACK_RESPONSES_SAVED);
		}
		
		// TODO: what happens if qn is deleted as response is being submitted?
		// what happens if team/etc change such that receiver / response in general is invalid?
		return createRedirectResult(Config.PAGE_STUDENT_HOME);
	}
	
	private FeedbackResponseAttributes extractFeedbackResponseData(int questionIndx, int responseIndx) {
		
		FeedbackResponseAttributes response = new FeedbackResponseAttributes();

		response.setId(getRequestParam(Config.PARAM_FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
		response.feedbackSessionName = getRequestParam(Config.PARAM_FEEDBACK_SESSION_NAME);
		response.courseId = getRequestParam(Config.PARAM_COURSE_ID);
		response.feedbackQuestionId = getRequestParam(Config.PARAM_FEEDBACK_QUESTION_ID+"-"+questionIndx);
		response.feedbackQuestionType = QuestionType.valueOf(getRequestParam(Config.PARAM_FEEDBACK_QUESTION_TYPE+"-"+questionIndx));
		response.recipient = getRequestParam(Config.PARAM_FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
		response.answer = new Text(getRequestParam(Config.PARAM_FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx));
		return response;
	}

}
