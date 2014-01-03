package teammates.common.datatransfer;

public class FeedbackMcqResponseDetails extends FeedbackAbstractResponseDetails {
	public String answer;
	public String otherFieldContent; //content of other field if "other" is selected as the answer
	
	public FeedbackMcqResponseDetails() {
		super(FeedbackQuestionType.MCQ);
		answer = "";
		otherFieldContent = "";
	}
	
	/** Creates a new FeedbackMcqResponseDetails object
	 * 
	 * @param answer The answer to the question or the content of other field if other is chosen
	 * @param isOther Whether or not other is chosen as the answer
	 */
	public FeedbackMcqResponseDetails(String answer, boolean isOther) {
		super(FeedbackQuestionType.MCQ);
		
		if(isOther){
			this.answer = "Other";
			this.otherFieldContent = answer;
		} else {
			this.answer = answer;
			this.otherFieldContent = "";
		}
	}

	@Override
	public String getAnswerString() {
		return answer;
	}
}
