package teammates.common.datatransfer.attributes;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackSession;

/**
 * The data transfer object for {@link FeedbackSession} entities.
 */
public final class FeedbackSessionAttributes extends EntityAttributes<FeedbackSession> {

    private String feedbackSessionName;
    private String courseId;

    private String creatorEmail;
    private String instructions;
    private Instant createdTime;
    private Instant deletedTime;
    private Instant startTime;
    private Instant endTime;
    private Instant sessionVisibleFromTime;
    private Instant resultsVisibleFromTime;
    private String timeZone;
    private Duration gracePeriod;
    private boolean sentOpeningSoonEmail;
    private boolean sentOpenedEmail;
    private boolean sentClosingSoonEmail;
    private boolean sentClosedEmail;
    private boolean sentPublishedEmail;
    private boolean isOpenedEmailEnabled;
    private boolean isClosingSoonEmailEnabled;
    private boolean isPublishedEmailEnabled;
    private Map<String, Instant> studentDeadlines;
    private Map<String, Instant> instructorDeadlines;

    private transient String userEmail;
    private transient Supplier<Instant> deadlineSupplier;

    private FeedbackSessionAttributes(String feedbackSessionName, String courseId) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;

        this.instructions = "";
        this.createdTime = Instant.now();

        this.isOpenedEmailEnabled = true;
        this.isClosingSoonEmailEnabled = true;
        this.isPublishedEmailEnabled = true;

        this.studentDeadlines = new HashMap<>();
        this.instructorDeadlines = new HashMap<>();

