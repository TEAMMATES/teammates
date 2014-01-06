package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teammates.common.datatransfer.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackQuestionAddAction extends Action {

	@Override
	protected ActionResult execute()  throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);
		
		FeedbackQuestionAttributes feedbackQuestion = extractFeedbackQuestionData();
		
		try {
			logic.createFeedbackQuestion(feedbackQuestion);	
			statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
			statusToAdmin = "Created Feedback Question for Feedback Session:<span class=\"bold\">(" +
					feedbackQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
					feedbackQuestion.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">" + feedbackQuestion.getQuestionDetails().getQuestionTypeDisplayName() + 
					":</span> " + feedbackQuestion.getQuestionDetails().questionText;
		} catch (EntityAlreadyExistsException e) {
			Assumption.fail("Creating a duplicate question should not be possible as GAE generates a new questionId every time\n");
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
	}

	private FeedbackQuestionAttributes extractFeedbackQuestionData() {
		//TODO Try to make this method stateless. i.e. pass input as a ParameterMap instead of
		//depending on the instance variable. Easier to test that way.
		FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();

		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
		Assumption.assertNotNull("Account trying to add feedback question is not an instructor of the course", instructorDetailForCourse);
		
		newQuestion.creatorEmail = instructorDetailForCourse.email; 
		newQuestion.courseId = courseId;
		newQuestion.feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		String feedbackQuestionGiverType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
		Assumption.assertNotNull("Null giver type", feedbackQuestionGiverType);
		newQuestion.giverType = FeedbackParticipantType.valueOf(feedbackQuestionGiverType);		
		
		String feedbackQuestionRecipientType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
		Assumption.assertNotNull("Null recipient type", feedbackQuestionRecipientType);
		newQuestion.recipientType = FeedbackParticipantType.valueOf(feedbackQuestionRecipientType);	
		
		String feedbackQuestionNumber = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
		Assumption.assertNotNull("Null question number", feedbackQuestionNumber);
		newQuestion.questionNumber = Integer.parseInt(feedbackQuestionNumber);
		
		newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;

		String numberOfEntityTypes = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);
		Assumption.assertNotNull("Null number of entity types", numberOfEntityTypes);
		if (numberOfEntityTypes.equals("custom")
			&& (newQuestion.recipientType == FeedbackParticipantType.STUDENTS 
				|| newQuestion.recipientType == FeedbackParticipantType.TEAMS)) {
			String numberOfEntities = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
			Assumption.assertNotNull("Null number of entities for custom entity number", numberOfEntities);

			newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(numberOfEntities);
		}
		
		newQuestion.showResponsesTo = getParticipantListFromParams(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
		String questionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
		Assumption.assertNotNull("Null question type", questionType);
		newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);
		
		String questionText = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
		Assumption.assertNotNull("Null question text", questionText);
		
		//TODO consider using inheritance instead of switch
		//i.e. type-specific code can be put in the corresponding question-type class
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
				
		return newQuestion;
	}

	private List<FeedbackParticipantType> getParticipantListFromParams(String participantListParam) {
		
		List<FeedbackParticipantType> participantList = new ArrayList<FeedbackParticipantType>();
		
		if(participantListParam == null || participantListParam.isEmpty()) {
			return participantList;
		}
		
		String[] splitString = participantListParam.split(",");
		
		for (String str : splitString) {
			participantList.add(FeedbackParticipantType.valueOf(str));
		}
		
		return participantList;
	}

}
