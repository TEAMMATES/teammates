package teammates.storage.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

/**
 * Represents an instructor-created Feedback Session.
 */
@Entity
@Index
public class FeedbackSession extends BaseEntity {

    // Format is feedbackSessionName%courseId
    // PMD.UnusedPrivateField and SingularField are suppressed
    // as feedbackSessionId is persisted to the database
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    @Id
    private transient String feedbackSessionId;

    private String feedbackSessionName;

    private String courseId;

    private String creatorEmail; //TODO: should this be googleId?

    @Unindex
    private Set<String> respondingInstructorList = new HashSet<>();

    @Unindex
    private Set<String> respondingStudentList = new HashSet<>();

    @Unindex
    private Text instructions;

    @Unindex
    private Date createdTime;

    private Date startTime;

    private Date endTime;

    @Unindex
    private Date sessionVisibleFromTime;

    @Unindex
    private Date resultsVisibleFromTime;

    /**
     * Defaults to false in legacy data where local dates are stored instead of UTC. <br>
     * TODO Remove after all legacy data has been converted
     */
    @Unindex
    private boolean isTimeStoredInUtc;

    private String timeZone;

    // TODO Remove after all legacy data has been converted
    @IgnoreSave
    private Double timeZoneDouble;

    @Unindex
    private int gracePeriod;

    private FeedbackSessionType feedbackSessionType;

    private boolean sentOpenEmail;

    private boolean sentClosingEmail;

    private boolean sentClosedEmail;

    private boolean sentPublishedEmail;

    //TODO change to primitive types
    private Boolean isOpeningEmailEnabled;

    private Boolean isClosingEmailEnabled;

    private Boolean isPublishedEmailEnabled;

    @SuppressWarnings("unused")
    private FeedbackSession() {
        // required by Objectify
    }

    public FeedbackSession(String feedbackSessionName, String courseId,
            String creatorEmail, Text instructions, Date createdTime, Date startTime, Date endTime,
            Date sessionVisibleFromTime, Date resultsVisibleFromTime, double offset, int gracePeriod,
            FeedbackSessionType feedbackSessionType, boolean sentOpenEmail,
            boolean sentClosingEmail, boolean sentClosedEmail, boolean sentPublishedEmail,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled) {
        this(feedbackSessionName, courseId, creatorEmail, instructions, createdTime, startTime, endTime,
             sessionVisibleFromTime, resultsVisibleFromTime, offset, gracePeriod, feedbackSessionType,
             sentOpenEmail, sentClosingEmail, sentClosedEmail, sentPublishedEmail, isOpeningEmailEnabled,
             isClosingEmailEnabled, isPublishedEmailEnabled, new HashSet<String>(), new HashSet<String>());
    }

    public FeedbackSession(String feedbackSessionName, String courseId,
            String creatorEmail, Text instructions, Date createdTime, Date startTime, Date endTime,
            Date sessionVisibleFromTime, Date resultsVisibleFromTime, double offset, int gracePeriod,
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
        this.timeZone = convertOffsetToZoneId(offset);
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
        this.respondingInstructorList = instructorList == null ? new HashSet<String>() : instructorList;
        this.respondingStudentList = studentList == null ? new HashSet<String>() : studentList;
        this.isTimeStoredInUtc = true;
    }

    @OnLoad
    @SuppressWarnings("unused") // called by Objectify
    private void convertFieldsToUtcIfRequired() {
        if (isTimeStoredInUtc) {
            return;
        }

        startTime = TimeHelper.convertLocalDateToUtc(startTime, timeZoneDouble);
        endTime = TimeHelper.convertLocalDateToUtc(endTime, timeZoneDouble);
        sessionVisibleFromTime = TimeHelper.convertLocalDateToUtc(sessionVisibleFromTime, timeZoneDouble);
        resultsVisibleFromTime = TimeHelper.convertLocalDateToUtc(resultsVisibleFromTime, timeZoneDouble);
        isTimeStoredInUtc = true;
    }

    @OnLoad
    @SuppressWarnings("unused") // called by Objectify
    private void setTimeZoneFromOffsetIfRequired() {
        if (timeZoneDouble == null) {
            return;
        }

        double offset;
        if (timeZone.equals(String.valueOf(Const.INT_UNINITIALIZED))) {
            offset = timeZoneDouble;
        } else {
            offset = Double.valueOf(timeZone);
        }
        timeZone = convertOffsetToZoneId(offset);
    }

    @OnLoad
    @SuppressWarnings("unused") // called by Objectify
    private void populateMissingBooleansIfRequired() {
        if (isOpeningEmailEnabled == null) {
            isOpeningEmailEnabled = true;
        }
        if (isClosingEmailEnabled == null) {
            isClosingEmailEnabled = true;
        }
        if (isPublishedEmailEnabled == null) {
            isPublishedEmailEnabled = true;
        }
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

    public double getOffset() {
        return convertZoneIdToOffset(timeZone);
    }

    public void setOffset(double offset) {
        this.timeZone = convertOffsetToZoneId(offset);
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
        return sentClosingEmail;
    }

    public void setSentClosingEmail(boolean sentClosingEmail) {
        this.sentClosingEmail = sentClosingEmail;
    }

    public boolean isSentClosedEmail() {
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
        return isOpeningEmailEnabled;
    }

    public void setIsOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public void setSendClosingEmail(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
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

    private String convertOffsetToZoneId(double offset) {
        return ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (offset * 60 * 60))).getId();
    }

    private double convertZoneIdToOffset(String zoneId) {
        return ((double) ZoneId.of(zoneId).getRules().getOffset(Instant.now()).getTotalSeconds()) / 60 / 60;
    }

}
