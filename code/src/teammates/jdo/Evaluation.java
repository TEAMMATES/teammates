package teammates.jdo;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Evaluation is a persistent data class that holds information pertaining to an
 * evaluation on Teammates.
 * 
 * @author Gerald GOH
 * 
 */
@PersistenceCapable
public class Evaluation {
	@SuppressWarnings("unused")
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	@SerializedName("course_id")
	private String courseID;

	@Persistent
	@SerializedName("name")
	private String name;

	@Persistent
	@SerializedName("instr")
	private String instructions;

	@Persistent
	@SerializedName("start_time")
	private Date startTime;
	@Persistent
	@SerializedName("end_time")
	private Date endTime;

	@Persistent
	@SerializedName("timezone")
	private double timeZone;

	@Persistent
	@SerializedName("grace")
	private int gracePeriod;

	@Persistent
	@SerializedName("comments_on")
	private boolean commentsEnabled;

	@Persistent
	private boolean published;

	@Persistent
	private boolean activated;

	/**
	 * Constructs an Evaluation object.
	 * 
	 * @param courseID
	 * @param name
	 * @param instructions
	 * @param commentsEnabled
	 * @param start
	 * @param deadline
	 * @param gracePeriod
	 */
	public Evaluation(String courseID, String name, String instructions,
			boolean commentsEnabled, Date start, Date deadline, double timeZone,
			int gracePeriod) {
		this.setCourseID(courseID);
		this.setName(name);
		this.setInstructions(instructions);
		this.setCommentsEnabled(commentsEnabled);
		this.setStart(start);
		this.setDeadline(deadline);
		this.setGracePeriod(gracePeriod);
		this.setPublished(false);
		this.setTimeZone(timeZone);

	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setStart(Date start) {
		this.startTime = start;
	}

	public Date getStart() {
		return startTime;
	}

	public void setDeadline(Date deadline) {
		this.endTime = deadline;
	}

	public Date getDeadline() {
		return endTime;
	}

	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public int getGracePeriod() {
		return gracePeriod;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public boolean isPublished() {
		return published;
	}

	public void setCommentsEnabled(boolean commentsEnabled) {
		this.commentsEnabled = commentsEnabled;
	}

	public boolean isCommentsEnabled() {
		return commentsEnabled;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setTimeZone(double timeZone2) {
		this.timeZone = timeZone2;
	}

	public double getTimeZone() {
		return timeZone;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("courseID: " + courseID);
		sb.append("\nname:" + name);
		sb.append("\ninstruction: " + instructions);
		sb.append("\nstarttime: " + startTime);
		sb.append("\nendtime: " + endTime);
		return sb.toString();
	}
}
