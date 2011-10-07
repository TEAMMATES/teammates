package teammates.jdo;

import com.google.appengine.api.datastore.Text;

/**
 * SubmissionDetailsForCoordinator is a data class that contains a mixture of
 * information from Submission and Student classes that are relevant to a
 * particular evaluation submission.
 * 
 * @author Gerald GOH
 * @see Submission
 * @see Student
 * 
 */
public class SubmissionDetailsForCoordinator {
	private String courseID;
	private String evaluationName;

	private String fromStudentName;
	private String toStudentName;
	private String fromStudent;
	private String toStudent;

	private String fromStudentComments;
	private String toStudentComments;

	private String teamName;

	private float points;
	private float pointsBumpRatio;
	private Text justification;
	private Text commentsToStudent;

	public SubmissionDetailsForCoordinator(String courseID,
			String evaluationName, String fromStudentName, String toStudentName,
			String fromStudent, String toStudent, String fromStudentComments,
			String toStudentComments, String teamName, float points,
			float pointsBumpRatio, Text justification, Text commentsToStudent) {
		this.courseID = courseID;
		this.evaluationName = evaluationName;
		this.fromStudentName = fromStudentName;
		this.toStudentName = toStudentName;
		this.fromStudent = fromStudent;
		this.toStudent = toStudent;
		this.fromStudentComments = fromStudentComments;
		this.toStudentComments = toStudentComments;
		this.teamName = teamName;
		this.points = points;
		this.pointsBumpRatio = pointsBumpRatio;
		this.justification = justification;
		this.commentsToStudent = commentsToStudent;
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

	public void setPointsBumpRatio(float pointsBumpRatio) {
		this.pointsBumpRatio = pointsBumpRatio;
	}

	public float getPointsBumpRatio() {
		return pointsBumpRatio;
	}

	public void setFromStudentComments(String fromStudentComments) {
		this.fromStudentComments = fromStudentComments;
	}

	public String getFromStudentComments() {
		return fromStudentComments;
	}

	public void setToStudentComments(String toStudentComments) {
		this.toStudentComments = toStudentComments;
	}

	public String getToStudentComments() {
		return toStudentComments;
	}

}