        this.timeZone = Const.DEFAULT_TIME_ZONE;
        this.gracePeriod = Duration.ZERO;

    }

    /**
     * Gets the {@link FeedbackSessionAttributes} instance of the given {@link FeedbackSession}.
     */
    public static FeedbackSessionAttributes valueOf(FeedbackSession fs) {
        FeedbackSessionAttributes feedbackSessionAttributes =
                new FeedbackSessionAttributes(fs.getFeedbackSessionName(), fs.getCourseId());

        feedbackSessionAttributes.creatorEmail = fs.getCreatorEmail();
        if (fs.getInstructions() != null) {
            feedbackSessionAttributes.instructions = fs.getInstructions();
        }
        feedbackSessionAttributes.createdTime = fs.getCreatedTime();
        feedbackSessionAttributes.deletedTime = fs.getDeletedTime();
        feedbackSessionAttributes.startTime = fs.getStartTime();
        feedbackSessionAttributes.endTime = fs.getEndTime();
        feedbackSessionAttributes.sessionVisibleFromTime = fs.getSessionVisibleFromTime();
        feedbackSessionAttributes.resultsVisibleFromTime = fs.getResultsVisibleFromTime();
        feedbackSessionAttributes.timeZone = fs.getTimeZone();
        feedbackSessionAttributes.gracePeriod = Duration.ofMinutes(fs.getGracePeriod());
        feedbackSessionAttributes.sentOpeningSoonEmail = fs.isSentOpeningSoonEmail();
        feedbackSessionAttributes.sentOpenedEmail = fs.isSentOpenedEmail();
        feedbackSessionAttributes.sentClosingSoonEmail = fs.isSentClosingSoonEmail();
        feedbackSessionAttributes.sentClosedEmail = fs.isSentClosedEmail();
        feedbackSessionAttributes.sentPublishedEmail = fs.isSentPublishedEmail();
        feedbackSessionAttributes.isOpenedEmailEnabled = fs.isOpenedEmailEnabled();
        feedbackSessionAttributes.isClosingSoonEmailEnabled = fs.isClosingSoonEmailEnabled();
        feedbackSessionAttributes.isPublishedEmailEnabled = fs.isPublishedEmailEnabled();
        if (fs.getStudentDeadlines() != null) {
            feedbackSessionAttributes.studentDeadlines = fs.getStudentDeadlines();
        }
        if (fs.getInstructorDeadlines() != null) {
            feedbackSessionAttributes.instructorDeadlines = fs.getInstructorDeadlines();
        }

        return feedbackSessionAttributes;
    }

    /**
     * Returns a builder for {@link FeedbackSessionAttributes}.
     */
    public static Builder builder(String feedbackSessionName, String courseId) {
        return new Builder(feedbackSessionName, courseId);
    }

    /**
     * Gets a deep copy of this object.
     */
    public FeedbackSessionAttributes getCopy() {
        return valueOf(toEntity());
    }

    /**
     * Creates a copy that uses the specific deadline for the given student.
     *
     * @param studentEmail The email address of the given student.
     * @return The copy of this object for the given student.
     */
    public FeedbackSessionAttributes getCopyForStudent(String studentEmail) {
        FeedbackSessionAttributes copy = getCopy();
        copy.deadlineSupplier = () -> copy.studentDeadlines.getOrDefault(studentEmail, endTime);
        copy.userEmail = studentEmail;
        return copy;
    }

    /**
     * Creates a copy that uses the specific deadline for the given instructor.
     *
     * @param instructorEmail The email address of the given instructor.
     * @return The copy of this object for the given instructor.
     */
    public FeedbackSessionAttributes getCopyForInstructor(String instructorEmail) {
        FeedbackSessionAttributes copy = getCopy();
        copy.deadlineSupplier = () -> copy.instructorDeadlines.getOrDefault(instructorEmail, endTime);
        copy.userEmail = instructorEmail;
        return copy;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    /**
     * Gets the instructions of the feedback session.
     */
    public String getInstructionsString() {
        if (instructions == null) {
            return null;
        }

        return SanitizationHelper.sanitizeForRichText(instructions);
    }

    @Override
    public FeedbackSession toEntity() {
        return new FeedbackSession(feedbackSessionName, courseId, creatorEmail, instructions,
                createdTime, deletedTime, startTime, endTime, sessionVisibleFromTime, resultsVisibleFromTime,
                timeZone, getGracePeriodMinutes(),
                sentOpeningSoonEmail, sentOpenedEmail, sentClosingSoonEmail, sentClosedEmail, sentPublishedEmail,
                isOpenedEmailEnabled, isClosingSoonEmailEnabled, isPublishedEmailEnabled, new HashMap<>(studentDeadlines),
                new HashMap<>(instructorDeadlines));
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.COURSE_ID_FIELD_NAME, courseId), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("instructions to students", instructions), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the session to become visible", sessionVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("session time zone", timeZone), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("creator's email", creatorEmail), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("session creation time", createdTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(creatorEmail), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForGracePeriod(gracePeriod), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission opening time", startTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("submission closing time", endTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the responses to become visible", resultsVisibleFromTime), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionStartAndEnd(startTime, endTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
                sessionVisibleFromTime, startTime), errors);

        Instant actualSessionVisibleFromTime = sessionVisibleFromTime;

        if (actualSessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            actualSessionVisibleFromTime = startTime;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
                actualSessionVisibleFromTime, resultsVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                endTime, studentDeadlines), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                endTime, instructorDeadlines), errors);

        return errors;
    }

    /**
     * Finds the point in time when the session is considered closed, excluding the grace period.
     * <p>This varies depending on who is looking at the session:</p>
     * <ul>
     *     <li>For instructors looking at the session in full detail, this is when the end time is reached.</li>
     *     <li>For participants, this is when the end time is reached, or their extension deadline, if it exists.</li>
     * </ul>
     */
    public Instant getDeadline() {
        if (deadlineSupplier == null) {
            return endTime;
        }
        return deadlineSupplier.get();
    }

    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Returns true if session's start time is opening from now to anytime before
     * now() + the specific number of {@param hours} supplied in the argument.
     */
    public boolean isOpeningInHours(long hours) {
        return startTime.isAfter(Instant.now())
                && Instant.now().plus(Duration.ofHours(hours)).isAfter(startTime);
    }

    /**
     * Returns true if the feedback session is closed after the number of specified hours.
     */
    public boolean isClosedAfter(long hours) {
        return Instant.now().plus(Duration.ofHours(hours)).isAfter(getDeadline());
    }

    /**
     * Returns true if the feedback session is closing (almost closed) after the number of specified hours.
     */
    public boolean isClosingWithinTimeLimit(long hours) {
        Instant now = Instant.now();
        Duration difference = Duration.between(now, getDeadline());
        // If now and start are almost similar, it means the feedback session
        // is open for only 24 hours.
        // Hence we do not send a reminder e-mail for feedback session.
        return now.isAfter(startTime)
               && difference.compareTo(Duration.ofHours(hours - 1)) >= 0
               && difference.compareTo(Duration.ofHours(hours)) < 0;
    }

    /**
     * Returns true if the feedback session opens after the number of specified hours.
     */
    public boolean isOpeningWithinTimeLimit(long hours) {
        Instant now = Instant.now();
        Duration difference = Duration.between(now, startTime);

        return now.isBefore(startTime)
                && difference.compareTo(Duration.ofHours(hours - 1)) >= 0
                && difference.compareTo(Duration.ofHours(hours)) < 0;
    }

    /**
     * Checks if the session closed some time in the last one hour from calling this function.
     *
     * @return true if the session closed within the past hour; false otherwise.
     */
    public boolean isClosedWithinPastHour() {
        Instant now = Instant.now();
        Instant given = getDeadline().plus(gracePeriod);
        return given.isBefore(now) && Duration.between(given, now).compareTo(Duration.ofHours(1)) < 0;
    }

    /**
     * Checks if the feedback session is closed.
     * This occurs when the current time is after both the deadline and the grace period.
     */
    public boolean isClosed() {
        return Instant.now().isAfter(getDeadline().plus(gracePeriod));
    }

    /**
     * Checks if the feedback session is open.
     * This occurs when the current time is either the start time or later but before the deadline.
     */
    public boolean isOpened() {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(getDeadline());
    }

    /**
     * Checks if the feedback session is closed but still accepts responses.
     * This occurs when the current time is either the deadline or later but still within the grace period.
     */
    public boolean isInGracePeriod() {
        Instant now = Instant.now();
        Instant deadline = getDeadline();
        Instant gracedEnd = deadline.plus(gracePeriod);
        return (now.isAfter(deadline) || now.equals(deadline)) && (now.isBefore(gracedEnd) || now.equals(gracedEnd));
    }

    /**
     * Returns {@code true} has not opened before and is waiting to open,
     * {@code false} if session has opened before.
     */
    public boolean isWaitingToOpen() {
        return Instant.now().isBefore(startTime);
    }

    /**
     * Returns {@code true} if the session is visible; {@code false} if not.
     *         Does not care if the session has started or not.
     */
    public boolean isVisible() {
        Instant visibleTime = this.sessionVisibleFromTime;

        if (visibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            visibleTime = this.startTime;
        }

        Instant now = Instant.now();
        return now.isAfter(visibleTime) || now.equals(visibleTime);
    }

    /**
     * Returns {@code true} if the results of the feedback session is visible; {@code false} if not.
     *         Does not care if the session has ended or not.
     */
    public boolean isPublished() {
        Instant publishTime = this.resultsVisibleFromTime;

        if (publishTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            return isVisible();
        }
        if (publishTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return false;
        }
        if (publishTime.equals(Const.TIME_REPRESENTS_NOW)) {
            return true;
        }

        Instant now = Instant.now();
        return now.isAfter(publishTime) || now.equals(publishTime);
    }

    /**
     * Returns true if the given email is the same as the creator email of the feedback session.
     */
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
               + ", createdTime=" + createdTime + ", deletedTime=" + deletedTime
               + ", startTime=" + startTime
               + ", endTime=" + endTime + ", sessionVisibleFromTime="
               + sessionVisibleFromTime + ", resultsVisibleFromTime="
               + resultsVisibleFromTime + ", timeZone=" + timeZone
               + ", gracePeriod=" + getGracePeriodMinutes() + "min"
               + ", sentOpeningSoonEmail=" + sentOpeningSoonEmail
               + ", sentOpenedEmail=" + sentOpenedEmail
               + ", sentClosingSoonEmail=" + sentClosingSoonEmail
               + ", sentClosedEmail=" + sentClosedEmail
               + ", sentPublishedEmail=" + sentPublishedEmail
               + ", isOpenedEmailEnabled=" + isOpenedEmailEnabled
               + ", isClosingSoonEmailEnabled=" + isClosingSoonEmailEnabled
               + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled
               + ", studentDeadlines=" + new TreeMap<>(studentDeadlines)
               + ", instructorDeadlines=" + new TreeMap<>(instructorDeadlines)
               + "]";
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.feedbackSessionName).append(this.courseId)
                .append(this.instructions).append(this.creatorEmail);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSessionAttributes otherFeedbackSession = (FeedbackSessionAttributes) other;
            return Objects.equals(this.feedbackSessionName, otherFeedbackSession.feedbackSessionName)
                    && Objects.equals(this.courseId, otherFeedbackSession.courseId)
                    && Objects.equals(this.instructions, otherFeedbackSession.instructions)
                    && Objects.equals(this.creatorEmail, otherFeedbackSession.creatorEmail);
        } else {
            return false;
        }
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

    public boolean isSessionDeleted() {
        return this.deletedTime != null;
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

    public long getGracePeriodMinutes() {
        return gracePeriod.toMinutes();
    }

    public void setGracePeriodMinutes(long gracePeriodMinutes) {
        this.gracePeriod = Duration.ofMinutes(gracePeriodMinutes);
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

    public void setOpenedEmailEnabled(boolean isOpenedEmailEnabled) {
        this.isOpenedEmailEnabled = isOpenedEmailEnabled;
    }

    public boolean isClosingSoonEmailEnabled() {
        return isClosingSoonEmailEnabled;
    }

    public void setClosingSoonEmailEnabled(boolean isClosingSoonEmailEnabled) {
        this.isClosingSoonEmailEnabled = isClosingSoonEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
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

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.instructionsOption.ifPresent(s -> instructions = s);
        updateOptions.startTimeOption.ifPresent(s -> startTime = s);
        updateOptions.endTimeOption.ifPresent(s -> endTime = s);
        updateOptions.sessionVisibleFromTimeOption.ifPresent(s -> sessionVisibleFromTime = s);
        updateOptions.resultsVisibleFromTimeOption.ifPresent(s -> resultsVisibleFromTime = s);
        updateOptions.timeZoneOption.ifPresent(s -> timeZone = s);
        updateOptions.gracePeriodOption.ifPresent(s -> gracePeriod = s);
        updateOptions.sentOpeningSoonEmailOption.ifPresent(s -> sentOpeningSoonEmail = s);
        updateOptions.sentOpenedEmailOption.ifPresent(s -> sentOpenedEmail = s);
        updateOptions.sentClosingSoonEmailOption.ifPresent(s -> sentClosingSoonEmail = s);
        updateOptions.sentClosedEmailOption.ifPresent(s -> sentClosedEmail = s);
        updateOptions.sentPublishedEmailOption.ifPresent(s -> sentPublishedEmail = s);
        updateOptions.isClosingSoonEmailEnabledOption.ifPresent(s -> isClosingSoonEmailEnabled = s);
        updateOptions.isPublishedEmailEnabledOption.ifPresent(s -> isPublishedEmailEnabled = s);
        updateOptions.studentDeadlinesOption.ifPresent(s -> studentDeadlines = s);
        updateOptions.instructorDeadlinesOption.ifPresent(s -> instructorDeadlines = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a session.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String feedbackSessionName, String courseId) {
        return new UpdateOptions.Builder(feedbackSessionName, courseId);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build on top of {@code updateOptions}.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(UpdateOptions updateOptions) {
        return new UpdateOptions.Builder(updateOptions);
    }

    /**
     * A builder for {@link FeedbackSessionAttributes}.
     */
    public static final class Builder extends BasicBuilder<FeedbackSessionAttributes, Builder> {
        private final FeedbackSessionAttributes feedbackSessionAttributes;

        private Builder(String feedbackSessionName, String courseId) {
            super(new UpdateOptions(feedbackSessionName, courseId));
            thisBuilder = this;

            feedbackSessionAttributes = new FeedbackSessionAttributes(feedbackSessionName, courseId);
        }

        public Builder withCreatorEmail(String creatorEmail) {
            assert creatorEmail != null;

            feedbackSessionAttributes.creatorEmail = creatorEmail;

            return this;
        }

        @Override
        public FeedbackSessionAttributes build() {
            feedbackSessionAttributes.update(updateOptions);

            return feedbackSessionAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackSessionAttributes}.
     */
    public static final class UpdateOptions {
        private String courseId;
        private String feedbackSessionName;

        private UpdateOption<String> instructionsOption = UpdateOption.empty();
        private UpdateOption<Instant> startTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> endTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> sessionVisibleFromTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> resultsVisibleFromTimeOption = UpdateOption.empty();
        private UpdateOption<String> timeZoneOption = UpdateOption.empty();
        private UpdateOption<Duration> gracePeriodOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentOpeningSoonEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentOpenedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosingSoonEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentPublishedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> isClosingSoonEmailEnabledOption = UpdateOption.empty();
        private UpdateOption<Boolean> isPublishedEmailEnabledOption = UpdateOption.empty();
        private UpdateOption<Map<String, Instant>> studentDeadlinesOption = UpdateOption.empty();
        private UpdateOption<Map<String, Instant>> instructorDeadlinesOption = UpdateOption.empty();

        private UpdateOptions(String feedbackSessionName, String courseId) {
            assert feedbackSessionName != null;
            assert courseId != null;

            this.feedbackSessionName = feedbackSessionName;
            this.courseId = courseId;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getFeedbackSessionName() {
            return feedbackSessionName;
        }

        @Override
        public String toString() {
            return "FeedbackSessionAttributes.UpdateOptions ["
                    + "feedbackSessionName = " + feedbackSessionName
                    + ", courseId = " + courseId
                    + ", instructions = " + instructionsOption
                    + ", startTime = " + startTimeOption
                    + ", endTime = " + endTimeOption
                    + ", sessionVisibleFromTime = " + sessionVisibleFromTimeOption
                    + ", resultsVisibleFromTime = " + resultsVisibleFromTimeOption
                    + ", timeZone = " + timeZoneOption
                    + ", gracePeriod = " + gracePeriodOption
                    + ", sentOpeningSoonEmail = " + sentOpeningSoonEmailOption
                    + ", sentOpenedEmail = " + sentOpenedEmailOption
                    + ", sentClosingSoonEmail = " + sentClosingSoonEmailOption
                    + ", sentClosedEmail = " + sentClosedEmailOption
                    + ", sentPublishedEmail = " + sentPublishedEmailOption
                    + ", isClosingSoonEmailEnabled = " + isClosingSoonEmailEnabledOption
                    + ", isPublishedEmailEnabled = " + isPublishedEmailEnabledOption
                    + ", studentDeadlines = " + studentDeadlinesOption
                    + ", instructorDeadlines = " + instructorDeadlinesOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                assert updateOptions != null;
                thisBuilder = this;
            }

            private Builder(String feedbackSessionName, String courseId) {
                super(new UpdateOptions(feedbackSessionName, courseId));
                thisBuilder = this;
            }

            public Builder withSentOpeningSoonEmail(boolean sentOpeningSoonEmailOption) {
                updateOptions.sentOpeningSoonEmailOption = UpdateOption.of(sentOpeningSoonEmailOption);
                return this;
            }

            public Builder withSentOpenedEmail(boolean sentOpenedEmail) {
                updateOptions.sentOpenedEmailOption = UpdateOption.of(sentOpenedEmail);
                return this;
            }

            public Builder withSentClosingSoonEmail(boolean sentClosingSoonEmail) {
                updateOptions.sentClosingSoonEmailOption = UpdateOption.of(sentClosingSoonEmail);
                return this;
            }

            public Builder withSentClosedEmail(boolean sentClosedEmail) {
                updateOptions.sentClosedEmailOption = UpdateOption.of(sentClosedEmail);
                return this;
            }

            public Builder withSentPublishedEmail(boolean sentPublishedEmail) {
                updateOptions.sentPublishedEmailOption = UpdateOption.of(sentPublishedEmail);
                return this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link FeedbackSessionAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withInstructions(String instruction) {
            assert instruction != null;

            updateOptions.instructionsOption = UpdateOption.of(instruction);
            return thisBuilder;
        }

        public B withStartTime(Instant startTime) {
            assert startTime != null;

            updateOptions.startTimeOption = UpdateOption.of(startTime);
            return thisBuilder;
        }

        public B withEndTime(Instant endTime) {
            assert endTime != null;

            updateOptions.endTimeOption = UpdateOption.of(endTime);
            return thisBuilder;
        }

        public B withSessionVisibleFromTime(Instant sessionVisibleFromTime) {
            assert sessionVisibleFromTime != null;

            updateOptions.sessionVisibleFromTimeOption = UpdateOption.of(sessionVisibleFromTime);
            return thisBuilder;
        }

        public B withResultsVisibleFromTime(Instant resultsVisibleFromTime) {
            assert resultsVisibleFromTime != null;

            updateOptions.resultsVisibleFromTimeOption = UpdateOption.of(resultsVisibleFromTime);
            return thisBuilder;
        }

        public B withTimeZone(String timeZone) {
            assert timeZone != null;

            updateOptions.timeZoneOption = UpdateOption.of(timeZone);
            return thisBuilder;
        }

        public B withGracePeriod(Duration gracePeriod) {
            assert gracePeriod != null;

            updateOptions.gracePeriodOption = UpdateOption.of(gracePeriod);
            return thisBuilder;
        }

        public B withIsClosingSoonEmailEnabled(boolean isClosingSoonEmailEnabled) {
            updateOptions.isClosingSoonEmailEnabledOption = UpdateOption.of(isClosingSoonEmailEnabled);
            return thisBuilder;
        }

        public B withIsPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
            updateOptions.isPublishedEmailEnabledOption = UpdateOption.of(isPublishedEmailEnabled);
            return thisBuilder;
        }

        public B withStudentDeadlines(Map<String, Instant> studentDeadlines) {
            assert studentDeadlines != null;

            updateOptions.studentDeadlinesOption = UpdateOption.of(studentDeadlines);
            return thisBuilder;
        }

        public B withInstructorDeadlines(Map<String, Instant> instructorDeadlines) {
            assert instructorDeadlines != null;

            updateOptions.instructorDeadlinesOption = UpdateOption.of(instructorDeadlines);
            return thisBuilder;
        }

        public abstract T build();

    }
}
