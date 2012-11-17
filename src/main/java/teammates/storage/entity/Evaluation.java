package teammates.storage.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Extension;

import teammates.common.Common;

import com.google.gson.annotations.SerializedName;

/**
 * Evaluation is a persistent data class that holds information pertaining to an
 * evaluation on Teammates.
 */
@PersistenceCapable
public class Evaluation {
	
	private static Logger log = Common.getLogger();
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	public Long id;

	@Persistent
	@SerializedName("course_id")
	private String courseID;

	@Persistent
	@SerializedName("name")
	private String name;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("instr")
	private String instructions;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("start_time")
	private Date startTime;
	
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("end_time")
	private Date endTime;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("timezone")
	private double timeZone;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("grace")
	private int gracePeriod;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("comments_on")
	private boolean commentsEnabled;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private boolean published = false;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private boolean activated = false;

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
			boolean commentsEnabled, Date start, Date deadline,
			double timeZone, int gracePeriod) {
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
		this.courseID = courseID.trim();
	}

	public String getCourseID() {
		return courseID;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions.trim();
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

	public boolean isReady() {
		Calendar currentTimeInUserTimeZone = Common.convertToUserTimeZone(
				Calendar.getInstance(), timeZone);

		Calendar evalStartTime = Calendar.getInstance();
		evalStartTime.setTime(startTime);
		
		log.fine("current:"+Common.calendarToString(currentTimeInUserTimeZone)+"|start:"+Common.calendarToString(evalStartTime));

		if (currentTimeInUserTimeZone.before(evalStartTime)){
			return false;
		}else {
			return (!activated);
		}
	}

		

}
