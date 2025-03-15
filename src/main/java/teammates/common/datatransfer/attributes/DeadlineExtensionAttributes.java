package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.sqlentity.Instructor;

/**
 * The data transfer object for {@link DeadlineExtension} entities.
 */
public final class DeadlineExtensionAttributes extends EntityAttributes<DeadlineExtension> {

    private String courseId;
    private String feedbackSessionName;
    private String userEmail;
    private boolean isInstructor;
    private boolean sentClosingSoonEmail;
    private Instant endTime;
    private transient Instant createdAt;
    private transient Instant updatedAt;

    private DeadlineExtensionAttributes(String courseId,
            String feedbackSessionName, String userEmail, boolean isInstructor) {
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.userEmail = userEmail;
        this.isInstructor = isInstructor;
        this.sentClosingSoonEmail = false;
        this.endTime = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
    }

    /**
     * Gets the {@link DeadlineExtensionAttributes} instance of the given {@link DeadlineExtension}.
     */
    public static DeadlineExtensionAttributes valueOf(DeadlineExtension deadlineExtension) {
        DeadlineExtensionAttributes deadlineExtensionAttributes = new DeadlineExtensionAttributes(
                deadlineExtension.getCourseId(),
                deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(),
                deadlineExtension.getIsInstructor());

        deadlineExtensionAttributes.sentClosingSoonEmail = deadlineExtension.getSentClosingSoonEmail();
        deadlineExtensionAttributes.endTime = deadlineExtension.getEndTime();
        deadlineExtensionAttributes.createdAt = deadlineExtension.getCreatedAt();
        deadlineExtensionAttributes.updatedAt = deadlineExtension.getUpdatedAt();

        return deadlineExtensionAttributes;
    }

    /**
     * Gets the {@link DeadlineExtensionAttributes} instance
     * of the given {@link teammates.storage.sqlentity.DeadlineExtension}.
     */
    public static DeadlineExtensionAttributes valueOf(teammates.storage.sqlentity.DeadlineExtension deadlineExtension) {
        DeadlineExtensionAttributes deadlineExtensionAttributes = new DeadlineExtensionAttributes(
                deadlineExtension.getId().toString(),
                deadlineExtension.getFeedbackSession().getName(),
                deadlineExtension.getUser().getEmail(),
                deadlineExtension.getUser() instanceof Instructor);

        deadlineExtensionAttributes.sentClosingSoonEmail = deadlineExtension.isClosingSoonEmailSent();
        deadlineExtensionAttributes.endTime = deadlineExtension.getEndTime();
        deadlineExtensionAttributes.createdAt = deadlineExtension.getCreatedAt();
        deadlineExtensionAttributes.updatedAt = deadlineExtension.getUpdatedAt();

        return deadlineExtensionAttributes;
    }

