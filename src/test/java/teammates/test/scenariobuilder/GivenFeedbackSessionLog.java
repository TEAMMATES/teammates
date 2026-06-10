package teammates.test.scenariobuilder;

import java.time.Instant;
import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.User;

/**
 * Builder for FeedbackSessionLog entities used in test scenarios.
 */
public final class GivenFeedbackSessionLog extends GivenBase<FeedbackSessionLog> {
    private final Instant now = Instant.now();

    public GivenFeedbackSessionLog(GivenData given, UUID feedbackSessionLogId) {
        super(given);
        this.entity = defaultFeedbackSessionLog(feedbackSessionLogId);
    }

    /**
     * Sets the student for the feedback session log.
     */
    public GivenFeedbackSessionLog student(String studentAlias) {
        assert entity.getUser() == null : "User has already been set for this feedback session log";
        User student = given.getOrCreate(studentAlias, given.dataBundle.students, (String sAlias) -> {
            if (entity.getFeedbackSession() == null) {
                given.student(sAlias);
                return;
            }

            String courseAlias = given.getAlias(entity.getFeedbackSession().getCourse());
            given.student(sAlias, s -> s.course(courseAlias));
        });
        entity.setUser(student);
        return this;
    }

    /**
     * Sets the feedback session for the feedback session log.
     */
    public GivenFeedbackSessionLog feedbackSession(String feedbackSessionAlias) {
        assert entity.getFeedbackSession() == null : "Feedback session has already been set for this log";
        FeedbackSession feedbackSession = given.getOrCreate(
                feedbackSessionAlias, given.dataBundle.feedbackSessions, (String fsAlias) -> {
                    if (entity.getUser() == null) {
                        given.feedbackSession(fsAlias);
                        return;
                    }

                    String courseAlias = given.getAlias(entity.getUser().getCourse());
                    given.feedbackSession(fsAlias, fs -> fs.course(courseAlias));
                });
        entity.setFeedbackSession(feedbackSession);
        return this;
    }

    /**
     * Sets the feedback session log type.
     */
    public GivenFeedbackSessionLog type(FeedbackSessionLogType feedbackSessionLogType) {
        entity.setFeedbackSessionLogType(feedbackSessionLogType);
        return this;
    }

    /**
     * Marks the feedback session log as an access log.
     */
    public GivenFeedbackSessionLog access() {
        return type(FeedbackSessionLogType.ACCESS);
    }

    /**
     * Marks the feedback session log as a submission log.
     */
    public GivenFeedbackSessionLog submission() {
        return type(FeedbackSessionLogType.SUBMISSION);
    }

    /**
     * Marks the feedback session log as a view-result log.
     */
    public GivenFeedbackSessionLog viewResult() {
        return type(FeedbackSessionLogType.VIEW_RESULT);
    }

    /**
     * Sets the timestamp for the feedback session log.
     */
    public GivenFeedbackSessionLog timestamp(Instant timestamp) {
        entity.setTimestamp(timestamp);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getFeedbackSession() == null) {
            this.feedbackSession(getFeedbackSessionAlias());
        }

        if (entity.getUser() == null) {
            this.student("default:feedback-session-log-student:" + entity.getId());
        }
    }

    private String getFeedbackSessionAlias() {
        if (entity.getUser() == null) {
            return GivenFeedbackSession.getDefaultAlias(GivenCourse.getDefaultAlias());
        }

        String courseAlias = given.getAlias(entity.getUser().getCourse());
        return GivenFeedbackSession.getDefaultAlias(courseAlias);
    }

    private FeedbackSessionLog defaultFeedbackSessionLog(UUID feedbackSessionLogId) {
        FeedbackSessionLog feedbackSessionLog = new FeedbackSessionLog(
                null, null, FeedbackSessionLogType.ACCESS, now);
        feedbackSessionLog.setId(feedbackSessionLogId);
        return feedbackSessionLog;
    }
}
