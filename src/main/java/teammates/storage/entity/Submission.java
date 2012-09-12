package teammates.storage.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Submission is a persistent data class that contains an evaluation submission
 * from a student to another student.
 */
@PersistenceCapable
public class Submission {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	public Long id;

	@Persistent
	String fromStudent;

	@Persistent
	String toStudent;

	@Persistent
	String courseID;

	@Persistent
	String evaluationName;

	@Persistent
	int points;

	@Persistent
	Text justification;

	@Persistent
	Text commentsToStudent;

	@Persistent
	String teamName;

	/**
	 * Constructs a Submission object.
	 * 
	 * @param fromStudent
	 * @param toStudent
	 * @param courseID
	 * @param evaluationName
	 * @param teamName
	 */
	public Submission(String fromStudent, String toStudent, String courseID,
			String evaluationName, String teamName) {
		this.setFromStudent(fromStudent);
		this.setToStudent(toStudent);
		this.setCourseID(courseID);
		this.setEvaluationName(evaluationName);
		this.setTeamName(teamName);

		this.setJustification(new Text(""));
		this.setCommentsToStudent(new Text(""));
		this.points = -999;
	}

	public void setFromStudent(String fromStudent) {
		this.fromStudent = fromStudent.trim();
	}

	public String getFromStudent() {
		return fromStudent;
	}

	public void setToStudent(String toStudent) {
		this.toStudent = toStudent.trim();
	}

	public String getToStudent() {
		return toStudent;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID.trim();
	}

	public String getCourseID() {
		return courseID;
	}

	public void setEvaluationName(String evaluationName) {
		this.evaluationName = evaluationName.trim();
	}

	public String getEvaluationName() {
		return evaluationName;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
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

	public void setTeamName(String teamName) {
		this.teamName = teamName.trim();
	}

	public String getTeamName() {
		return teamName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.courseID + "|");
		sb.append(this.evaluationName + "|");
		sb.append(this.fromStudent + "|");
		sb.append(this.toStudent + "|");
		sb.append(this.points + "|");
		sb.append(this.teamName + "\n");
		return sb.toString();
	}
}
