package teammates.storage.sqlentity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "FeedbackSessions")
public class FeedbackSession extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String creatorEmail;

    @Column(nullable = false)
    private String instructions;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private Instant sessionVisibleFromTime;

    @Column(nullable = false)
    private Instant resultsVisibleFromTime;

    @Column(nullable = false)
    @Convert(converter = DurationLongConverter.class)
    private Duration gracePeriod;

    @Column(nullable = false)
    private boolean isOpeningEmailEnabled;

    @Column(nullable = false)
    private boolean isClosingEmailEnabled;

    @Column(nullable = false)
    private boolean isPublishedEmailEnabled;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    protected FeedbackSession() {
        // required by Hibernate
    }

    private FeedbackSession(FeedbackSessionBuilder builder) {
        this.setName(builder.name);
        this.setCourse(builder.course);

        this.setCreatorEmail(builder.creatorEmail);
        this.setInstructions(StringUtils.defaultString(builder.instructions));
        this.setStartTime(builder.startTime);
        this.setEndTime(builder.endTime);
        this.setSessionVisibleFromTime(builder.sessionVisibleFromTime);
        this.setResultsVisibleFromTime(builder.resultsVisibleFromTime);
        this.setGracePeriod(Objects.requireNonNullElse(builder.gracePeriod, Duration.ZERO));
        this.setOpeningEmailEnabled(builder.isOpeningEmailEnabled);
        this.setClosingEmailEnabled(builder.isClosingEmailEnabled);
        this.setPublishedEmailEnabled(builder.isPublishedEmailEnabled);

        if (builder.deletedAt != null) {
            this.setDeletedAt(builder.deletedAt);
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.instructions = SanitizationHelper.sanitizeForRichText(instructions);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, name), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.COURSE_ID_FIELD_NAME, course), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("instructions to students", instructions),
                errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                "time for the session to become visible", sessionVisibleFromTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("creator's email", creatorEmail), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(name), errors);

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

        // TODO: add once extended dealines added to entity
        // addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
        // endTime, studentDeadlines), errors);

        // addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
        // endTime, instructorDeadlines), errors);

        return errors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Duration getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Duration gracePeriod) {
        this.gracePeriod = gracePeriod;
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

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "FeedbackSession [id=" + id + ", course=" + course + ", name=" + name + ", creatorEmail=" + creatorEmail
                + ", instructions=" + instructions + ", startTime=" + startTime + ", endTime=" + endTime
                + ", sessionVisibleFromTime=" + sessionVisibleFromTime + ", resultsVisibleFromTime="
                + resultsVisibleFromTime + ", gracePeriod=" + gracePeriod + ", isOpeningEmailEnabled="
                + isOpeningEmailEnabled + ", isClosingEmailEnabled=" + isClosingEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + ", deletedAt=" + deletedAt + "]";
    }

    /**
     * Builder for FeedbackSession.
     */
    public static class FeedbackSessionBuilder {
        private String name;
        private Course course;

        private String creatorEmail;
        private String instructions;
        private Instant startTime;
        private Instant endTime;
        private Instant sessionVisibleFromTime;
        private Instant resultsVisibleFromTime;
        private Duration gracePeriod;
        private boolean isOpeningEmailEnabled;
        private boolean isClosingEmailEnabled;
        private boolean isPublishedEmailEnabled;
        private Instant deletedAt;

        public FeedbackSessionBuilder(String name) {
            this.name = name;
        }

        public FeedbackSessionBuilder withCourse(Course course) {
            this.course = course;
            return this;
        }

        public FeedbackSessionBuilder withCreatorEmail(String creatorEmail) {
            this.creatorEmail = creatorEmail;
            return this;
        }

        public FeedbackSessionBuilder withInstructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        public FeedbackSessionBuilder withStartTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public FeedbackSessionBuilder withEndTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public FeedbackSessionBuilder withSessionVisibleFromTime(Instant sessionVisibleFromTime) {
            this.sessionVisibleFromTime = sessionVisibleFromTime;
            return this;
        }

        public FeedbackSessionBuilder withResultsVisibleFromTime(Instant resultsVisibleFromTime) {
            this.resultsVisibleFromTime = resultsVisibleFromTime;
            return this;
        }

        public FeedbackSessionBuilder withGracePeriod(Duration gracePeriod) {
            this.gracePeriod = gracePeriod;
            return this;
        }

        public FeedbackSessionBuilder withOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
            this.isOpeningEmailEnabled = isOpeningEmailEnabled;
            return this;
        }

        public FeedbackSessionBuilder withClosingEmailEnabled(boolean isClosingEmailEnabled) {
            this.isClosingEmailEnabled = isClosingEmailEnabled;
            return this;
        }

        public FeedbackSessionBuilder withPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
            this.isPublishedEmailEnabled = isPublishedEmailEnabled;
            return this;
        }

        public FeedbackSessionBuilder withDeletedAt(Instant deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public FeedbackSession build() {
            return new FeedbackSession(this);
        }
    }
}
