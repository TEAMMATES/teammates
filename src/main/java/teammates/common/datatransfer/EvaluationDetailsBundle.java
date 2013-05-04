package teammates.common.datatransfer;

import teammates.common.Common;

public class EvaluationDetailsBundle {

	public EvaluationData evaluation;

	public int submittedTotal = Common.UNINITIALIZED_INT;
	public int expectedTotal = Common.UNINITIALIZED_INT;


	public EvaluationDetailsBundle(EvaluationData evaluation) {
		this.evaluation = evaluation;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + evaluation.course + ", name:" + evaluation.name
				+ Common.EOL);
		//TODO: add statistics
		return sb.toString();
	}

}
