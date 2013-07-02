package teammates.common.datatransfer;

import teammates.common.util.Config;

/**
 * Represents submission statistics for the evaluation.
 * <br> Contains:
 * <br> * The total number of students who were expected to submit. 
 * <br> * The total number of students who submitted .
 */
public class FeedbackSessionStats {
		public int submittedTotal = Config.UNINITIALIZED_INT;
		public int expectedTotal = Config.UNINITIALIZED_INT;
}
