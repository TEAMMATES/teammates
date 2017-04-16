package teammates.storage.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Const;

/**
 * Represents an instructor-created Feedback Session.
 */
@PersistenceCapable
public class FeedbackSession extends Entity {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(FeedbackSession.class);

    // Format is feedbackSessionName%courseId
    // PMD.UnusedPrivateField and SingularField are suppressed
    // as feedbackSessionId is persisted to the database
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    @PrimaryKey
    @Persistent
    private transient String feedbackSessionId;

    @Persistent
    private String feedbackSessionName;

    @Persistent
    private String courseId;

    @Persistent
    private String creatorEmail; //TODO: should this be googleId?

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Set<String> respondingInstructorList;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Set<String> respondingStudentList;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Text instructions;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Date createdTime;

    @Persistent
    private Date startTime;

    @Persistent
    private Date endTime;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Date sessionVisibleFromTime;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Date resultsVisibleFromTime;

    /** This is legacy data that is no longer used. <br>
     * The value is set to Const.INT_UNINITIALIZED if it is already processed or
     * the old value if it hasn't. <br>
     * TODO Remove this field
     */
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private int timeZone;

    /** This replaces the legacy field timeZone. <br>
     * The value is null for legacy data. <br>
     * TODO Rename to timeZone after removing legacy field
     */
    @Persistent
    private Double timeZoneDouble;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private int gracePeriod;

    @Persistent
    private FeedbackSessionType feedbackSessionType;

    @Persistent
    private boolean sentOpenEmail;

    @Persistent
    private Boolean sentClosingEmail;

    @Persistent
    private Boolean sentClosedEmail;

    @Persistent
    private boolean sentPublishedEmail;

    //TODO change to primitive types and update getter
    @Persistent
    private Boolean isOpeningEmailEnabled;

    @Persistent
    private Boolean isClosingEmailEnabled;

    @Persistent
    private Boolean isPublishedEmailEnabled;

    public FeedbackSession(String feedbackSessionName, String courseId,
            String creatorEmail, Text instructions, Date createdTime, Date startTime, Date endTime,
            Date sessionVisibleFromTime, Date resultsVisibleFromTime, double timeZone, int gracePeriod,
            FeedbackSessionType feedbackSessionType, boolean sentOpenEmail,
            boolean sentClosingEmail, boolean sentClosedEmail, boolean sentPublishedEmail,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled) {
        this(feedbackSessionName, courseId, creatorEmail, instructions, createdTime, startTime, endTime,
             sessionVisibleFromTime, resultsVisibleFromTime, timeZone, gracePeriod, feedbackSessionType,
             sentOpenEmail, sentClosingEmail, sentClosedEmail, sentPublishedEmail, isOpeningEmailEnabled,
             isClosingEmailEnabled, isPublishedEmailEnabled, new HashSet<String>(), new HashSet<String>());
    }

