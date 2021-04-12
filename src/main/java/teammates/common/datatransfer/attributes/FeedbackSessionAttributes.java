package teammates.common.datatransfer.attributes;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackSession;

public class FeedbackSessionAttributes extends EntityAttributes<FeedbackSession> {

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
    private ZoneId timeZone;
    private Duration gracePeriod;
    private boolean sentOpenEmail;
    private boolean sentClosingEmail;
    private boolean sentClosedEmail;
    private boolean sentPublishedEmail;
    private boolean isOpeningEmailEnabled;
    private boolean isClosingEmailEnabled;
    private boolean isPublishedEmailEnabled;

    private FeedbackSessionAttributes(String feedbackSessionName, String courseId) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;

        this.instructions = "";
        this.createdTime = Instant.now();

        this.isOpeningEmailEnabled = true;
        this.isClosingEmailEnabled = true;
        this.isPublishedEmailEnabled = true;

        this.timeZone = Const.DEFAULT_TIME_ZONE;
        this.gracePeriod = Duration.ZERO;

    }

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
        feedbackSessionAttributes.timeZone = ZoneId.of(fs.getTimeZone());
        feedbackSessionAttributes.gracePeriod = Duration.ofMinutes(fs.getGracePeriod());
        feedbackSessionAttributes.sentOpenEmail = fs.isSentOpenEmail();
        feedbackSessionAttributes.sentClosingEmail = fs.isSentClosingEmail();
        feedbackSessionAttributes.sentClosedEmail = fs.isSentClosedEmail();
        feedbackSessionAttributes.sentPublishedEmail = fs.isSentPublishedEmail();
        feedbackSessionAttributes.isOpeningEmailEnabled = fs.isOpeningEmailEnabled();
        feedbackSessionAttributes.isClosingEmailEnabled = fs.isClosingEmailEnabled();
        feedbackSessionAttributes.isPublishedEmailEnabled = fs.isPublishedEmailEnabled();

        return feedbackSessionAttributes;
    }

    /**
     * Returns a builder for {@link FeedbackSessionAttributes}.
     */
    public static Builder builder(String feedbackSessionName, String courseId) {
        return new Builder(feedbackSessionName, courseId);
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
                timeZone.getId(), getGracePeriodMinutes(),
                sentOpenEmail, sentClosingEmail, sentClosedEmail, sentPublishedEmail,
                isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled);
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

        return errors;
    }

    public boolean isClosedAfter(long hours) {
        return Instant.now().plus(Duration.ofHours(hours)).isAfter(endTime);
    }

    public boolean isClosingWithinTimeLimit(long hours) {
        Instant now = Instant.now();
        Duration difference = Duration.between(now, endTime);
        // If now and start are almost similar, it means the feedback session
        // is open for only 24 hours.
        // Hence we do not send a reminder e-mail for feedback session.
        return now.isAfter(startTime)
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
        Instant given = endTime.plus(gracePeriod);
        return given.isBefore(now) && Duration.between(given, now).compareTo(Duration.ofHours(1)) < 0;
    }

    /**
     * Returns {@code true} if it is after the closing time of this feedback session; {@code false} if not.
     */
    public boolean isClosed() {
        return Instant.now().isAfter(endTime.plus(gracePeriod));
    }

    /**
     * Returns true if the session is currently open and accepting responses.
     */
    public boolean isOpened() {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(endTime);
    }

    /**
     * Returns true if the session is currently close but is still accept responses.
     */
    public boolean isInGracePeriod() {
        Instant now = Instant.now();
        Instant gracedEnd = endTime.plus(gracePeriod);
        return (now.isAfter(endTime) || now.equals(endTime)) && (now.isBefore(gracedEnd) || now.equals(gracedEnd));
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
               + ", startTime=" + startTime
               + ", endTime=" + endTime + ", sessionVisibleFromTime="
               + sessionVisibleFromTime + ", resultsVisibleFromTime="
               + resultsVisibleFromTime + ", timeZone=" + timeZone
               + ", gracePeriod=" + getGracePeriodMinutes() + "min"
               + ", sentOpenEmail=" + sentOpenEmail
               + ", sentPublishedEmail=" + sentPublishedEmail
               + ", isOpeningEmailEnabled=" + isOpeningEmailEnabled
               + ", isClosingEmailEnabled=" + isClosingEmailEnabled
               + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + "]";
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

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public long getGracePeriodMinutes() {
        return gracePeriod.toMinutes();
    }

    public void setGracePeriodMinutes(long gracePeriodMinutes) {
        this.gracePeriod = Duration.ofMinutes(gracePeriodMinutes);
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
        updateOptions.sentOpenEmailOption.ifPresent(s -> sentOpenEmail = s);
        updateOptions.sentClosingEmailOption.ifPresent(s -> sentClosingEmail = s);
        updateOptions.sentClosedEmailOption.ifPresent(s -> sentClosedEmail = s);
        updateOptions.sentPublishedEmailOption.ifPresent(s -> sentPublishedEmail = s);
        updateOptions.isClosingEmailEnabledOption.ifPresent(s -> isClosingEmailEnabled = s);
        updateOptions.isPublishedEmailEnabledOption.ifPresent(s -> isPublishedEmailEnabled = s);
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
    public static class Builder extends BasicBuilder<FeedbackSessionAttributes, Builder> {
        private final FeedbackSessionAttributes feedbackSessionAttributes;

        private Builder(String feedbackSessionName, String courseId) {
            super(new UpdateOptions(feedbackSessionName, courseId));
            thisBuilder = this;

            feedbackSessionAttributes = new FeedbackSessionAttributes(feedbackSessionName, courseId);
        }

        public Builder withCreatorEmail(String creatorEmail) {
            Assumption.assertNotNull(creatorEmail);

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
    public static class UpdateOptions {
        private String courseId;
        private String feedbackSessionName;

        private UpdateOption<String> instructionsOption = UpdateOption.empty();
        private UpdateOption<Instant> startTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> endTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> sessionVisibleFromTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> resultsVisibleFromTimeOption = UpdateOption.empty();
        private UpdateOption<ZoneId> timeZoneOption = UpdateOption.empty();
        private UpdateOption<Duration> gracePeriodOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentOpenEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosingEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentPublishedEmailOption = UpdateOption.empty();
        private UpdateOption<Boolean> isClosingEmailEnabledOption = UpdateOption.empty();
        private UpdateOption<Boolean> isPublishedEmailEnabledOption = UpdateOption.empty();

        private UpdateOptions(String feedbackSessionName, String courseId) {
            Assumption.assertNotNull(feedbackSessionName);
            Assumption.assertNotNull(courseId);

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
            return "StudentAttributes.UpdateOptions ["
                    + "feedbackSessionName = " + feedbackSessionName
                    + ", courseId = " + courseId
                    + ", instructions = " + instructionsOption
                    + ", startTime = " + startTimeOption
                    + ", endTime = " + endTimeOption
                    + ", sessionVisibleFromTime = " + sessionVisibleFromTimeOption
                    + ", resultsVisibleFromTime = " + resultsVisibleFromTimeOption
                    + ", timeZone = " + timeZoneOption
                    + ", gracePeriod = " + gracePeriodOption
                    + ", sentOpenEmail = " + sentOpenEmailOption
                    + ", sentClosingEmail = " + sentClosingEmailOption
                    + ", sentClosedEmail = " + sentClosedEmailOption
                    + ", sentPublishedEmail = " + sentPublishedEmailOption
                    + ", isClosingEmailEnabled = " + isClosingEmailEnabledOption
                    + ", isPublishedEmailEnabled = " + isPublishedEmailEnabledOption
                    + "]";
        }

        /**
         * Represents the change of email for an(a) instructor/student.
         */
        private static class EmailChange {

            private String oldEmail;
            private String newEmail;

            private EmailChange(String oldEmail, String newEmail) {
                this.oldEmail = oldEmail;
                this.newEmail = newEmail;
            }

            private String getOldEmail() {
                return oldEmail;
            }

            private String getNewEmail() {
                return newEmail;
            }
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                Assumption.assertNotNull(updateOptions);
                thisBuilder = this;
            }

            private Builder(String feedbackSessionName, String courseId) {
                super(new UpdateOptions(feedbackSessionName, courseId));
                thisBuilder = this;
            }

            public Builder withSentOpenEmail(boolean sentOpenEmail) {
                updateOptions.sentOpenEmailOption = UpdateOption.of(sentOpenEmail);
                return this;
            }

            public Builder withSentClosingEmail(boolean sentClosingEmail) {
                updateOptions.sentClosingEmailOption = UpdateOption.of(sentClosingEmail);
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
            Assumption.assertNotNull(instruction);

            updateOptions.instructionsOption = UpdateOption.of(instruction);
            return thisBuilder;
        }

        public B withStartTime(Instant startTime) {
            Assumption.assertNotNull(startTime);

            updateOptions.startTimeOption = UpdateOption.of(startTime);
            return thisBuilder;
        }

        public B withEndTime(Instant endTime) {
            Assumption.assertNotNull(endTime);

            updateOptions.endTimeOption = UpdateOption.of(endTime);
            return thisBuilder;
        }

        public B withSessionVisibleFromTime(Instant sessionVisibleFromTime) {
            Assumption.assertNotNull(sessionVisibleFromTime);

            updateOptions.sessionVisibleFromTimeOption = UpdateOption.of(sessionVisibleFromTime);
            return thisBuilder;
        }

        public B withResultsVisibleFromTime(Instant resultsVisibleFromTime) {
            Assumption.assertNotNull(resultsVisibleFromTime);

            updateOptions.resultsVisibleFromTimeOption = UpdateOption.of(resultsVisibleFromTime);
            return thisBuilder;
        }

        public B withTimeZone(ZoneId timeZone) {
            Assumption.assertNotNull(timeZone);

            updateOptions.timeZoneOption = UpdateOption.of(timeZone);
            return thisBuilder;
        }

        public B withGracePeriod(Duration gracePeriod) {
            Assumption.assertNotNull(gracePeriod);

            updateOptions.gracePeriodOption = UpdateOption.of(gracePeriod);
            return thisBuilder;
        }

        public B withIsClosingEmailEnabled(boolean isClosingEmailEnabled) {
            updateOptions.isClosingEmailEnabledOption = UpdateOption.of(isClosingEmailEnabled);
            return thisBuilder;
        }

        public B withIsPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
            updateOptions.isPublishedEmailEnabledOption = UpdateOption.of(isPublishedEmailEnabled);
            return thisBuilder;
        }

        public abstract T build();

    }
}
