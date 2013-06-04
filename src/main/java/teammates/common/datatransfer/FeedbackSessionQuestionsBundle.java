package teammates.common.datatransfer;

import java.util.List;

public class FeedbackSessionQuestionsBundle {
	public FeedbackSessionAttributes feedbackSession = null;
	public List<FeedbackQuestionAttributes> questions = null;;
	
	public FeedbackSessionQuestionsBundle(FeedbackSessionAttributes feedbackSession,
			List<FeedbackQuestionAttributes> questions) {
		this.feedbackSession = feedbackSession;
		this.questions = questions;
	}
}
