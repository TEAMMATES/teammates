package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;
import teammates.storage.entity.FeedbackQuestion.QuestionType;

public class InstructorFeedbackQuestionEditAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorUsingOwnIdOrAbove(account.googleId);

		String editType = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_EDITTYPE);
		
		FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData();
		
		try {
			if(editType.equals("edit")) {
				logic.updateFeedbackQuestion(updatedQuestion);	
				statusToUser.add(Common.MESSAGE_FEEDBACK_QUESTION_EDITED);
				statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
						updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
						updatedQuestion.courseId + "]</span> edited.<br>" +
						"<span class=\"bold\">Feedback Question Text:</span> " + updatedQuestion.questionText;
			} else if (editType.equals("delete")) {
				logic.deleteFeedbackQuestion(updatedQuestion.feedbackSessionName, updatedQuestion.courseId, updatedQuestion.questionNumber);
				statusToUser.add(Common.MESSAGE_FEEDBACK_QUESTION_DELETED);
				statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
						updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
						updatedQuestion.courseId + "]</span> deleted.<br>" +
						"<span class=\"bold\">Feedback Question Text:</span> " + updatedQuestion.questionText;
			}
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
	}

	private FeedbackQuestionAttributes extractFeedbackQuestionData() {
		FeedbackQuestionAttributes newQuestion =
				new FeedbackQuestionAttributes();
		
		newQuestion.setId(getRequestParam(Common.PARAM_FEEDBACK_QUESTION_ID));
		
		newQuestion.courseId = getRequestParam(Common.PARAM_COURSE_ID);
		newQuestion.feedbackSessionName = getRequestParam(Common.PARAM_FEEDBACK_SESSION_NAME);
		
		String param;
		if((param = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_GIVERTYPE)) != null) {
			newQuestion.giverType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE)) != null){
			newQuestion.recipientType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_NUMBER)) != null){
			newQuestion.questionNumber = Integer.parseInt(param);
		}
		newQuestion.questionText = 
				new Text(getRequestParam(Common.PARAM_FEEDBACK_QUESTION_TEXT));
		newQuestion.questionType = 
				QuestionType.TEXT;
		if((param = getRequestParam(Common.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES)) != null){
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(param);
		}
		newQuestion.showResponsesTo = getParticipantListFromParams(
				getRequestParam(Common.PARAM_FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				getRequestParam(Common.PARAM_FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				getRequestParam(Common.PARAM_FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
		return newQuestion;
	}

	private List<FeedbackParticipantType> getParticipantListFromParams(String params) {
		
		List<FeedbackParticipantType> list = new ArrayList<FeedbackParticipantType>();
		
		if(params.isEmpty())
			return list;
		
		String[] splitString = params.split(",");
		
		for (String str : splitString) {
			list.add(FeedbackParticipantType.valueOf(str));
		}
		
		return list;
	}
}
