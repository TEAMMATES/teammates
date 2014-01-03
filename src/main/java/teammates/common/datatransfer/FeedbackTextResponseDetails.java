package teammates.common.datatransfer;

public class FeedbackTextResponseDetails extends
		FeedbackAbstractResponseDetails {
	//TODO use this instead of plain text for essay questions
	//will involve converting the existing database
	
	public String answer;
	
	public FeedbackTextResponseDetails(){
		super(FeedbackQuestionType.TEXT);
		this.answer = "";
	}
	
	public FeedbackTextResponseDetails(String answer) {
		super(FeedbackQuestionType.TEXT);
		this.answer = answer;
	}

	@Override
	public String getAnswerString() {
		return answer;
	}

}
