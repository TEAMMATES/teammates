package teammates.jdo;

import com.google.appengine.api.datastore.Text;

/**
 * SubmissionDetailsForStudent is a data class that contains a mixture of
 * information from Submission and Student classes that are relevant to a
 * particular evaluation submission.
 * 
 * @author Gerald GOH
 * @see Submission
 * @see Student
 * 
 */
public class SubmissionDetailsForStudent {
	private String courseID;
	private String evaluationName;

	private String fromStudentName;
	private String toStudentName;
	private String fromStudent;
	private String toStudent;

	private String teamName;

	private float points;
	private Text justification;
	private Text commentsToStudent;

	/**
	 * Constructs a SubmissionDetailsForStudent object.
	 * 
	 * @param courseID
	 * @param evaluationName
	 * @param fromStudentName
	 * @param toStudentName
	 * @param fromStudent
	 * @param toStudent
	 * @param teamName
	 * @param points
	 * @param justification
	 * @param commentsToStudent
	 */
	public SubmissionDetailsForStudent(String courseID, String evaluationName,
			String fromStudentName, String toStudentName, String fromStudent,
			String toStudent, String teamName, float points,
			Text justification, Text commentsToStudent) {
		this.setCourseID(courseID);
		this.setEvaluationName(evaluationName);
		
		this.setFromStudentName(fromStudentName);
		this.setToStudentName(toStudentName);
		this.setFromStudent(fromStudent);
		this.setToStudent(toStudent);
		
		this.setTeamName(teamName);
		
		this.setPoints(points);
		this.setJustification(justification);
		this.setCommentsToStudent(commentsToStudent);
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setEvaluationName(String evaluationName) {
		this.evaluationName = evaluationName;
	}

	public String getEvaluationName() {
		return evaluationName;
	}

	public void setFromStudentName(String fromStudentName) {
		this.fromStudentName = fromStudentName;
	}

	public String getFromStudentName() {
		return fromStudentName;
	}

	public void setToStudentName(String toStudentName) {
		this.toStudentName = toStudentName;
	}

	public String getToStudentName() {
		return toStudentName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	public float getPoints() {
		return points;
	}

	public void setJustification(Text justification) {
		this.justification = justification;
	}

	public Text getJustification() {
		return justification;
	}

	public void setCommentsToStudent(Text commentsToStudent) {
		this.commentsToStudent = commentsToStudent;
	}

	public Text getCommentsToStudent() {
		return commentsToStudent;
	}

	public void setFromStudent(String fromStudent) {
		this.fromStudent = fromStudent;
	}

	public String getFromStudent() {
		return fromStudent;
	}

	public void setToStudent(String toStudent) {
		this.toStudent = toStudent;
	}

	public String getToStudent() {
		return toStudent;
	}
}
