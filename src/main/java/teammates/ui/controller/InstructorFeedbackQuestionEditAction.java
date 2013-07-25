package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionEditAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		//TODO: Looks like this class is handling multiple actions. Break up?
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);

		String editType = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE);
		
		FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData();
		
		try {
			if(editType.equals("edit")) {
				logic.updateFeedbackQuestion(updatedQuestion);	
				statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
				statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
						updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
						updatedQuestion.courseId + "]</span> edited.<br>" +
						"<span class=\"bold\">Feedback Question Text:</span> " + updatedQuestion.questionText;
			} else if (editType.equals("delete")) {
				logic.deleteFeedbackQuestion(updatedQuestion.feedbackSessionName, updatedQuestion.courseId, updatedQuestion.questionNumber);
				statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
				statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
						updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
						updatedQuestion.courseId + "]</span> deleted.<br>" +
						"<span class=\"bold\">Feedback Question Text:</span> " + updatedQuestion.questionText;
			}
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}

		
		return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
	}

	private FeedbackQuestionAttributes extractFeedbackQuestionData() {
		FeedbackQuestionAttributes newQuestion =
				new FeedbackQuestionAttributes();
		
		newQuestion.setId(getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_ID));
		
		newQuestion.courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		newQuestion.feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		String param;
		if((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE)) != null) {
			newQuestion.giverType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE)) != null){
			newQuestion.recipientType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER)) != null){
			newQuestion.questionNumber = Integer.parseInt(param);
		}
		newQuestion.questionText = new Text(getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_TEXT));
		newQuestion.questionType = FeedbackQuestionType.TEXT;
		
		if (numberOfEntitiesIsUserDefined(newQuestion.recipientType)) {
			param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(param);
		} else {
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
		}
		
		newQuestion.showResponsesTo = getParticipantListFromParams(
				getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
		return newQuestion;
	}
	
	private boolean numberOfEntitiesIsUserDefined(FeedbackParticipantType recipientType) {
		if (recipientType != FeedbackParticipantType.STUDENTS &&
				recipientType != FeedbackParticipantType.TEAMS) {
			return false;
		}
		
		String param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);
		if (param == null || param.equals("custom") == false) {
			return false;
		}
		
		param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);		
		if (param == null) {
			return false;
		}
		
		return true;
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
