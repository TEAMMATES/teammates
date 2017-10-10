package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.Text;
import com.google.gson.annotations.SerializedName;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.FeedbackSession;

public class FeedbackSessionAttributes extends EntityAttributes<FeedbackSession> implements SessionAttributes {
    // Required fields
    private String feedbackSessionName;
    private String courseId;
    private String creatorEmail;

    // Optional fields
    private Text instructions;
    private Date createdTime;
    @SerializedName("startTime")
    private Date startTimeUtc;
    @SerializedName("endTime")
    private Date endTimeUtc;
    @SerializedName("sessionVisibleFromTime")
    private Date sessionVisibleFromTimeUtc;
    @SerializedName("resultsVisibleFromTime")
    private Date resultsVisibleFromTimeUtc;
    private double timeZone;
    private int gracePeriod;
    private FeedbackSessionType feedbackSessionType;
    private boolean sentOpenEmail;
    private boolean sentClosingEmail;
    private boolean sentClosedEmail;
    private boolean sentPublishedEmail;
    private boolean isOpeningEmailEnabled;
    private boolean isClosingEmailEnabled;
    private boolean isPublishedEmailEnabled;
    private transient Set<String> respondingInstructorList;
    private transient Set<String> respondingStudentList;

    FeedbackSessionAttributes() {
        isOpeningEmailEnabled = true;
        isClosingEmailEnabled = true;
        isPublishedEmailEnabled = true;
        respondingInstructorList = new HashSet<>();
        respondingStudentList = new HashSet<>();

        instructions = new Text("");
    }

    public static FeedbackSessionAttributes valueOf(FeedbackSession fs) {
        return builder(fs.getFeedbackSessionName(), fs.getCourseId(), fs.getCreatorEmail())
                .withInstructions(fs.getInstructions())
                .withCreatedTime(fs.getCreatedTime())
                .withStartTime(fs.getStartTimeUtc())
                .withEndTime(fs.getEndTimeUtc())
                .withSessionVisibleFromTime(fs.getSessionVisibleFromTimeUtc())
                .withResultsVisibleFromTime(fs.getResultsVisibleFromTimeUtc())
                .withTimeZone(fs.getTimeZone())
                .withGracePeriod(fs.getGracePeriod())
                .withFeedbackSessionType(fs.getFeedbackSessionType())
                .withSentOpenEmail(fs.isSentOpenEmail())
                .withSentClosingEmail(fs.isSentClosingEmail())
                .withSentClosedEmail(fs.isSentClosedEmail())
                .withSentPublishedEmail(fs.isSentPublishedEmail())
                .withOpeningEmailEnabled(fs.isOpeningEmailEnabled())
                .withPublishedEmailEnabled(fs.isPublishedEmailEnabled())
                .withRespondingInstructorList(fs.getRespondingInstructorList())
                .withRespondingStudentList(fs.getRespondingStudentList())
                .withClosingEmailEnabled(fs.isClosingEmailEnabled())
                .build();
    }

    /**
     * Returns new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code isOpeningEmailEnabled = true}</li>
     * <li>{@code isClosingEmailEnabled = true}</li>
     * <li>{@code isPublishedEmailEnabled = true}</li>
     * <li>{@code respondingInstructorList = new HashSet<>()}</li>
     * <li>{@code respondingStudentList = new HashSet<>()}</li>
     * </ul>
     */
    public static Builder builder(String feedbackSessionName, String courseId, String creatorEmail) {
        return new Builder(feedbackSessionName, courseId, creatorEmail);
    }

