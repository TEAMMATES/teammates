package teammates.jdo;

import java.util.Date;

import teammates.Common;

/**
 * EvaluationDetailsForCoordinator is a data class that contains information
 * from Evaluation and Submission classes that are relevant to a particular
 * coordinator.
 * 
 * @author Gerald GOH
 * @deprecated Use EvaluationData instead.
 * 
 */

public class EvaluationDetailsForCoordinator {
	public String courseID= null;
	public String name= null;
	public boolean commentsEnabled;
	public String instructions= null;
	public Date start= null;
	public Date deadline = null;
	public double timeZone= Common.UNINITIALIZED_DOUBLE;
	public int gracePeriod= Common.UNINITIALIZED_INT;

	public boolean published;
	public boolean activated;

	public int numberOfCompletedEvaluations = Common.UNINITIALIZED_INT;
	public int numberOfEvaluations= Common.UNINITIALIZED_INT;

	/**
	 * Constructs an EvaluationDetailsForCoordinator object.
	 * 
	 * @param courseID
	 * @param name
	 * @param instructions
	 * @param commentsEnabled
	 * @param start
	 * @param deadline
	 * @param timeZone
	 * @param gracePeriod
	 * @param published
	 * @param activated
	 * @param numberOfCompletedEvaluations
	 * @param numberOfEvaluations
	 */
	public EvaluationDetailsForCoordinator(String courseID, String name,
			String instructions, boolean commentsEnabled, Date start,
			Date deadline, double timeZone, int gracePeriod, boolean published,
			boolean activated, int numberOfCompletedEvaluations,
			int numberOfEvaluations) {
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
	
	public EvaluationDetailsForCoordinator(String courseId, String evaluationName){
		this.courseID = courseId;
		this.name = evaluationName;
	}

	@Deprecated
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	@Deprecated
	public String getCourseID() {
		return courseID;
	}

	@Deprecated
	public void setName(String name) {
		this.name = name;
	}

	@Deprecated
	public String getName() {
		return name;
	}

	@Deprecated
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	@Deprecated
	public String getInstructions() {
		return instructions;
	}

	@Deprecated
	public void setStart(Date start) {
		this.start = start;
	}

	@Deprecated
	public Date getStart() {
		return start;
	}

	@Deprecated
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Deprecated
	public Date getDeadline() {
		return deadline;
	}

	@Deprecated
	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	@Deprecated
	public int getGracePeriod() {
		return gracePeriod;
	}

	@Deprecated
	public void setPublished(boolean published) {
		this.published = published;
	}

	@Deprecated
	public boolean isPublished() {
		return published;
	}

	@Deprecated
	public void setNumberOfCompletedEvaluations(int numberOfCompletedEvaluations) {
		this.numberOfCompletedEvaluations = numberOfCompletedEvaluations;
	}

	@Deprecated
	public int getNumberOfCompletedEvaluations() {
		return numberOfCompletedEvaluations;
	}

	@Deprecated
	public void setNumberOfEvaluations(int numberOfEvaluations) {
		this.numberOfEvaluations = numberOfEvaluations;
	}

	@Deprecated
	public int getNumberOfEvaluations() {
		return numberOfEvaluations;
	}

	@Deprecated
	public void setCommentsEnabled(boolean commentsEnabled) {
		this.commentsEnabled = commentsEnabled;
	}

	@Deprecated
	public boolean isCommentsEnabled() {
		return commentsEnabled;
	}

	@Deprecated
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Deprecated
	public boolean isActivated() {
		return activated;
	}

	@Deprecated
	public void setTimeZone(double timeZone) {
		this.timeZone = timeZone;
	}

	@Deprecated
	public double getTimeZone() {
		return timeZone;
	}
}
