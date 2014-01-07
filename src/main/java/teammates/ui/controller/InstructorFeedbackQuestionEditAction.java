package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackAbstractQuestionDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionEditAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		//TODO: Looks like this class is handling multiple actions. Break up?
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);

		String editType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE);
		
		FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData(requestParameters);
		
		try {
			if(editType.equals("edit")) {
				if(updatedQuestion.questionNumber != 0){ //Question number was updated
					logic.updateFeedbackQuestionNumber(updatedQuestion);
					statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
				} else{
					logic.updateFeedbackQuestion(updatedQuestion);	
					statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
					statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
							updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
							updatedQuestion.courseId + "]</span> edited.<br>" +
							"<span class=\"bold\">" + updatedQuestion.getQuestionDetails().getQuestionTypeDisplayName() + ":</span> " +
							updatedQuestion.getQuestionDetails().questionText;
				}
			} else if (editType.equals("delete")) {
				logic.deleteFeedbackQuestion(updatedQuestion.getId());
				statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
				statusToAdmin = "Feedback Question "+ updatedQuestion.questionNumber +" for session:<span class=\"bold\">(" +
						updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
						updatedQuestion.courseId + "]</span> deleted.<br>";
			}
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
	}

	private static FeedbackQuestionAttributes extractFeedbackQuestionData(Map<String, String[]> requestParameters) {
		FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();
		
		newQuestion.setId(HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_ID));
		Assumption.assertNotNull("Null question id", newQuestion.getId());
		
		newQuestion.courseId = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull("Null course id", newQuestion.courseId);
		
		newQuestion.feedbackSessionName = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull("Null feedback session name", newQuestion.feedbackSessionName);
		
		//TODO thoroughly investigate when and why these parameters can be null
		//and check all possibilities in the tests
		//should only be null when deleting. might be good to separate the delete action from this class
		
		//Can be null
		String giverType = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
		if(giverType != null) {
			newQuestion.giverType = FeedbackParticipantType.valueOf(giverType);
		}
		
		//Can be null
		String recipientType = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
		if(recipientType != null) {
			newQuestion.recipientType = FeedbackParticipantType.valueOf(recipientType);
		}

		String questionNumber = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
		Assumption.assertNotNull("Null question number", questionNumber);
		newQuestion.questionNumber = Integer.parseInt(questionNumber);
		
		// Can be null
		String nEntityTypes = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);
		if (numberOfEntitiesIsUserDefined(newQuestion.recipientType, nEntityTypes)) {
			String nEntities;
			nEntities = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
			Assumption.assertNotNull(nEntities);
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(nEntities);
		} else {
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
		}
		
		newQuestion.showResponsesTo = getParticipantListFromParams(
				HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
		String questionType = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
		Assumption.assertNotNull(questionType);
		newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);
		
		//Can be null
		String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
		if (questionText != null) {
			FeedbackAbstractQuestionDetails questionDetails = 
					FeedbackAbstractQuestionDetails.createQuestionDetails(requestParameters, newQuestion.questionType);
			newQuestion.setQuestionDetails(questionDetails);
		}
				
		return newQuestion;
	}
	
	private static boolean numberOfEntitiesIsUserDefined(FeedbackParticipantType recipientType, String nEntityTypes) {
		if (recipientType != FeedbackParticipantType.STUDENTS &&
				recipientType != FeedbackParticipantType.TEAMS) {
			return false;
		}
		
		if (nEntityTypes.equals("custom") == false) {
			return false;
		}
		
		return true;
	}

	private static List<FeedbackParticipantType> getParticipantListFromParams(String params) {
		
		List<FeedbackParticipantType> list = new ArrayList<FeedbackParticipantType>();
		
		if(params.isEmpty()) {
			return list;
		}	
		
		String[] splitString = params.split(",");
		
		for (String str : splitString) {
			list.add(FeedbackParticipantType.valueOf(str));
		}
		
		return list;
	}
}
