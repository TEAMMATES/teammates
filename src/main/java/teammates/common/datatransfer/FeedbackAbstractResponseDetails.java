package teammates.common.datatransfer;

/** A class holding the details for the response of a specific question type.
+ * This abstract class is inherited by concrete Feedback*ResponseDetails
+ * classes which provides the implementation for the various abstract methods
+ * such that pages can render the correct information depending on the 
+ * question type.
+ */
public abstract class FeedbackAbstractResponseDetails {
	public FeedbackQuestionType questionType;
	
	public FeedbackAbstractResponseDetails(FeedbackQuestionType questionType){
		this.questionType = questionType;
	}
	
	public abstract String getAnswerString();
}
