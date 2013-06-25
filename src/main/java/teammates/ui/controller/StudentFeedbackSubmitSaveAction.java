package teammates.ui.controller;

import com.google.appengine.api.datastore.Text;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;
import teammates.storage.entity.FeedbackQuestion.QuestionType;

public class StudentFeedbackSubmitSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);

		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		//TODO: ensure the session is OPEN. Access control does not ensure it.

		StudentFeedbackSubmitPageData data = new StudentFeedbackSubmitPageData(account);

		// Get student email instead of account email.
		String studentEmail = logic.getStudentForGoogleId(courseId, account.googleId).email;
				
		data.bundle = logic.getFeedbackSessionQuestionsBundle(feedbackSessionName, courseId, studentEmail);
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();
		
		for(int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
			String totalResponsesForQuestion = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_RESPONSETOTAL+"-"+questionIndx);
			if (totalResponsesForQuestion == null) {
				continue; // question has been skipped (not displayed).
			}
			int numOfResponsesToGet = Integer.parseInt(totalResponsesForQuestion);			
			for(int responseIndx = 0; responseIndx < numOfResponsesToGet; responseIndx++){
				FeedbackResponseAttributes response = extractFeedbackResponseData(questionIndx,responseIndx); 
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
			statusToUser.add("All responses submitted succesfully!");
		}
		
		// TODO: what happens if qn is deleted as response is being submitted?
		// what happens if team/etc change such that receiver / response in general is invalid?
		
		return createRedirectResult(new PageData(account).getStudentFeedbackResponseEditLink(courseId,feedbackSessionName));
	}
	
	private FeedbackResponseAttributes extractFeedbackResponseData(int questionIndx, int responseIndx) {
		
		FeedbackResponseAttributes response = new FeedbackResponseAttributes();

		response.setId(getRequestParam(Common.PARAM_FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
		response.feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		response.courseId = getRequestParam(Common.PARAM_COURSE_ID);
		response.feedbackQuestionId = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_ID+"-"+questionIndx);
		response.feedbackQuestionType = QuestionType.valueOf(getRequestParam(Common.PARAM_FEEDBACK_QUESTION_TYPE+"-"+questionIndx));
		response.giverEmail = account.email;
		response.recipient = getRequestParam(Common.PARAM_FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
		response.answer = new Text(getRequestParam(Common.PARAM_FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx));
		return response;
	}

}
