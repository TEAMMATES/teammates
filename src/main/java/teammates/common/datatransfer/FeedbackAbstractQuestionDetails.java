package teammates.common.datatransfer;

/** A class holding the details for a specific question type.
+ * This abstract class is inherited by concrete Feedback*QuestionDetails
+ * classes which provides the implementation for the various abstract methods
+ * such that pages can render the correct information/forms depending on the 
+ * question type
+ */
public abstract class FeedbackAbstractQuestionDetails {
	public FeedbackQuestionType questionType;
	public String questionText;

	public FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType){
		this.questionType = questionType;
	}
	
	public FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType,
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
}