    /**
     * Returns a builder for {@link DeadlineExtensionAttributes}.
     */
    public static Builder builder(String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        return new Builder(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }

    public boolean getSentClosingSoonEmail() {
        return sentClosingSoonEmail;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(getCourseId()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(getFeedbackSessionName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getUserEmail()), errors);

        return errors;
    }

    @Override
    public DeadlineExtension toEntity() {
        assert getEndTime() != null;

        DeadlineExtension deadlineExtension = new DeadlineExtension(getCourseId(), getFeedbackSessionName(),
                getUserEmail(), getIsInstructor(), getSentClosingSoonEmail(), getEndTime());

        if (!Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP.equals(this.getCreatedAt())) {
            deadlineExtension.setCreatedAt(this.getCreatedAt());
        }

        return deadlineExtension;
    }

    @Override
    public String toString() {
        return "DeadlineExtensionAttributes ["
                + "courseId = " + courseId
                + ", feedbackSessionName = " + feedbackSessionName
                + ", userEmail = " + userEmail
                + ", isInstructor = " + isInstructor
                + "]";
    }

    @Override
    public int hashCode() {
        return (this.courseId + this.feedbackSessionName + this.userEmail + this.isInstructor).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            DeadlineExtensionAttributes otherDeadlineExtension = (DeadlineExtensionAttributes) other;
            return Objects.equals(this.courseId, otherDeadlineExtension.courseId)
                    && Objects.equals(this.feedbackSessionName, otherDeadlineExtension.feedbackSessionName)
                    && Objects.equals(this.userEmail, otherDeadlineExtension.userEmail)
                    && Objects.equals(this.isInstructor, otherDeadlineExtension.isInstructor);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.courseId = SanitizationHelper.sanitizeTitle(courseId);
        this.feedbackSessionName = SanitizationHelper.sanitizeTitle(feedbackSessionName);
        this.userEmail = SanitizationHelper.sanitizeEmail(userEmail);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.endTimeOption.ifPresent(s -> endTime = s);
        updateOptions.sentClosingSoonEmailOption.ifPresent(s -> sentClosingSoonEmail = s);
        updateOptions.newEmailOption.ifPresent(s -> userEmail = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a deadline extension.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        return new UpdateOptions.Builder(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a deadline extension.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(UpdateOptions updateOptions) {
        return new UpdateOptions.Builder(updateOptions);
    }

    /**
     * A builder for {@link DeadlineExtensionAttributes}.
     */
    public static final class Builder extends BasicBuilder<DeadlineExtensionAttributes, Builder> {
        private final DeadlineExtensionAttributes deadlineExtensionAttributes;

        private Builder(String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
            super(new UpdateOptions(courseId, feedbackSessionName, userEmail, isInstructor));
            thisBuilder = this;

            deadlineExtensionAttributes =
                    new DeadlineExtensionAttributes(courseId, feedbackSessionName, userEmail, isInstructor);
        }

        @Override
        public DeadlineExtensionAttributes build() {
            deadlineExtensionAttributes.update(updateOptions);

            return deadlineExtensionAttributes;
        }
    }

    /**
     * Helper class to specify the fields to update in {@link DeadlineExtensionAttributes}.
     */
    public static final class UpdateOptions {
        private final String courseId;
        private final String feedbackSessionName;
        private final String userEmail;
        private final boolean isInstructor;

        private UpdateOption<Instant> endTimeOption = UpdateOption.empty();
        private UpdateOption<Boolean> sentClosingSoonEmailOption = UpdateOption.empty();
        private UpdateOption<String> newEmailOption = UpdateOption.empty();

        private UpdateOptions(String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
            assert courseId != null;
            assert feedbackSessionName != null;
            assert userEmail != null;

            this.courseId = courseId;
            this.feedbackSessionName = feedbackSessionName;
            this.userEmail = userEmail;
            this.isInstructor = isInstructor;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getFeedbackSessionName() {
            return feedbackSessionName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public boolean getIsInstructor() {
            return isInstructor;
        }

        public boolean isEndTimeOptionPresent() {
            return endTimeOption.isPresent();
        }

        public boolean isSentClosingSoonEmailOptionPresent() {
            return sentClosingSoonEmailOption.isPresent();
        }

        @Override
        public String toString() {
            return "DeadlineExtensionAttributes.UpdateOptions ["
                    + "courseId = " + courseId
                    + ", feedbackSessionName = " + feedbackSessionName
                    + ", userEmail = " + userEmail
                    + ", isInstructor = " + isInstructor
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {
            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                thisBuilder = this;
            }

            private Builder(String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
                super(new UpdateOptions(courseId, feedbackSessionName, userEmail, isInstructor));
                thisBuilder = this;
            }

            public Builder withNewEmail(String newEmail) {
                assert newEmail != null;

                updateOptions.newEmailOption = UpdateOption.of(SanitizationHelper.sanitizeEmail(newEmail));
                return thisBuilder;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link DeadlineExtensionAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            assert updateOptions != null;

            this.updateOptions = updateOptions;
        }

        public B withEndTime(Instant endTime) {
            assert endTime != null;

            updateOptions.endTimeOption = UpdateOption.of(endTime);
            return thisBuilder;
        }

        public B withSentClosingSoonEmail(boolean sentClosingSoonEmail) {
            updateOptions.sentClosingSoonEmailOption = UpdateOption.of(sentClosingSoonEmail);
            return thisBuilder;
        }

        public abstract T build();

    }
}
