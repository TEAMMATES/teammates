package teammates.common.datatransfer;

import teammates.common.util.Const;

/**
 * Represents submission statistics for the evaluation.
 * <br> Contains:
 * <br> * The total number of students who were expected to submit. 
 * <br> * The total number of students who submitted .
 */
public class EvaluationStats {
    public int submittedTotal = Const.INT_UNINITIALIZED;
    public int expectedTotal = Const.INT_UNINITIALIZED;
}