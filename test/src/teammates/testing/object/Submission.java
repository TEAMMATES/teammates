package teammates.testing.object;

import com.google.gson.annotations.SerializedName;


public class Submission 
{
	@SuppressWarnings("unused")
	private Long id;
	
	String fromStudent;
	
	String toStudent;
	
	@SerializedName("courseid")
	String courseID;

	@SerializedName("evaluationname")
	String evaluationName;
	
	int points;
	
	String justification;
	
	String commentsToStudent;

	@SerializedName("teamname")
	String teamName;

	public Submission(String fromStudent, String toStudent, String courseID, String evaluationName, String teamName)
	{
		this.setFromStudent(fromStudent);
		this.setToStudent(toStudent);
		this.setCourseID(courseID);
		this.setEvaluationName(evaluationName);
		this.setTeamName(teamName);
		
		this.setJustification("");
		this.setCommentsToStudent("");
		this.points = -999;
	}
	
	public Submission(String fromStudent, String toStudent, String courseID, String evaluationName, 
			String teamName, int points, String justification, String commentsToStudent)
	{
		this.setFromStudent(fromStudent);
		this.setToStudent(toStudent);
		this.setCourseID(courseID);
		this.setEvaluationName(evaluationName);
		this.setTeamName(teamName);
		
		this.setPoints(points);
		this.setJustification(justification);
		this.setCommentsToStudent(commentsToStudent);
	}
	
	public String getFromStudent() {
		return fromStudent;
	}

	public void setFromStudent(String fromStudent) {
		this.fromStudent = fromStudent;
	}

	public String getToStudent() {
		return toStudent;
	}

	public void setToStudent(String toStudent) {
		this.toStudent = toStudent;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getEvaluationName() {
		return evaluationName;
	}

	public void setEvaluationName(String evaluationName) {
		this.evaluationName = evaluationName;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public String getCommentsToStudent() {
		return commentsToStudent;
	}

	public void setCommentsToStudent(String commentsToStudent) {
		this.commentsToStudent = commentsToStudent;
	}
	
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
}
