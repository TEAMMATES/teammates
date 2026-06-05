package teammates.test.scenariobuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.User;

/**
 * Builder for DeadlineExtension entities used in test scenarios.
 */
public final class GivenDeadlineExtension extends GivenBase<DeadlineExtension> {
    private final Instant now = Instant.now();

    public GivenDeadlineExtension(GivenData given, UUID deadlineExtensionId) {
        super(given);
        this.entity = defaultDeadlineExtension(deadlineExtensionId);
    }

    /**
     * Sets the student for the deadline extension.
     */
    public GivenDeadlineExtension student(String userAlias) {
        assert entity.getUser() == null : "User has already been set for this deadline extension";
        User user = given.getOrCreate(userAlias, given.dataBundle.students, (String uAlias) -> {
            if (entity.getFeedbackSession() == null) {
                given.student(uAlias);
                return;
            }

            String courseAlias = given.getAlias(entity.getFeedbackSession().getCourse());
            given.student(uAlias, s -> s.course(courseAlias));
        });
        entity.setUser(user);
        return this;
    }

    /**
     * Sets the instructor for the deadline extension.
     */
    public GivenDeadlineExtension instructor(String userAlias) {
        assert entity.getUser() == null : "User has already been set for this deadline extension";
        User user = given.getOrCreate(userAlias, given.dataBundle.instructors, (String uAlias) -> {
            if (entity.getFeedbackSession() == null) {
                given.instructor(uAlias);
                return;
            }

            String courseAlias = given.getAlias(entity.getFeedbackSession().getCourse());
            given.instructor(uAlias, s -> s.course(courseAlias));
        });
        entity.setUser(user);
        return this;
    }

    /**
     * Sets the feedback session for the deadline extension.
     */
    public GivenDeadlineExtension feedbackSession(String feedbackSessionAlias) {
        assert entity.getFeedbackSession() == null : "Feedback session has already been set for this deadline extension";
        FeedbackSession feedbackSession = given.getOrCreate(
                feedbackSessionAlias, given.dataBundle.feedbackSessions, (String fsAlias) -> {
                    if (entity.getUser() == null) {
                        given.feedbackSession(fsAlias);
                        return;
                    }

                    String courseAlias = given.getAlias(entity.getUser().getCourse());
                    given.feedbackSession(fsAlias, fs -> fs.course(courseAlias));
                });
        feedbackSession.addDeadlineExtension(entity);
        return this;
    }

    /**
     * Sets the end time for the deadline extension.
     */
    public GivenDeadlineExtension endTime(Instant endTime) {
        entity.setEndTime(endTime);
        return this;
    }

    /**
     * Sets the deadline extension end time to close soon.
     */
    public GivenDeadlineExtension closingSoon() {
        return endTime(now.plus(23, ChronoUnit.HOURS));
    }

    /**
     * Sets whether the closing soon email has been sent.
     */
    public GivenDeadlineExtension closingSoonEmailSent(boolean isClosingSoonEmailSent) {
        entity.setClosingSoonEmailSent(isClosingSoonEmailSent);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getFeedbackSession() == null) {
            this.feedbackSession(getFeedbackSessionAlias());
        }

        if (entity.getUser() == null) {
            this.student("default:deadline-extension-student:" + entity.getId());
        }
    }

    private String getFeedbackSessionAlias() {
        if (entity.getUser() == null) {
            return GivenFeedbackSession.getDefaultAlias(GivenCourse.getDefaultAlias());
        }

        String courseAlias = given.getAlias(entity.getUser().getCourse());
        return GivenFeedbackSession.getDefaultAlias(courseAlias);
    }

    private DeadlineExtension defaultDeadlineExtension(UUID deadlineExtensionId) {
        DeadlineExtension deadlineExtension = new DeadlineExtension(null, now.plus(2, ChronoUnit.HOURS));
        deadlineExtension.setId(deadlineExtensionId);
        return deadlineExtension;
    }
}
