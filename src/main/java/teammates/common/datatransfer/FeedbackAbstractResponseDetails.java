package teammates.common.datatransfer;

public abstract class FeedbackAbstractResponseDetails {
	public FeedbackQuestionType questionType;
	
	public FeedbackAbstractResponseDetails(FeedbackQuestionType questionType){
		this.questionType = questionType;
	}
	
	public abstract String getAnswerString();
}
