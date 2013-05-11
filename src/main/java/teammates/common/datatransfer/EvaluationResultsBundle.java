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
	
}
