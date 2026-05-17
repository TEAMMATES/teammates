package teammates.storage.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "FeedbackSessions", uniqueConstraints = @UniqueConstraint(columnNames = {"courseId", "name"}))
public class FeedbackSession extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false, insertable = false, updatable = false)
    private String courseId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String creatorEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
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
    private boolean isClosingSoonEmailEnabled;

    @Column(nullable = false)
    private boolean isPublishedEmailEnabled;

    @Column(nullable = false)
    private boolean isOpeningSoonEmailSent;

    @Column(nullable = false)
    private boolean isOpenedEmailSent;

    @Column(nullable = false)
    private boolean isClosingSoonEmailSent;

    @Column(nullable = false)
    private boolean isClosedEmailSent;

    @Column(nullable = false)
    private boolean isPublishedEmailSent;

    @OneToMany(mappedBy = "feedbackSession")
    private Set<DeadlineExtension> deadlineExtensions = new HashSet<>();

    @OneToMany(mappedBy = "feedbackSession")
    private Set<FeedbackQuestion> feedbackQuestions = new HashSet<>();

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    protected FeedbackSession() {
        // required by Hibernate
    }

    public FeedbackSession(String name, String creatorEmail, String instructions, Instant startTime,
            Instant endTime, Instant sessionVisibleFromTime, Instant resultsVisibleFromTime, Duration gracePeriod,
            boolean isClosingSoonEmailEnabled, boolean isPublishedEmailEnabled) {
        this.setId(UUID.randomUUID());
        this.setName(name);
        this.setCreatorEmail(creatorEmail);
        this.setInstructions(StringUtils.defaultString(instructions));
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setSessionVisibleFromTime(sessionVisibleFromTime);
        this.setResultsVisibleFromTime(resultsVisibleFromTime);
        this.setGracePeriod(gracePeriod);
        this.setClosingSoonEmailEnabled(isClosingSoonEmailEnabled);
        this.setPublishedEmailEnabled(isPublishedEmailEnabled);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, name), errors);

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

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                endTime, deadlineExtensions), errors);

        return errors;
    }

    /**
     * Adds a feedback question to the feedback session.
     */
    public void addFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
        this.feedbackQuestions.add(feedbackQuestion);
        feedbackQuestion.setFeedbackSession(this);
    }

    /**
     * Adds a deadline extension to the feedback session.
     */
    public void addDeadlineExtension(DeadlineExtension deadlineExtension) {
        this.deadlineExtensions.add(deadlineExtension);
        deadlineExtension.setFeedbackSession(this);
    }

    /**
     * Removes a deadline extension from the feedback session.
     */
    public void removeDeadlineExtension(DeadlineExtension deadlineExtension) {
        this.deadlineExtensions.remove(deadlineExtension);
        deadlineExtension.setFeedbackSession(null);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    /**
     * Sets a course as well as the courseId.
     */
    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course == null ? null : course.getId();
    }

    public String getCourseId() {
        return this.courseId;
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
        this.creatorEmail = SanitizationHelper.sanitizeEmail(creatorEmail);
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = SanitizationHelper.sanitizeForRichText(instructions);
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
        this.gracePeriod = Objects.requireNonNullElse(gracePeriod, Duration.ZERO);
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

    public Set<DeadlineExtension> getDeadlineExtensions() {
        return deadlineExtensions;
    }

    public void setDeadlineExtensions(Set<DeadlineExtension> deadlineExtensions) {
        this.deadlineExtensions = deadlineExtensions;
    }

    public Set<FeedbackQuestion> getFeedbackQuestions() {
        return feedbackQuestions;
    }

    public void setFeedbackQuestions(Set<FeedbackQuestion> feedbackQuestions) {
        this.feedbackQuestions = feedbackQuestions;
    }

    public boolean isOpeningSoonEmailSent() {
        return isOpeningSoonEmailSent;
    }

    public void setOpeningSoonEmailSent(boolean isOpeningSoonEmailSent) {
        this.isOpeningSoonEmailSent = isOpeningSoonEmailSent;
    }

    public boolean isOpenedEmailSent() {
        return isOpenedEmailSent;
    }

    public void setOpenedEmailSent(boolean isOpenedEmailSent) {
        this.isOpenedEmailSent = isOpenedEmailSent;
    }

    public boolean isClosingSoonEmailSent() {
        return isClosingSoonEmailSent;
    }

    public void setClosingSoonEmailSent(boolean isClosingSoonEmailSent) {
        this.isClosingSoonEmailSent = isClosingSoonEmailSent;
    }

    public boolean isClosedEmailSent() {
        return isClosedEmailSent;
    }

    public void setClosedEmailSent(boolean isClosedEmailSent) {
        this.isClosedEmailSent = isClosedEmailSent;
    }

    public boolean isPublishedEmailSent() {
        return isPublishedEmailSent;
    }

    public void setPublishedEmailSent(boolean isPublishedEmailSent) {
        this.isPublishedEmailSent = isPublishedEmailSent;
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
        return "FeedbackSession [id=" + id + ", courseId=" + course.getId() + ", name=" + name
                + ", creatorEmail=" + creatorEmail
                + ", instructions=" + instructions + ", startTime=" + startTime + ", endTime=" + endTime
                + ", sessionVisibleFromTime=" + sessionVisibleFromTime
                + ", resultsVisibleFromTime=" + resultsVisibleFromTime + ", gracePeriod=" + gracePeriod
                + ", isClosingSoonEmailEnabled=" + isClosingSoonEmailEnabled
                + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled
                + ", isOpeningSoonEmailSent=" + isOpeningSoonEmailSent + ", isOpenedEmailSent=" + isOpenedEmailSent
                + ", isClosingSoonEmailSent=" + isClosingSoonEmailSent + ", isClosedEmailSent=" + isClosedEmailSent
                + ", isPublishedEmailSent=" + isPublishedEmailSent + ", deadlineExtensions=" + deadlineExtensions
                + ", feedbackQuestions=" + feedbackQuestions + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FeedbackSession other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns true if the session is visible; false otherwise.
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
     * Gets the instructions of the feedback session.
     */
    public String getInstructionsString() {
        return SanitizationHelper.sanitizeForRichText(instructions);
    }

    /**
     * Checks if the feedback session is closed.
     * This occurs only when the current time is after both the deadline and the grace period.
     */
    public boolean isClosed() {
        return Instant.now().isAfter(endTime.plus(gracePeriod));
    }

    /**
     * Checks if the feedback session is open.
     * This occurs when the current time is either the start time or later but before the deadline.
     */
    public boolean isOpened() {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(endTime);
    }

    /**
     * Checks if the feedback session is during the grace period.
     * This occurs when the current time is after end time, but before the end of the grace period.
     */
    public boolean isInGracePeriod() {
        return Instant.now().isAfter(endTime) && !isClosed();
    }

    /**
     * Checks if the feedback session has not opened yet.
     */
    public boolean isWaitingToOpen() {
        return Instant.now().isBefore(startTime);
    }

    /**
     * Checks if the feedback session is opened given the extendedDeadline and grace period.
     */
    public boolean isOpenedGivenExtendedDeadline(Instant extendedDeadline) {
        Instant now = Instant.now();
        return (now.isAfter(startTime) || now.equals(startTime))
                && (now.isBefore(extendedDeadline.plus(gracePeriod)) || now.isBefore(endTime.plus(gracePeriod)));
    }

    /**
     * Checks if the feedback session is closed given the extendedDeadline and grace period.
     * This occurs only when it is after the extended deadline or end time plus grace period.
     */
    public boolean isClosedGivenExtendedDeadline(Instant extendedDeadline) {
        Instant now = Instant.now();
        return !isOpenedGivenExtendedDeadline(extendedDeadline)
                && now.isAfter(endTime.plus(gracePeriod)) && now.isAfter(extendedDeadline.plus(gracePeriod));
    }

    /**
     * Checks if the feedback session is during the grace period given the extendedDeadline.
     */
    public boolean isInGracePeriodGivenExtendedDeadline(Instant extendedDeadline) {
        Instant now = Instant.now();
        return now.isAfter(endTime) && now.isAfter(extendedDeadline) && !isClosedGivenExtendedDeadline(extendedDeadline);
    }

    /**
     * Returns {@code true} if the results of the feedback session is published; {@code false} if not.
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
     * Checks if user with {@code userEmail} is the creator.
     */
    public boolean isCreator(String userEmail) {
        return SanitizationHelper.areEmailsEqual(creatorEmail, userEmail);
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
        return Instant.now().plus(Duration.ofHours(hours)).isAfter(endTime);
    }

    public boolean isSessionDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Returns true if the feedback session is closing (almost closed) after the number of specified hours.
     */
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
        Instant timeClosed = endTime.plus(gracePeriod);
        return timeClosed.isBefore(now) && Duration.between(timeClosed, now).compareTo(Duration.ofHours(1)) < 0;
    }
}
