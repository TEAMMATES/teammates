package teammates.common.datatransfer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

/** A class holding the details for a specific question type.
+ * This abstract class is inherited by concrete Feedback*QuestionDetails
+ * classes which provides the implementation for the various abstract methods
+ * such that pages can render the correct information/forms depending on the 
+ * question type
+ */
public abstract class FeedbackAbstractQuestionDetails {
	public FeedbackQuestionType questionType;
	public String questionText;

	protected FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType){
		this.questionType = questionType;
	}
	
	protected FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType,
			String questionText) {
		this.questionType = questionType;
		this.questionText = questionText;
	}
		
	public abstract String getQuestionTypeDisplayName();
	
	public abstract String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
			int qnIdx, int responseIdx,
			FeedbackAbstractResponseDetails existingResponseDetails,
			FeedbackAbstractQuestionDetails questionDetails);
	
	public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
			int qnIdx, int responseIdx,
			FeedbackAbstractQuestionDetails questionDetails);
	
	public abstract String getQuestionSpecificEditFormHtml(int questionNumber);
	
	public abstract boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails);
	
	public static FeedbackAbstractQuestionDetails createQuestionDetails(Map<String, String[]> requestParameters, FeedbackQuestionType questionType) {
		String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
		Assumption.assertNotNull("Null question text", questionText);
		
		FeedbackAbstractQuestionDetails questionDetails = null;
		
		switch(questionType){
		case TEXT:
			questionDetails = new FeedbackTextQuestionDetails(questionText);
			break;
		case MCQ:
			String numMcqChoicesCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
			Assumption.assertNotNull("Null number of choice for MCQ", numMcqChoicesCreatedString);
			int numMcqChoicesCreated = Integer.parseInt(numMcqChoicesCreatedString);
			
			int numOfMcqChoices = 0;
			List<String> mcqChoices = new LinkedList<String>();
			for(int i = 0; i < numMcqChoicesCreated; i++) {
				String mcqChoice = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i);
				if(mcqChoice != null && !mcqChoice.trim().isEmpty()) {
					mcqChoices.add(mcqChoice);
					numOfMcqChoices++;
				}
			}
			
			boolean mcqOtherEnabled = false; // TODO change this when implementing "other, please specify" field
			
			questionDetails = new FeedbackMcqQuestionDetails(questionText, numOfMcqChoices, mcqChoices, mcqOtherEnabled);
			break;
		case MSQ:
			String numMsqChoicesCreatedString = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED);
			Assumption.assertNotNull("Null number of choice for MSQ", numMsqChoicesCreatedString);
			int numMsqChoicesCreated = Integer.parseInt(numMsqChoicesCreatedString);
			
			int numOfMsqChoices = 0;
			List<String> msqChoices = new LinkedList<String>();
			for(int i = 0; i < numMsqChoicesCreated; i++) {
				String msqChoice = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-" + i);
				if(msqChoice != null && !msqChoice.trim().isEmpty()) {
					msqChoices.add(msqChoice);
					numOfMsqChoices++;
				}
			}
			
			boolean msqOtherEnabled = false; // TODO change this when implementing "other, please specify" field
			
			questionDetails = new FeedbackMsqQuestionDetails(questionText, numOfMsqChoices, msqChoices, msqOtherEnabled);
			break;
		default:
			Assumption.fail("Question type not supported");
			break;
		}
		
		return questionDetails;
	}
}