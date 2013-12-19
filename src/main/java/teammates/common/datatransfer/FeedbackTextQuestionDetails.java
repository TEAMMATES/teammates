package teammates.common.datatransfer;

public class FeedbackTextQuestionDetails extends
		FeedbackAbstractQuestionDetails {
	//TODO use this instead of plain text for essay questions
	//will involve converting the existing database
	
	public FeedbackTextQuestionDetails() {
		super(FeedbackQuestionType.TEXT);
	}
	
	public FeedbackTextQuestionDetails(String questionText) {
		super(FeedbackQuestionType.TEXT, questionText);
	}
}
