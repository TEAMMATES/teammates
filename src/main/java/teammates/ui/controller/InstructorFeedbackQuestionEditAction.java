package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackMcqQuestionDetails;
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
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);

		String editType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE);
		
		FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData();
		
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

	private FeedbackQuestionAttributes extractFeedbackQuestionData() {
		//TODO Try to make this method stateless. i.e. pass input as a ParameterMap instead of
		//depending on the instance variable. Easier to test that way.
		FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();
		
		String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
		Assumption.assertNotNull(questionId);
		newQuestion.setId(questionId);
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		newQuestion.courseId = courseId;
		
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull(feedbackSessionName);
		newQuestion.feedbackSessionName = feedbackSessionName;
		
		//TODO thoroughly investigate when and why these parameters can be null
		//and check all possibilities in the tests
		//should only be null when deleting. might be good to separate the delete action from this class
		
		//Can be null
		String giverType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
		if(giverType != null) {
			newQuestion.giverType = FeedbackParticipantType.valueOf(giverType);
		}
		
		//Can be null
		String recipientType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
		if(recipientType != null) {
			newQuestion.recipientType = FeedbackParticipantType.valueOf(recipientType);
		}

		String questionNumber = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
		Assumption.assertNotNull(questionNumber);
		newQuestion.questionNumber = Integer.parseInt(questionNumber);
				
		if (numberOfEntitiesIsUserDefined(newQuestion.recipientType)) {
			String nEntities;
			nEntities = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
			Assumption.assertNotNull(nEntities);
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(nEntities);
		} else {
			newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
		}
		
		newQuestion.showResponsesTo = getParticipantListFromParams(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
		String questionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
		Assumption.assertNotNull(questionType);
		newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);
		
		//Can be null
		String questionText = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
		if (questionText != null) {
			switch(newQuestion.questionType){
			case TEXT:
				newQuestion.questionMetaData = new Text(questionText);
				break;
			case MCQ:
				String numberOfChoicesCreatedString = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
				Assumption.assertNotNull("Null number of choice for MCQ", numberOfChoicesCreatedString);
				int numberOfChoicesCreated = Integer.parseInt(numberOfChoicesCreatedString);
				
				int numOfMcqChoices = 0;
				List<String> mcqChoices = new LinkedList<String>();
				for(int i = 0; i < numberOfChoicesCreated; i++) {
					String mcqChoice = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i);
					if(mcqChoice != null && !mcqChoice.trim().isEmpty()) {
						mcqChoices.add(mcqChoice);
						numOfMcqChoices++;
					}
				}
				
				boolean otherEnabled = false; // TODO change this when implementing "other, please specify" field
				
				FeedbackMcqQuestionDetails mcqDetails = 
						new FeedbackMcqQuestionDetails(questionText, numOfMcqChoices, mcqChoices, otherEnabled);
				newQuestion.setQuestionDetails(mcqDetails);
				break;
			default:
				Assumption.fail("Question type not supported");
				break;	
			}
		}
				
		return newQuestion;
	}
	
	private boolean numberOfEntitiesIsUserDefined(FeedbackParticipantType recipientType) {
		if (recipientType != FeedbackParticipantType.STUDENTS &&
				recipientType != FeedbackParticipantType.TEAMS) {
			return false;
		}
		
		String nEntityTypes = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);
		Assumption.assertNotNull(nEntityTypes);
		if (nEntityTypes.equals("custom") == false) {
			return false;
		}
		
		return true;
	}

	private List<FeedbackParticipantType> getParticipantListFromParams(String params) {
		
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
