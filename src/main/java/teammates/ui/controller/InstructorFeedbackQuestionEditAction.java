package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;
import teammates.storage.entity.FeedbackQuestion.QuestionType;

public class InstructorFeedbackQuestionEditAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		
		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Constants.PARAM_FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));

		String editType = getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_EDITTYPE);
		
		FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData();
		
		try {
			if(editType.equals("edit")) {
				logic.updateFeedbackQuestion(updatedQuestion);	
				statusToUser.add(Constants.STATUS_FEEDBACK_QUESTION_EDITED);
				statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
						updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
						updatedQuestion.courseId + "]</span> edited.<br>" +
						"<span class=\"bold\">Feedback Question Text:</span> " + updatedQuestion.questionText;
			} else if (editType.equals("delete")) {
				logic.deleteFeedbackQuestion(updatedQuestion.feedbackSessionName, updatedQuestion.courseId, updatedQuestion.questionNumber);
				statusToUser.add(Constants.STATUS_FEEDBACK_QUESTION_DELETED);
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

		
		return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
	}

	private FeedbackQuestionAttributes extractFeedbackQuestionData() {
		FeedbackQuestionAttributes newQuestion =
				new FeedbackQuestionAttributes();
		
		newQuestion.setId(getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_ID));
		
		newQuestion.courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		newQuestion.feedbackSessionName = getRequestParam(Constants.PARAM_FEEDBACK_SESSION_NAME);
		
		String param;
		if((param = getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_GIVERTYPE)) != null) {
			newQuestion.giverType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_RECIPIENTTYPE)) != null){
			newQuestion.recipientType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_NUMBER)) != null){
			newQuestion.questionNumber = Integer.parseInt(param);
		}
		newQuestion.questionText = new Text(getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_TEXT));
		newQuestion.questionType = QuestionType.TEXT;
		
		newQuestion.numberOfEntitiesToGiveFeedbackTo = Constants.MAX_POSSIBLE_RECIPIENTS;
		if ((param = getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE)) != null) {
			if (param.equals("custom")) {
				if ((param = getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_NUMBEROFENTITIES)) != null) {
					if (newQuestion.recipientType == FeedbackParticipantType.STUDENTS ||
						newQuestion.recipientType == FeedbackParticipantType.TEAMS) {
						newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(param);
					}
				}
			}
		}
		
		newQuestion.showResponsesTo = getParticipantListFromParams(
				getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				getRequestParam(Constants.PARAM_FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
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
