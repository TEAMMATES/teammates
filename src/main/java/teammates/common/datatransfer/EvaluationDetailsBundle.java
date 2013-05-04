package teammates.common.datatransfer;

import teammates.common.Common;

public class EvaluationDetailsBundle {

	public EvaluationStats stats;
	public EvaluationAttributes evaluation;

	public EvaluationDetailsBundle(EvaluationAttributes evaluation) {
		this.evaluation = evaluation;
		this.stats = new EvaluationStats();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + evaluation.course + ", name:" + evaluation.name
				+ Common.EOL);
		//TODO: add statistics
		return sb.toString();
	}

}
