package teammates.common.datatransfer;

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