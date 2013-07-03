package teammates.common.datatransfer;

import teammates.common.util.Constants;

/**
 * Represents submission statistics for the evaluation.
 * <br> Contains:
 * <br> * The total number of students who were expected to submit. 
 * <br> * The total number of students who submitted .
 */
public class FeedbackSessionStats {
		public int submittedTotal = Constants.INT_UNINITIALIZED;
		public int expectedTotal = Constants.INT_UNINITIALIZED;
}
