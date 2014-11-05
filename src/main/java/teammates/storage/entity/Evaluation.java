package teammates.storage.entity;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.common.util.Utils;

import com.google.appengine.api.datastore.Text;
import com.google.gson.annotations.SerializedName;

/**
 * Represents an evaluation/feedback session.
 */
@PersistenceCapable
public class Evaluation {

    @SuppressWarnings("unused")
    private static Logger log = Utils.getLogger();

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    @SerializedName("course_id")
    private String courseID;

    @Persistent
    @SerializedName("name")
    private String name;

    /**
     * This instructions field is just for backward-compatibility
     * (Some existing entities in Datastore still have String as the data type for instructions)
     */
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("instr")
    private String instructions;
    
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("longInstr")
    private Text longInstructions;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("start_time")
    private Date startTime;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("end_time")
    private Date endTime;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("timezone")
    private double timeZone;

    /**
     * The time (in minutes) the evaluation will stay open after the deadline.
     */
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("grace")
    private int gracePeriod;

    /**
     * Whether the evaluation allows peer-to-peer feedback.
     */
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("comments_on")
    private boolean commentsEnabled;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private boolean published = false;

    /**
     * Indicates if the evaluation has been 'activated'.
     * Activated means anything that needs to be done at the point of opening 
     * an evaluation (e.g., sending emails) has been done.
     */
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private boolean activated = false;
    
    public Evaluation(String courseId, String evaluationName,
            Text instructions, boolean commentsEnabled, Date start,
            Date deadline, double timeZone, int gracePeriod) {
        this.setCourseId(courseId);
        this.setName(evaluationName);
        this.setInstructions(null);
        this.setLongInstructions(instructions);
        this.setCommentsEnabled(commentsEnabled);
        this.setStart(start);
        this.setDeadline(deadline);
        this.setGracePeriod(gracePeriod);
        this.setPublished(false);
        this.setTimeZone(timeZone);
    }

    public Long getId() {
        return id;
    }
    
    public String getCourseId() {
        return courseID;
    }

    public void setCourseId(String courseId) {
        this.courseID = courseId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String evaluationName) {
        this.name = evaluationName.trim();
    }

    public String getInstructions() {
        return instructions;
    }
    
    public Text getLongInstructions() {
        return longInstructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public void setLongInstructions(Text instructions) {
        this.longInstructions = instructions;
    }

    public Date getStart() {
        return startTime;
    }

    public void setStart(Date start) {
        this.startTime = start;
    }

    public Date getDeadline() {
        return endTime;
    }

    public void setDeadline(Date deadline) {
        this.endTime = deadline;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isCommentsEnabled() {
        return commentsEnabled;
    }

    public void setCommentsEnabled(boolean commentsEnabled) {
        this.commentsEnabled = commentsEnabled;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    @Override
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
