package teammates.common.datatransfer;

import java.util.TreeMap;

public class EvaluationResultsBundle {
	public EvaluationData evaluation;
	public TreeMap<String,TeamResultBundle> teamResults = new TreeMap<String,TeamResultBundle>();
	
}
