package teammates.common.datatransfer;

import teammates.common.Common;

public class EvaluationDetailsBundle {

	public EvaluationStats stats;
	public EvaluationData evaluation;

	public EvaluationDetailsBundle(EvaluationData evaluation) {
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
