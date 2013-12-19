package teammates.common.datatransfer;

import java.util.List;

public class FeedbackMcqQuestionDetails extends FeedbackAbstractQuestionDetails {
	public int nChoices;
	public List<String> mcqChoices;
	public boolean otherEnabled;

	public FeedbackMcqQuestionDetails() {
		super(FeedbackQuestionType.MCQ);
	}

	public FeedbackMcqQuestionDetails(String questionText,
			int nChoices,
			List<String> mcqChoices,
			boolean otherEnabled) {
		super(FeedbackQuestionType.MCQ, questionText);
		
		this.nChoices = nChoices;
		this.mcqChoices = mcqChoices;
		this.otherEnabled = otherEnabled;
	}

}
