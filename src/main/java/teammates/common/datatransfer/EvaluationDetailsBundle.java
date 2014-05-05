package teammates.common.datatransfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.util.Const;

/**
 * Represents details of an evaluation. 
 * Contains:
 * <br> * The basic info of the evaluation (as a {@link EvaluationAttributes} object).
 * <br> * Submission statistics (as a {@link EvaluationStats} object).
 */
public class EvaluationDetailsBundle {

    public EvaluationStats stats;
    public EvaluationAttributes evaluation;

    public EvaluationDetailsBundle(EvaluationAttributes evaluation) {
        this.evaluation = evaluation;
        this.stats = new EvaluationStats();
    }

    /**
     * Sorts evaluations based courseID (ascending), then by deadline
     * (ascending), then by start time (ascending), then by evaluation name
     * (ascending) The sort by CourseID part is to cater the case when this
     * method is called with combined evaluations from many courses
     * 
     * @param evals
     */
    public static void sortEvaluationsByDeadline(List<EvaluationDetailsBundle> evals) {
        Collections.sort(evals, new Comparator<EvaluationDetailsBundle>() {
            public int compare(EvaluationDetailsBundle edd1, EvaluationDetailsBundle edd2) {
                EvaluationAttributes eval1 = edd1.evaluation;
                EvaluationAttributes eval2 = edd2.evaluation;
                int result = 0;
                if (result == 0) {
                    result = eval1.endTime.after(eval2.endTime) ? 1
                            : (eval1.endTime.before(eval2.endTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = eval1.startTime.after(eval2.startTime) ? 1
                            : (eval1.startTime.before(eval2.startTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = eval1.courseId.compareTo(eval2.courseId);
                }
                if (result == 0) {
                    result = eval1.name.compareTo(eval2.name);
                }
                return result;
            }
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("course:" + evaluation.courseId + ", name:" + evaluation.name
                + Const.EOL);
        sb.append("submitted/total: " + stats.submittedTotal + "/" + stats.expectedTotal);
        return sb.toString();
    }

}
