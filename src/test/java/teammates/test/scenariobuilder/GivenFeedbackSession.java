package teammates.test.scenariobuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;

/**
 * Builder for FeedbackSession entities used in test scenarios.
 */
public final class GivenFeedbackSession extends GivenBase<FeedbackSession> {
    private final Instant now = Instant.now();
    private boolean isNoCreator;

    public GivenFeedbackSession(GivenData given, UUID feedbackSessionId) {
        super(given);
        this.entity = defaultFeedbackSession(feedbackSessionId);
    }

    /**
     * Sets the name for the feedback session.
     */
    public GivenFeedbackSession name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the course for the feedback session.
     */
    public GivenFeedbackSession course(String courseAlias) {
        assert entity.getCourse() == null : "Course has already been set for this feedback session";
        Course course = given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        course.addFeedbackSession(entity);
        return this;
    }

    /**
     * Sets the default course for the feedback session.
     */
    public GivenFeedbackSession defaultCourse() {
        return course(GivenData.DEFAULT_COURSE_ALIAS);
    }

    /**
     * Sets the creator for the feedback session.
     */
    public GivenFeedbackSession creator(String instructorAlias) {
        assert entity.getSessionCreator() == null : "Creator has already been set for this feedback session";
        isNoCreator = false;
        Instructor instructor = given.getOrCreate(instructorAlias, given.dataBundle.instructors, (String iAlias) -> {
            if (entity.getCourse() == null) {
                this.course(GivenCourse.getDefaultAlias());
            }

            String courseAlias = given.getAlias(entity.getCourse());
            given.instructor(iAlias, i -> i.course(courseAlias));
        });

        if (entity.getCourse() == null) {
            instructor.getCourse().addFeedbackSession(entity);
        }
        entity.setSessionCreator(instructor);
        return this;
    }

    /**
     * Sets no creator for the feedback session.
     */
    public GivenFeedbackSession noCreator() {
        isNoCreator = true;
        entity.setSessionCreator(null);
        return this;
    }

    /**
     * Sets the instructions for the feedback session.
     */
    public GivenFeedbackSession instructions(String instructions) {
        entity.setInstructions(instructions);
        return this;
    }

    /**
     * Sets the start time for the feedback session.
     */
    public GivenFeedbackSession startTime(Instant startTime) {
        entity.setStartTime(startTime);
        return this;
    }

    /**
     * Sets the end time for the feedback session.
     */
    public GivenFeedbackSession endTime(Instant endTime) {
        entity.setEndTime(endTime);
        return this;
    }

    /**
     * Sets the results visible time for the feedback session.
     */
    public GivenFeedbackSession resultsVisibleFromTime(Instant resultsVisibleFromTime) {
        entity.setResultsVisibleFromTime(resultsVisibleFromTime);
        return this;
    }

    /**
     * Sets the results visibility to follow the session visibility.
     */
    public GivenFeedbackSession resultsVisibleFromSessionVisibleTime() {
        return resultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
    }

    /**
     * Sets the grace period for the feedback session.
     */
    public GivenFeedbackSession gracePeriod(Duration gracePeriod) {
        entity.setGracePeriod(gracePeriod);
        return this;
    }

    /**
     * Marks the feedback session as not yet open to students.
     */
    public GivenFeedbackSession notVisible() {
        return startTime(now.plus(3, ChronoUnit.HOURS))
                .endTime(now.plus(5, ChronoUnit.HOURS))
                .resultsVisibleFromTime(now.plus(6, ChronoUnit.HOURS));
    }

    /**
     * Marks the feedback session as waiting to open.
     */
    public GivenFeedbackSession waitingToOpen() {
        return startTime(now.plus(1, ChronoUnit.HOURS))
                .endTime(now.plus(3, ChronoUnit.HOURS))
                .resultsVisibleFromTime(now.plus(4, ChronoUnit.HOURS));
    }

    /**
     * Marks the feedback session as opened.
     */
    public GivenFeedbackSession opened() {
        return startTime(now.minus(1, ChronoUnit.HOURS))
                .endTime(now.plus(1, ChronoUnit.HOURS))
                .resultsVisibleFromTime(now.plus(2, ChronoUnit.HOURS));
    }

