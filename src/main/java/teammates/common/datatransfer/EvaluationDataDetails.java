package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class EvaluationDataDetails {

	public EvaluationData evaluation;

	public int submittedTotal = Common.UNINITIALIZED_INT;
	public int expectedTotal = Common.UNINITIALIZED_INT;

	public ArrayList<TeamData> teams = new ArrayList<TeamData>();

	public EvaluationDataDetails(EvaluationData evaluation) {
		this.evaluation = evaluation;
	}

	public TeamData getTeamData(String teamName) {
		for (TeamData team : teams) {
			if (team.name.equals(teamName)) {
				return team;
			}
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + evaluation.course + ", name:" + evaluation.name
				+ Common.EOL);
		for (TeamData team : teams) {
			sb.append(team.toString(1));
		}
		return sb.toString();
	}

}
