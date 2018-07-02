package teammates.storage.entity;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;

/**
 * Represents an instructor-created Feedback Session.
 */
@Entity
@Index
public class FeedbackSession extends BaseEntity {

    private static final Logger log = Logger.getLogger();

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

    @Unindex
    private boolean isFollowingCourseTimeZone;

    @SuppressWarnings("PMD.UnusedPrivateField") // Used by migration script
    @Ignore
    private boolean wasFollowingCourseTimeZone;

    // TODO Remove after all legacy data has been converted
    @IgnoreSave
    private Double timeZoneDouble;

    @IgnoreSave
    private String feedbackSessionType;

    @Unindex
    private long gracePeriod;

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
            String creatorEmail, Text instructions, Instant createdTime, Instant startTime, Instant endTime,
            Instant sessionVisibleFromTime, Instant resultsVisibleFromTime, String timeZone, long gracePeriod,
            boolean sentOpenEmail,
            boolean sentClosingEmail, boolean sentClosedEmail, boolean sentPublishedEmail,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled) {
        this(feedbackSessionName, courseId, creatorEmail, instructions, createdTime, startTime, endTime,
                sessionVisibleFromTime, resultsVisibleFromTime, timeZone, gracePeriod,
                sentOpenEmail, sentClosingEmail, sentClosedEmail, sentPublishedEmail, isOpeningEmailEnabled,
                isClosingEmailEnabled, isPublishedEmailEnabled, new HashSet<String>(), new HashSet<String>());
    }

    public FeedbackSession(String feedbackSessionName, String courseId,
            String creatorEmail, Text instructions, Instant createdTime, Instant startTime, Instant endTime,
            Instant sessionVisibleFromTime, Instant resultsVisibleFromTime, String timeZone, long gracePeriod,
            boolean sentOpenEmail, boolean sentClosingEmail,
            boolean sentClosedEmail, boolean sentPublishedEmail,
            boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, boolean isPublishedEmailEnabled,
            Set<String> instructorList, Set<String> studentList) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.creatorEmail = creatorEmail;
        this.instructions = instructions;
        this.createdTime = TimeHelper.convertInstantToDate(createdTime);
        this.startTime = TimeHelper.convertInstantToDate(startTime);
        this.endTime = TimeHelper.convertInstantToDate(endTime);
        this.sessionVisibleFromTime = TimeHelper.convertInstantToDate(sessionVisibleFromTime);
        this.resultsVisibleFromTime = TimeHelper.convertInstantToDate(resultsVisibleFromTime);
        this.timeZone = timeZone;
        this.gracePeriod = gracePeriod;
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
        this.isFollowingCourseTimeZone = true;
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
    private void setTimeZoneFromCourseTimeZoneIfRequired() {
        if (isFollowingCourseTimeZone) {
            wasFollowingCourseTimeZone = true;
            return;
        }

        Course course = Ref.create(Key.create(Course.class, courseId)).get();
        ZoneId courseTimeZone = Const.DEFAULT_TIME_ZONE; // UTC
        if (course == null) {
            log.severe("Could not retrieve course \"" + courseId
                    + "\"; defaulting to UTC for session: " + feedbackSessionId);
        } else {
            try {
                courseTimeZone = ZoneId.of(course.getTimeZone());
            } catch (DateTimeException e) {
                log.severe("Invalid time zone \"" + course.getTimeZone() + "\" encountered for course \"" + courseId
                        + "\"; defaulting to UTC for session: " + feedbackSessionId);
            }
        }
        timeZone = courseTimeZone.getId();
        isFollowingCourseTimeZone = true;
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

    @OnLoad
    @SuppressWarnings("unused") // called by Objectify
    private void adjustResultsVisibleFromTimeIfRequired() {
        if (Const.TIME_REPRESENTS_NEVER.equals(getResultsVisibleFromTime())) {
            setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        }
    }

    @OnLoad
    @SuppressWarnings("unused") // called by Objectify
    private void adjustOpeningAndClosingAndVisibleTimeForPrivateSessions() {
        if ("PRIVATE".equals(feedbackSessionType)) {
            setStartTime(TimeHelper.parseInstant("2118-01-01 12:00 AM +0000"));
            setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
            setEndTime(TimeHelper.parseInstant("2118-01-01 12:00 AM +0000"));
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

    public Instant getCreatedTime() {
        return TimeHelper.convertDateToInstant(createdTime);
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = TimeHelper.convertInstantToDate(createdTime);
    }

    public Instant getStartTime() {
        return TimeHelper.convertDateToInstant(startTime);
    }

    public void setStartTime(Instant startTime) {
        this.startTime = TimeHelper.convertInstantToDate(startTime);
    }

    public Instant getEndTime() {
        return TimeHelper.convertDateToInstant(endTime);
    }

    public void setEndTime(Instant endTime) {
        this.endTime = TimeHelper.convertInstantToDate(endTime);
    }

    public Instant getSessionVisibleFromTime() {
        return TimeHelper.convertDateToInstant(sessionVisibleFromTime);
    }

    public void setSessionVisibleFromTime(Instant sessionVisibleFromTime) {
        this.sessionVisibleFromTime = TimeHelper.convertInstantToDate(sessionVisibleFromTime);
    }

    public Instant getResultsVisibleFromTime() {
        return TimeHelper.convertDateToInstant(resultsVisibleFromTime);
    }

    public void setResultsVisibleFromTime(Instant resultsVisibleFromTime) {
        this.resultsVisibleFromTime = TimeHelper.convertInstantToDate(resultsVisibleFromTime);
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
                + ", gracePeriod=" + gracePeriod
                + ", sentOpenEmail=" + sentOpenEmail
                + ", sentPublishedEmail=" + sentPublishedEmail
                + ", isOpeningEmailEnabled=" + isOpeningEmailEnabled
                + ", isClosingEmailEnabled=" + isClosingEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + "]";
    }

}
