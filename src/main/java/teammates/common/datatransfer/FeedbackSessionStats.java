package teammates.common.datatransfer;

import teammates.common.Common;

/**
 * Represents submission statistics for the evaluation.
 * <br> Contains:
 * <br> * The total number of students who were expected to submit. 
 * <br> * The total number of students who submitted .
 */
public class FeedbackSessionStats {
		public int submittedTotal = Common.UNINITIALIZED_INT;
		public int expectedTotal = Common.UNINITIALIZED_INT;
}
