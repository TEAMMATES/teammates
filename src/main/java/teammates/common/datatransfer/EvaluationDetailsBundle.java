package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class EvaluationDetailsBundle {

	public EvaluationData evaluation;

	public int submittedTotal = Common.UNINITIALIZED_INT;
	public int expectedTotal = Common.UNINITIALIZED_INT;

	public ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();

	public EvaluationDetailsBundle(EvaluationData evaluation) {
		this.evaluation = evaluation;
	}

	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + evaluation.course + ", name:" + evaluation.name
				+ Common.EOL);
		//TODO: add student details here
		return sb.toString();
	}

}
