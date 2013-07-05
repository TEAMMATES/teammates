package teammates.ui.controller;

import com.google.appengine.api.datastore.Text;

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

		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);

		new GateKeeper().verifyAccessible(
				logic.getStudentForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		StudentFeedbackSubmissionEditPageData data = new StudentFeedbackSubmissionEditPageData(account);

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
			String totalResponsesForQuestion = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL+"-"+questionIndx);
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
			statusToUser.add(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
		}
		
		// TODO: what happens if qn is deleted as response is being submitted?
		// what happens if team/etc change such that receiver / response in general is invalid?
		return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
	}
	
	private FeedbackResponseAttributes extractFeedbackResponseData(int questionIndx, int responseIndx) {
		
		FeedbackResponseAttributes response = new FeedbackResponseAttributes();

		response.setId(getRequestParam(Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-"+questionIndx+"-"+responseIndx));
		response.feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		response.courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		response.feedbackQuestionId = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_ID+"-"+questionIndx);
		response.feedbackQuestionType = FeedbackQuestionType.valueOf(getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_TYPE+"-"+questionIndx));
		response.recipient = getRequestParam(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-"+questionIndx+"-"+responseIndx);
		response.answer = new Text(getRequestParam(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-"+questionIndx+"-"+responseIndx));
		return response;
	}

}
