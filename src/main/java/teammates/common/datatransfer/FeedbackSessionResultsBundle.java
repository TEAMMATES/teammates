package teammates.common.datatransfer;

import java.util.List;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes} 
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle {
	public FeedbackSessionAttributes feedbackSession = null;
	public List<FeedbackQuestionAttributes> questions = null;
	public List<FeedbackResponseAttributes> responses = null;
	
	public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
			List<FeedbackQuestionAttributes> questions,
			List<FeedbackResponseAttributes> responses) {
		this.feedbackSession = feedbackSession;
		this.questions = questions;
		this.responses = responses;
	}
}