    public FeedbackSessionAttributes getCopy() {
        return valueOf(toEntity());
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getStartTimeString() {
        if (startTimeUtc == null) {
            return "-";
        }
        return TimeHelper.formatDateTimeForSessions(startTimeUtc, timeZone);
    }

    public String getEndTimeString() {
        if (endTimeUtc == null) {
            return "-";
        }
        return TimeHelper.formatDateTimeForSessions(endTimeUtc, timeZone);
    }

    public String getInstructionsString() {
        if (instructions == null) {
            return null;
        }

        return SanitizationHelper.sanitizeForRichText(instructions.getValue());
    }

    @Override
    public FeedbackSession toEntity() {
        return new FeedbackSession(feedbackSessionName, courseId, creatorEmail, instructions, createdTime,
                                   startTimeUtc, endTimeUtc, sessionVisibleFromTimeUtc, resultsVisibleFromTimeUtc,
                                   timeZone, gracePeriod, feedbackSessionType, sentOpenEmail,
                                   sentClosingEmail, sentClosedEmail, sentPublishedEmail,
                                   isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled,
                                   respondingInstructorList, respondingStudentList);
    }

    @Override
    public String getIdentificationString() {
        return this.feedbackSessionName + "/" + this.courseId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Feedback Session";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, FeedbackSessionAttributes.class);
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        // Check for null fields.

        addNonEmptyError(validator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, feedbackSessionName), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField(FieldValidator.COURSE_ID_FIELD_NAME, courseId), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField("instructions to students", instructions), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField(
                "time for the session to become visible", sessionVisibleFromTimeUtc), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField("creator's email", creatorEmail), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField("session creation time", createdTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(validator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmail(creatorEmail), errors);

        // Skip time frame checks if session type is private.
        if (this.isPrivateSession()) {
            return errors;
        }

        addNonEmptyError(validator.getValidityInfoForNonNullField("submission opening time", startTimeUtc), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField("submission closing time", endTimeUtc), errors);

        addNonEmptyError(validator.getValidityInfoForNonNullField(
                "time for the responses to become visible", resultsVisibleFromTimeUtc), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(validator.getInvalidityInfoForTimeForSessionStartAndEnd(startTimeUtc, endTimeUtc), errors);

        addNonEmptyError(validator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                sessionVisibleFromTimeUtc, startTimeUtc), errors);

        Date actualSessionVisibleFromTime = sessionVisibleFromTimeUtc;

        if (actualSessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            actualSessionVisibleFromTime = startTimeUtc;
        }

        addNonEmptyError(validator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                actualSessionVisibleFromTime, resultsVisibleFromTimeUtc), errors);

        return errors;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    public boolean isClosedAfter(int hours) {
        Date now = new Date();

        long nowMillis = now.getTime();
        long deadlineMillis = endTimeUtc.getTime();
        long differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis) / (60 * 60 * 1000);

        return now.after(startTimeUtc) && differenceBetweenDeadlineAndNow < hours;
    }

    public boolean isClosingWithinTimeLimit(int hours) {
        Date now = new Date();

        long nowMillis = now.getTime();
        long deadlineMillis = endTimeUtc.getTime();
        long differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis) / (60 * 60 * 1000);

        // If now and start are almost similar, it means the feedback session
        // is open for only 24 hours.
        // Hence we do not send a reminder e-mail for feedback session.
        return now.after(startTimeUtc)
               && differenceBetweenDeadlineAndNow >= hours - 1
               && differenceBetweenDeadlineAndNow < hours;
    }

    /**
     * Returns true if the session is closed within the past hour of calling this function.
     */
    public boolean isClosedWithinPastHour() {
        Date date = new Date(endTimeUtc.getTime() + gracePeriod * 60000L);
        return TimeHelper.isWithinPastHourFromNow(date);
    }

    /**
     * Returns {@code true} if it is after the closing time of this feedback session; {@code false} if not.
     */
    public boolean isClosed() {
        if (endTimeUtc == null) {
            return false;
        }

        Date now = new Date();
        Date end = new Date(endTimeUtc.getTime() + gracePeriod * 60000L);

        return now.after(end);
    }

    /**
     * Returns true if the session is currently open and accepting responses.
     */
    public boolean isOpened() {
        if (startTimeUtc == null || endTimeUtc == null) {
            return false;
        }

        Date now = new Date();

        return now.after(startTimeUtc) && now.before(endTimeUtc);
    }

    /**
     * Returns true if the session is currently close but is still accept responses.
     */
    public boolean isInGracePeriod() {
        if (endTimeUtc == null) {
            return false;
        }

        Date now = new Date();
        Date gracedEnd = new Date(endTimeUtc.getTime() + gracePeriod * 60000L);

        return now.after(endTimeUtc) && now.before(gracedEnd);
    }

    /**
     * Returns {@code true} has not opened before and is waiting to open,
     * {@code false} if session has opened before.
     */
    public boolean isWaitingToOpen() {
        if (startTimeUtc == null) {
            return false;
        }

        Date now = new Date();

        return now.before(startTimeUtc);
    }

    /**
     * Returns {@code true} if the session is visible; {@code false} if not.
     *         Does not care if the session has started or not.
     */
    public boolean isVisible() {
        Date visibleTime = this.sessionVisibleFromTimeUtc;

        if (visibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            visibleTime = this.startTimeUtc;
        } else if (visibleTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            return false;
        }

        Date now = new Date();
        return visibleTime.before(now);
    }

    /**
     * Returns {@code true} if the results of the feedback session is visible; {@code false} if not.
     *         Does not care if the session has ended or not.
     */
    public boolean isPublished() {
        Date now = new Date();
        Date publishTime = this.resultsVisibleFromTimeUtc;

        if (publishTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            return isVisible();
        } else if (publishTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return false;
        } else if (publishTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            return false;
        } else if (publishTime.equals(Const.TIME_REPRESENTS_NOW)) {
            return true;
        } else {
            return publishTime.before(now);
        }
    }