    /**
     * Marks the feedback session as closed.
     */
    public GivenFeedbackSession closed() {
        return startTime(now.minus(3, ChronoUnit.HOURS))
                .endTime(now.minus(2, ChronoUnit.HOURS))
                .resultsVisibleFromTime(now.plus(1, ChronoUnit.HOURS));
    }

    /**
     * Marks the feedback session as opening soon.
     */
    public GivenFeedbackSession openingSoon() {
        return startTime(now.plus(23, ChronoUnit.HOURS))
                .endTime(now.plus(25, ChronoUnit.HOURS))
                .resultsVisibleFromTime(now.plus(26, ChronoUnit.HOURS));
    }

    /**
     * Marks the feedback session as closing soon.
     */
    public GivenFeedbackSession closingSoon() {
        return startTime(now.minus(25, ChronoUnit.HOURS))
                .endTime(now.plus(23, ChronoUnit.HOURS));
    }

    /**
     * Marks the feedback session as published.
     */
    public GivenFeedbackSession published() {
        return startTime(now.minus(2, ChronoUnit.HOURS))
                .endTime(now.minus(1, ChronoUnit.HOURS))
                .resultsVisibleFromTime(now.minus(30, ChronoUnit.MINUTES));
    }

    /**
     * Sets whether the closing soon email is enabled.
     */
    public GivenFeedbackSession closingSoonEmailEnabled(boolean isClosingSoonEmailEnabled) {
        entity.setClosingSoonEmailEnabled(isClosingSoonEmailEnabled);
        return this;
    }

    /**
     * Sets whether the published email is enabled.
     */
    public GivenFeedbackSession publishedEmailEnabled(boolean isPublishedEmailEnabled) {
        entity.setPublishedEmailEnabled(isPublishedEmailEnabled);
        return this;
    }

    /**
     * Marks the opening soon email as sent.
     */
    public GivenFeedbackSession openingSoonEmailSent(boolean isOpeningSoonEmailSent) {
        entity.setOpeningSoonEmailSent(isOpeningSoonEmailSent);
        return this;
    }

    /**
     * Marks the opened email as sent.
     */
    public GivenFeedbackSession openedEmailSent(boolean isOpenedEmailSent) {
        entity.setOpenedEmailSent(isOpenedEmailSent);
        return this;
    }

    /**
     * Marks the closing soon email as sent.
     */
    public GivenFeedbackSession closingSoonEmailSent(boolean isClosingSoonEmailSent) {
        entity.setClosingSoonEmailSent(isClosingSoonEmailSent);
        return this;
    }

    /**
     * Marks the closed email as sent.
     */
    public GivenFeedbackSession closedEmailSent(boolean isClosedEmailSent) {
        entity.setClosedEmailSent(isClosedEmailSent);
        return this;
    }

    /**
     * Marks the published email as sent.
     */
    public GivenFeedbackSession publishedEmailSent(boolean isPublishedEmailSent) {
        entity.setPublishedEmailSent(isPublishedEmailSent);
        return this;
    }

    /**
     * Marks the feedback session as soft deleted.
     */
    public GivenFeedbackSession softDeleted() {
        entity.setDeletedAt(now);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getCourseId() == null) {
            this.course(GivenCourse.getDefaultAlias());
        }

        if (entity.getSessionCreator() == null && !isNoCreator) {
            String instructorAlias = "default:feedback-session-creator:" + entity.getId();
            this.creator(instructorAlias);
        }
    }

    /**
     * Generates a default alias for a feedback session in the specified course.
     */
    public static String getDefaultAlias(String courseAlias) {
        return "default:" + courseAlias;
    }

    private FeedbackSession defaultFeedbackSession(UUID feedbackSessionId) {
        FeedbackSession feedbackSession = new FeedbackSession(
                feedbackSessionId.toString(),
                null,
                "<p>instructions:" + feedbackSessionId.toString() + "</p>",
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS),
                Duration.ZERO,
                true,
                true);
        feedbackSession.setId(feedbackSessionId);
        return feedbackSession;
    }
}
