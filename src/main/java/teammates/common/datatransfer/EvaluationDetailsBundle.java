package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class EvaluationDetailsBundle {

	public EvaluationData evaluation;

	public int submittedTotal = Common.UNINITIALIZED_INT;
	public int expectedTotal = Common.UNINITIALIZED_INT;

	public ArrayList<TeamResultBundle> teams = new ArrayList<TeamResultBundle>();

	public EvaluationDetailsBundle(EvaluationData evaluation) {
		this.evaluation = evaluation;
	}

	public TeamResultBundle getTeamEvalResultBundle(String teamName) {
		for (TeamResultBundle teamResult : teams) {
			if (teamResult.team.name.equals(teamName)) {
				return teamResult;
			}
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + evaluation.course + ", name:" + evaluation.name
				+ Common.EOL);
		for (TeamResultBundle team : teams) {
			sb.append(team.toString(1));
		}
		return sb.toString();
	}

}