    /**
     * Returns {@code true} if the session has been set by the creator to be manually published.
     */
    public boolean isManuallyPublished() {
        return resultsVisibleFromTimeUtc.equals(Const.TIME_REPRESENTS_LATER)
               || resultsVisibleFromTimeUtc.equals(Const.TIME_REPRESENTS_NOW);
    }

    /**
     * Returns {@code true} if session is a private session (only open to the session creator),
     *  {@code false} if not.
     */
    public boolean isPrivateSession() {
        return Const.TIME_REPRESENTS_NEVER.equals(sessionVisibleFromTimeUtc)
               || FeedbackSessionType.PRIVATE.equals(feedbackSessionType);
    }

    public boolean isCreator(String instructorEmail) {
        return creatorEmail.equals(instructorEmail);
    }

    @Override
    public void sanitizeForSaving() {
        this.instructions = SanitizationHelper.sanitizeForRichText(instructions);
    }

    @Override
    public String toString() {
        return "FeedbackSessionAttributes [feedbackSessionName="
               + feedbackSessionName + ", courseId=" + courseId
               + ", creatorEmail=" + creatorEmail + ", instructions=" + instructions
               + ", startTimeUtc=" + startTimeUtc
               + ", endTimeUtc=" + endTimeUtc + ", sessionVisibleFromTimeUtc="
               + sessionVisibleFromTimeUtc + ", resultsVisibleFromTimeUtc="
               + resultsVisibleFromTimeUtc + ", timeZone=" + timeZone
               + ", gracePeriod=" + gracePeriod + ", feedbackSessionType="
               + feedbackSessionType + ", sentOpenEmail=" + sentOpenEmail
               + ", sentPublishedEmail=" + sentPublishedEmail
               + ", isOpeningEmailEnabled=" + isOpeningEmailEnabled
               + ", isClosingEmailEnabled=" + isClosingEmailEnabled
               + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + "]";
    }

    /**
     * Sorts feedback session based courseID (ascending), then by create time (ascending), deadline
     * (ascending), then by start time (ascending), then by feedback session name
     * (ascending). The sort by CourseID part is to cater the case when this
     * method is called with combined feedback sessions from many courses
     */
    public static void sortFeedbackSessionsByCreationTime(List<FeedbackSessionAttributes> sessions) {
        sessions.sort(Comparator.comparing((FeedbackSessionAttributes session) -> session.courseId)
                .thenComparing(session -> session.createdTime)
                .thenComparing(session -> session.endTimeUtc)
                .thenComparing(session -> session.startTimeUtc)
                .thenComparing(session -> session.feedbackSessionName));
    }

