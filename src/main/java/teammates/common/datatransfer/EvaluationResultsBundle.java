package teammates.common.datatransfer;

import java.util.TreeMap;

/**
 * Represents detailed results for an evaluation.
 * <br> Contains:
 * <br> * The basic {@link EvaluationAttributes} 
 * <br> * {@link java.util.TreeMap} of {@link TeamResultBundle} objects.
 */
public class EvaluationResultsBundle {
    public EvaluationAttributes evaluation;
    public TreeMap<String,TeamResultBundle> teamResults = new TreeMap<String,TeamResultBundle>();
    
    /**
     * For result of each student, sorts incoming submissions by p2p feedback text (ascending)
     * and sorts outgoing submissions by reviewee name followed by reviewee email (ascending).
     */
    public void sortForReportToInstructor() {
        for (TeamResultBundle teamResultBundle :teamResults.values()) {
            teamResultBundle.sortByStudentNameAscending();
            for (StudentResultBundle srb : teamResultBundle.studentResults) {
                srb.sortIncomingByFeedbackAscending();
                srb.sortOutgoingByStudentNameAscending();
            }
        }
        
    }
    
    
    
}