    public FeedbackSession(String feedbackSessionName, String courseId,
            String creatorEmail, Text instructions, Date createdTime, Date startTime, Date endTime,
            Date sessionVisibleFromTime, Date resultsVisibleFromTime, double timeZone, int gracePeriod,
            FeedbackSessionType feedbackSessionType, boolean sentOpenEmail, boolean sentClosingEmail,
            boolean sentClosedEmail, boolean sentPublishedEmail,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled,
            Set<String> instructorList, Set<String> studentList) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        this.instructions = instructions;
        this.createdTime = createdTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionVisibleFromTime = sessionVisibleFromTime;
        this.resultsVisibleFromTime = resultsVisibleFromTime;
        this.timeZone = Const.INT_UNINITIALIZED;
        this.timeZoneDouble = timeZone;
        this.gracePeriod = gracePeriod;
        this.feedbackSessionType = feedbackSessionType;
        this.sentOpenEmail = sentOpenEmail;
        this.sentClosingEmail = sentClosingEmail;
        this.sentClosedEmail = sentClosedEmail;
        this.sentPublishedEmail = sentPublishedEmail;
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
        this.isClosingEmailEnabled = isClosingEmailEnabled;
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
        this.feedbackSessionId = this.feedbackSessionName + "%" + this.courseId;
        this.respondingInstructorList = instructorList;
        this.respondingStudentList = studentList;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorId) {
        this.creatorEmail = creatorId;
    }

    public Text getInstructions() {
        return instructions;
    }

    public void setInstructions(Text instructions) {
        this.instructions = instructions;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getSessionVisibleFromTime() {
        return sessionVisibleFromTime;
    }

    public void setSessionVisibleFromTime(Date sessionVisibleFromTime) {
        this.sessionVisibleFromTime = sessionVisibleFromTime;
    }

    public Date getResultsVisibleFromTime() {
        return resultsVisibleFromTime;
    }

    public void setResultsVisibleFromTime(Date resultsVisibleFromTime) {
        this.resultsVisibleFromTime = resultsVisibleFromTime;
    }

    /** This method automatically converts the legacy timeZone field to
     * the new timeZoneDouble field and returns the value of timeZoneDouble.
     */
    public double getTimeZone() {
        if (timeZone != Const.INT_UNINITIALIZED) {
            timeZoneDouble = Double.valueOf(timeZone);
            timeZone = Const.INT_UNINITIALIZED;
        }
        return timeZoneDouble;
    }

    /** This method automatically marks the timeZone field as legacy
     * and store the timeZone data to the new timeZoneDouble field.
     */
    public void setTimeZone(double timeZone) {
        this.timeZone = Const.INT_UNINITIALIZED;
        this.timeZoneDouble = timeZone;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public FeedbackSessionType getFeedbackSessionType() {
        return feedbackSessionType;
    }

    public void setFeedbackSessionType(FeedbackSessionType feedbackSessionType) {
        this.feedbackSessionType = feedbackSessionType;
    }

    public boolean isSentOpenEmail() {
        return sentOpenEmail;
    }

    public void setSentOpenEmail(boolean sentOpenEmail) {
        this.sentOpenEmail = sentOpenEmail;
    }

    public boolean isSentClosingEmail() {
        // Legacy data might not have this field
        if (sentClosingEmail == null) {
            return false;
        }
        return sentClosingEmail;
    }

    public void setSentClosingEmail(boolean sentClosingEmail) {
        this.sentClosingEmail = sentClosingEmail;
    }

    public boolean isSentClosedEmail() {
        // Legacy data might not have this field
        if (sentClosedEmail == null) {
            return false;
        }
        return sentClosedEmail;
    }

    public void setSentClosedEmail(boolean sentClosedEmail) {
        this.sentClosedEmail = sentClosedEmail;
    }

    public boolean isSentPublishedEmail() {
        return sentPublishedEmail;
    }

    public void setSentPublishedEmail(boolean sentPublishedEmail) {
        this.sentPublishedEmail = sentPublishedEmail;
    }

    public boolean isOpeningEmailEnabled() {
        // Legacy data might not have this field
        if (isOpeningEmailEnabled == null) {
            isOpeningEmailEnabled = true;
        }

        return isOpeningEmailEnabled.booleanValue();
    }

    public void setIsOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        // Legacy data might not have this field
        if (isClosingEmailEnabled == null) {
            isClosingEmailEnabled = true;
        }

        return isClosingEmailEnabled.booleanValue();
    }

    public void setSendClosingEmail(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        // Legacy data might not have this field
        if (isPublishedEmailEnabled == null) {
            isPublishedEmailEnabled = true;
        }

        return isPublishedEmailEnabled.booleanValue();
    }

    public void setSendPublishedEmail(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    public Set<String> getRespondingInstructorList() {
        return this.respondingInstructorList;
    }

    public void setRespondingInstructorList(Set<String> instructorList) {
        this.respondingInstructorList = instructorList;
    }

    public Set<String> getRespondingStudentList() {
        return this.respondingStudentList;
    }

    public void setRespondingStudentList(Set<String> studentList) {
        this.respondingStudentList = studentList;
    }

    @Override
    public String toString() {
        return "FeedbackSession [feedbackSessionName=" + feedbackSessionName
                + ", courseId=" + courseId + ", creatorId=" + creatorEmail
                + ", instructions=" + instructions + ", createdTime="
                + createdTime + ", startTime=" + startTime + ", endTime="
                + endTime + ", sessionVisibleFromTime="
                + sessionVisibleFromTime + ", resultsVisibleFromTime="
                + resultsVisibleFromTime + ", timeZone=" + timeZone
                + ", gracePeriod=" + gracePeriod + ", feedbackSessionType="
                + feedbackSessionType + ", sentOpenEmail=" + sentOpenEmail
                + ", sentPublishedEmail=" + sentPublishedEmail
                + ", isOpeningEmailEnabled=" + isOpeningEmailEnabled
                + ", isClosingEmailEnabled=" + isClosingEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + "]";
    }

}