    /**
     * Sorts feedback session based on create time (descending), deadline
     * (descending), then by start time (descending),then by courseID (ascending),then by feedback session name
     * (ascending). The sort by CourseID part is to cater the case when this
     * method is called with combined feedback sessions from many courses
     */
    public static void sortFeedbackSessionsByCreationTimeDescending(List<FeedbackSessionAttributes> sessions) {
        sessions.sort(Comparator.comparing((FeedbackSessionAttributes session) ->
                session.createdTime, Comparator.reverseOrder())
                .thenComparing(session -> session.endTimeUtc, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(session -> session.startTimeUtc, Comparator.reverseOrder())
                .thenComparing(session -> session.courseId)
                .thenComparing(session -> session.feedbackSessionName));
    }

    @Override
    public Date getSessionStartTime() {
        return this.startTimeUtc;
    }

    @Override
    public Date getSessionEndTime() {
        return this.endTimeUtc;
    }

    @Override
    public String getSessionName() {
        return this.feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
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

    public Date getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(Date startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public Date getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setEndTimeUtc(Date endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }

    public Date getSessionVisibleFromTimeUtc() {
        return sessionVisibleFromTimeUtc;
    }

    public void setSessionVisibleFromTimeUtc(Date sessionVisibleFromTimeUtc) {
        this.sessionVisibleFromTimeUtc = sessionVisibleFromTimeUtc;
    }

    public Date getResultsVisibleFromTimeUtc() {
        return resultsVisibleFromTimeUtc;
    }

    public void setResultsVisibleFromTimeUtc(Date resultsVisibleFromTimeUtc) {
        this.resultsVisibleFromTimeUtc = resultsVisibleFromTimeUtc;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
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

    public void setOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public void setClosingEmailEnabled(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    public Set<String> getRespondingInstructorList() {
        return respondingInstructorList;
    }

    public void setRespondingInstructorList(Set<String> respondingInstructorList) {
        this.respondingInstructorList = respondingInstructorList;
    }

    public Set<String> getRespondingStudentList() {
        return respondingStudentList;
    }

    public void setRespondingStudentList(Set<String> respondingStudentList) {
        this.respondingStudentList = respondingStudentList;
    }

    public String getEndTimeInIso8601Format() {
        return TimeHelper.formatDateToIso8601Utc(endTimeUtc);
    }

    /**
     * A Builder for {@link FeedbackSessionAttributes}.
     */
    public static class Builder {
        private final FeedbackSessionAttributes feedbackSessionAttributes;

        public Builder(String feedbackSessionName, String courseId, String creatorEmail) {
            feedbackSessionAttributes = new FeedbackSessionAttributes();

            feedbackSessionAttributes.setFeedbackSessionName(feedbackSessionName);
            feedbackSessionAttributes.setCourseId(courseId);
            feedbackSessionAttributes.setCreatorEmail(creatorEmail);
        }

        public Builder withInstructions(Text instructions) {
            Text instructionsToSet = instructions == null
                    ? new Text("")
                    : instructions;
            feedbackSessionAttributes.setInstructions(instructionsToSet);
            return this;
        }

        public Builder withCreatedTime(Date createdTime) {
            if (createdTime != null) {
                feedbackSessionAttributes.setCreatedTime(createdTime);
            }
            return this;
        }

        public Builder withStartTime(Date startTime) {
            if (startTime != null) {
                feedbackSessionAttributes.setStartTimeUtc(startTime);
            }
            return this;
        }

        public Builder withEndTime(Date endTime) {
            if (endTime != null) {
                feedbackSessionAttributes.setEndTimeUtc(endTime);
            }
            return this;
        }

        public Builder withSessionVisibleFromTime(Date sessionVisibleFromTime) {
            if (sessionVisibleFromTime != null) {
                feedbackSessionAttributes.setSessionVisibleFromTimeUtc(sessionVisibleFromTime);
            }
            return this;
        }

        public Builder withResultsVisibleFromTime(Date resultsVisibleFromTime) {
            if (resultsVisibleFromTime != null) {
                feedbackSessionAttributes.setResultsVisibleFromTimeUtc(resultsVisibleFromTime);
            }
            return this;
        }

        public Builder withTimeZone(double timeZone) {
            feedbackSessionAttributes.setTimeZone(timeZone);
            return this;
        }

        public Builder withGracePeriod(int gracePeriod) {
            feedbackSessionAttributes.setGracePeriod(gracePeriod);
            return this;
        }

        public Builder withFeedbackSessionType(FeedbackSessionType feedbackSessionType) {
            if (feedbackSessionType != null) {
                feedbackSessionAttributes.setFeedbackSessionType(feedbackSessionType);
            }
            return this;
        }

        public Builder withSentOpenEmail(boolean sentOpenEmail) {
            feedbackSessionAttributes.setSentOpenEmail(sentOpenEmail);
            return this;
        }

        public Builder withSentClosingEmail(boolean sentClosingEmail) {
            feedbackSessionAttributes.setSentClosingEmail(sentClosingEmail);
            return this;
        }

        public Builder withSentClosedEmail(boolean sentClosedEmail) {
            feedbackSessionAttributes.setSentClosedEmail(sentClosedEmail);
            return this;
        }

        public Builder withSentPublishedEmail(boolean sentPublishedEmail) {
            feedbackSessionAttributes.setSentPublishedEmail(sentPublishedEmail);
            return this;
        }

        public Builder withOpeningEmailEnabled(boolean openingEmailEnabled) {
            feedbackSessionAttributes.setOpeningEmailEnabled(openingEmailEnabled);
            return this;
        }

        public Builder withClosingEmailEnabled(boolean closingEmailEnabled) {
            feedbackSessionAttributes.setClosingEmailEnabled(closingEmailEnabled);
            return this;
        }

        public Builder withPublishedEmailEnabled(boolean publishedEmailEnabled) {
            feedbackSessionAttributes.setPublishedEmailEnabled(publishedEmailEnabled);
            return this;
        }

        public Builder withRespondingInstructorList(Set<String> respondingInstructorList) {
            Set<String> respondingList = respondingInstructorList == null
                    ? new HashSet<String>()
                    : respondingInstructorList;
            feedbackSessionAttributes.setRespondingInstructorList(respondingList);
            return this;
        }

        public Builder withRespondingStudentList(Set<String> respondingStudentList) {
            Set<String> respondingList = respondingStudentList == null
                    ? new HashSet<String>()
                    : respondingStudentList;
            feedbackSessionAttributes.setRespondingStudentList(respondingList);
            return this;
        }

        public FeedbackSessionAttributes build() {
            return feedbackSessionAttributes;
        }
    }
}
