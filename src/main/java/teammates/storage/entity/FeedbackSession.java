package teammates.storage.entity;

import java.time.Instant;
import java.util.Map;

import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents an instructor-created Feedback Session.
 */
@Entity
@Index
public class FeedbackSession extends BaseEntity {

    // PMD.UnusedPrivateField and SingularField are suppressed
    // as feedbackSessionId is persisted to the database
    /**
     * The unique id of the entity.
     *
     * @see #generateId(String, String)
     */
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    @Id
    private transient String feedbackSessionId;

    private String feedbackSessionName;

    private String courseId;

    private String creatorEmail;

    @Unindex
    private String instructions;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant deletedTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant startTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant endTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant sessionVisibleFromTime;

    @Translate(InstantTranslatorFactory.class)
    private Instant resultsVisibleFromTime;

    private String timeZone;

    @Unindex
    private long gracePeriod;

    private boolean sentOpeningSoonEmail;

    @AlsoLoad("sentOpeningEmail")
    private boolean sentOpenedEmail;

    @AlsoLoad("sentClosingEmail")
    private boolean sentClosingSoonEmail;

    private boolean sentClosedEmail;

    private boolean sentPublishedEmail;

    @AlsoLoad("isOpeningEmailEnabled")
    private boolean isOpenedEmailEnabled;

    @AlsoLoad("isClosingEmailEnabled")
    private boolean isClosingSoonEmailEnabled;

    private boolean isPublishedEmailEnabled;

    @Unindex
    @Serialize
    private Map<String, Instant> studentDeadlines;

    @Unindex
    @Serialize
    private Map<String, Instant> instructorDeadlines;

    @SuppressWarnings("unused")
    private FeedbackSession() {
        // required by Objectify
    }

    public FeedbackSession(String feedbackSessionName, String courseId, String creatorEmail,
            String instructions, Instant createdTime, Instant deletedTime, Instant startTime, Instant endTime,
            Instant sessionVisibleFromTime, Instant resultsVisibleFromTime, String timeZone, long gracePeriod,
            boolean sentOpeningSoonEmail, boolean sentOpenedEmail, boolean sentClosingSoonEmail,
            boolean sentClosedEmail, boolean sentPublishedEmail, boolean isOpenedEmailEnabled,
            boolean isClosingSoonEmailEnabled, boolean isPublishedEmailEnabled, Map<String, Instant> studentDeadlines,
            Map<String, Instant> instructorDeadlines) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        this.instructions = instructions;
        this.createdTime = createdTime;
        this.deletedTime = deletedTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionVisibleFromTime = sessionVisibleFromTime;
        this.resultsVisibleFromTime = resultsVisibleFromTime;
        this.timeZone = timeZone;
        this.gracePeriod = gracePeriod;
        this.sentOpeningSoonEmail = sentOpeningSoonEmail;
        this.sentOpenedEmail = sentOpenedEmail;
        this.sentClosingSoonEmail = sentClosingSoonEmail;
        this.sentClosedEmail = sentClosedEmail;
        this.sentPublishedEmail = sentPublishedEmail;
        this.isOpenedEmailEnabled = isOpenedEmailEnabled;
        this.isClosingSoonEmailEnabled = isClosingSoonEmailEnabled;
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
        this.studentDeadlines = studentDeadlines;
        this.instructorDeadlines = instructorDeadlines;
        this.feedbackSessionId = generateId(this.feedbackSessionName, this.courseId);
    }

    /**
     * Generates an unique ID for the feedback session.
     */
    public static String generateId(String feedbackSessionName, String courseId) {
        // Format is feedbackSessionName%courseId
        return feedbackSessionName + '%' + courseId;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Instant deletedTime) {
        this.deletedTime = deletedTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getSessionVisibleFromTime() {
        return sessionVisibleFromTime;
    }

    public void setSessionVisibleFromTime(Instant sessionVisibleFromTime) {
        this.sessionVisibleFromTime = sessionVisibleFromTime;
    }

    public Instant getResultsVisibleFromTime() {
        return resultsVisibleFromTime;
    }

    public void setResultsVisibleFromTime(Instant resultsVisibleFromTime) {
        this.resultsVisibleFromTime = resultsVisibleFromTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(long gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public boolean isSentOpeningSoonEmail() {
        return sentOpeningSoonEmail;
    }

    public void setSentOpeningSoonEmail(boolean sentOpeningSoonEmail) {
        this.sentOpeningSoonEmail = sentOpeningSoonEmail;
    }

    public boolean isSentOpenedEmail() {
        return sentOpenedEmail;
    }

    public void setSentOpenedEmail(boolean sentOpenedEmail) {
        this.sentOpenedEmail = sentOpenedEmail;
    }

    public boolean isSentClosingSoonEmail() {
        return sentClosingSoonEmail;
    }

    public void setSentClosingSoonEmail(boolean sentClosingSoonEmail) {
        this.sentClosingSoonEmail = sentClosingSoonEmail;
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

    public boolean isOpenedEmailEnabled() {
        return isOpenedEmailEnabled;
    }

    public void setIsOpenedEmailEnabled(boolean isOpenedEmailEnabled) {
        this.isOpenedEmailEnabled = isOpenedEmailEnabled;
    }

    public boolean isClosingSoonEmailEnabled() {
        return isClosingSoonEmailEnabled;
    }

    public void setSendClosingSoonEmail(boolean isClosingSoonEmailEnabled) {
        this.isClosingSoonEmailEnabled = isClosingSoonEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setSendPublishedEmail(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    public Map<String, Instant> getStudentDeadlines() {
        return studentDeadlines;
    }

    public void setStudentDeadlines(Map<String, Instant> studentDeadlines) {
        this.studentDeadlines = studentDeadlines;
    }

    public Map<String, Instant> getInstructorDeadlines() {
        return instructorDeadlines;
    }

    public void setInstructorDeadlines(Map<String, Instant> instructorDeadlines) {
        this.instructorDeadlines = instructorDeadlines;
    }

    @Override
    public String toString() {
        return "FeedbackSession [feedbackSessionName=" + feedbackSessionName
                + ", courseId=" + courseId + ", creatorId=" + creatorEmail
                + ", instructions=" + instructions + ", createdTime="
                + createdTime + ", deletedTime=" + deletedTime + ", startTime=" + startTime
                + ", endTime=" + endTime + ", sessionVisibleFromTime="
                + sessionVisibleFromTime + ", resultsVisibleFromTime="
                + resultsVisibleFromTime + ", timeZone=" + timeZone
                + ", gracePeriod=" + gracePeriod
                + ", sentOpeningSoonEmail=" + sentOpeningSoonEmail
                + ", sentOpenedEmail=" + sentOpenedEmail
                + ", sentClosingSoonEmail=" + sentClosingSoonEmail
                + ", sentClosedEmail=" + sentClosedEmail
                + ", sentPublishedEmail=" + sentPublishedEmail
                + ", isOpenedEmailEnabled=" + isOpenedEmailEnabled
                + ", isClosingSoonEmailEnabled=" + isClosingSoonEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled
                + ", studentDeadlines=" + studentDeadlines
                + ", instructorDeadlines=" + instructorDeadlines
                + "]";
    }

}
