package teammates.jdo;

import java.util.Date;

/**
 * EvaluationDetailsForCoordinator is a data class that contains information
 * from Evaluation and Submission classes that are relevant to a particular
 * coordinator.
 * 
 * @author Gerald GOH
 * 
 */
public class EvaluationDetailsForCoordinator {
	private String courseID;
	private String name;
	private boolean commentsEnabled;
	private String instructions;
	private Date start;
	private Date deadline;
	private double timeZone;
	private int gracePeriod;

	private boolean published;
	private boolean activated;

	private int numberOfCompletedEvaluations;
	private int numberOfEvaluations;

	public EvaluationDetailsForCoordinator(String courseID, String name,
			String instructions, boolean commentsEnabled, Date start, Date deadline,
			double timeZone, int gracePeriod, boolean published, boolean activated,
			int numberOfCompletedEvaluations, int numberOfEvaluations) {
		this.setCourseID(courseID);
		this.setName(name);
		this.setInstructions(instructions);
		this.setStart(start);
		this.setDeadline(deadline);
		this.setTimeZone(timeZone);
		this.setGracePeriod(gracePeriod);
		this.setPublished(published);
		this.setActivated(activated);
		this.setNumberOfCompletedEvaluations(numberOfCompletedEvaluations);
		this.setNumberOfEvaluations(numberOfEvaluations);
		this.setCommentsEnabled(commentsEnabled);
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
		this.start = start;
	}

	public Date getStart() {
		return start;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getDeadline() {
		return deadline;
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

	public void setNumberOfCompletedEvaluations(int numberOfCompletedEvaluations) {
		this.numberOfCompletedEvaluations = numberOfCompletedEvaluations;
	}

	public int getNumberOfCompletedEvaluations() {
		return numberOfCompletedEvaluations;
	}

	public void setNumberOfEvaluations(int numberOfEvaluations) {
		this.numberOfEvaluations = numberOfEvaluations;
	}

	public int getNumberOfEvaluations() {
		return numberOfEvaluations;
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

	public void setTimeZone(double timeZone) {
		this.timeZone = timeZone;
	}

	public double getTimeZone() {
		return timeZone;
	}

}