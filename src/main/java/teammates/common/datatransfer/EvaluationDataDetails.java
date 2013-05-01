package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class EvaluationDataDetails {

	public EvaluationData evaluation;

	public int submittedTotal = Common.UNINITIALIZED_INT;
	public int expectedTotal = Common.UNINITIALIZED_INT;

	public ArrayList<TeamEvalResultBundle> teams = new ArrayList<TeamEvalResultBundle>();

	public EvaluationDataDetails(EvaluationData evaluation) {
		this.evaluation = evaluation;
	}

	public TeamEvalResultBundle getTeamEvalResultBundle(String teamName) {
		for (TeamEvalResultBundle teamResult : teams) {
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
		for (TeamEvalResultBundle team : teams) {
			sb.append(team.toString(1));
		}
		return sb.toString();
	}

}
