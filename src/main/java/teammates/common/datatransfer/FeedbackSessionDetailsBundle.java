package teammates.common.datatransfer;

import teammates.common.Common;

/**
 * Represents details of an evaluation. 
 * Contains:
 * <br> * The basic info of the feedback session (as a {@link FeedbackSessionAttributes} object).
 * <br> * Feedback response statistics (as a {@link FeedbackSessionStats} object).
 */
public class FeedbackSessionDetailsBundle {

	public FeedbackSessionStats stats;
	public FeedbackSessionAttributes feedbackSession;

	public FeedbackSessionDetailsBundle(FeedbackSessionAttributes feedbackSession) {
		this.feedbackSession = feedbackSession;
		this.stats = new FeedbackSessionStats();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + feedbackSession.courseId + ", name:" + feedbackSession.feedbackSessionName
				+ Common.EOL);
		sb.append("submitted/total: " + stats.submittedTotal + "/" + stats.expectedTotal);
		return sb.toString();
	}

}