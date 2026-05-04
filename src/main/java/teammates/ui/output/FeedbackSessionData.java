package teammates.ui.output;

import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * The API output format of {@link FeedbackSession}.
 */
public class FeedbackSessionData extends ApiOutput {

    private final UUID feedbackSessionId;

    private final String courseId;
    private final String timeZone;
    private final String feedbackSessionName;
    private final String instructions;

    private final Long submissionStartTimestamp;
    private final Long submissionEndTimestamp;
    @Nullable
    private Long submissionEndWithExtensionTimestamp;
    @Nullable
    private Long sessionVisibleFromTimestamp;
    @Nullable
    private Long resultVisibleFromTimestamp;
    private Long gracePeriod;

    private SessionVisibleSetting sessionVisibleSetting;
    @Nullable
    private Long customSessionVisibleTimestamp;

    private ResponseVisibleSetting responseVisibleSetting;
    @Nullable
    private Long customResponseVisibleTimestamp;

    private FeedbackSessionSubmissionStatus submissionStatus;
    private FeedbackSessionPublishStatus publishStatus;

    private Boolean isClosingSoonEmailEnabled;
    private Boolean isPublishedEmailEnabled;

    private long createdAtTimestamp;
    @Nullable
    private final Long deletedAtTimestamp;
    @Nullable
    private InstructorPermissionSet privileges;

    @JsonCreator
    private FeedbackSessionData(UUID feedbackSessionId, String courseId, String timeZone,
            String feedbackSessionName, String instructions, Long submissionStartTimestamp,
            Long submissionEndTimestamp, Long deletedAtTimestamp) {
        this.feedbackSessionId = feedbackSessionId;
        this.courseId = courseId;
        this.timeZone = timeZone;
        this.feedbackSessionName = feedbackSessionName;
        this.instructions = instructions;
        this.submissionStartTimestamp = submissionStartTimestamp;
        this.submissionEndTimestamp = submissionEndTimestamp;
        this.deletedAtTimestamp = deletedAtTimestamp;
    }

    public FeedbackSessionData(FeedbackSession feedbackSession) {
        assert feedbackSession != null;
        assert feedbackSession.getCourse() != null;
        String timeZone = feedbackSession.getCourse().getTimeZone();
        this.feedbackSessionId = feedbackSession.getId();
        this.courseId = feedbackSession.getCourseId();
        this.timeZone = timeZone;
        this.feedbackSessionName = feedbackSession.getName();
        this.instructions = feedbackSession.getInstructions();
        this.submissionStartTimestamp = feedbackSession.getStartTime().toEpochMilli();
        this.submissionEndTimestamp = feedbackSession.getEndTime().toEpochMilli();
        // If no deadline extension time is provided, then the end time with extension is assumed to be
        // just the end time.
        this.submissionEndWithExtensionTimestamp = feedbackSession.getEndTime().toEpochMilli();
        this.gracePeriod = feedbackSession.getGracePeriod().toMinutes();

        Instant sessionVisibleTime = feedbackSession.getSessionVisibleFromTime();
        this.sessionVisibleFromTimestamp = sessionVisibleTime.toEpochMilli();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            this.sessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        } else {
            this.sessionVisibleSetting = SessionVisibleSetting.CUSTOM;
            this.customSessionVisibleTimestamp = this.sessionVisibleFromTimestamp;
        }

        Instant responseVisibleTime = feedbackSession.getResultsVisibleFromTime();
        this.resultVisibleFromTimestamp = responseVisibleTime.toEpochMilli();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            this.responseVisibleSetting = ResponseVisibleSetting.AT_VISIBLE;
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            this.responseVisibleSetting = ResponseVisibleSetting.LATER;
        } else {
            this.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
            this.customResponseVisibleTimestamp = this.resultVisibleFromTimestamp;
        }

        if (!feedbackSession.isVisible()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        } else if (feedbackSession.isVisible() && !feedbackSession.isOpened()
                && !feedbackSession.isClosed()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
        } else if (feedbackSession.isInGracePeriod()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        } else if (feedbackSession.isOpened()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.OPEN;
        } else if (feedbackSession.isClosed()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
        }

        if (feedbackSession.isPublished()) {
            this.publishStatus = FeedbackSessionPublishStatus.PUBLISHED;
        } else {
            this.publishStatus = FeedbackSessionPublishStatus.NOT_PUBLISHED;
        }

        this.isClosingSoonEmailEnabled = feedbackSession.isClosingSoonEmailEnabled();
        this.isPublishedEmailEnabled = feedbackSession.isPublishedEmailEnabled();

        this.createdAtTimestamp = feedbackSession.getCreatedAt().toEpochMilli();
        if (feedbackSession.getDeletedAt() == null) {
            this.deletedAtTimestamp = null;
        } else {
            this.deletedAtTimestamp = feedbackSession.getDeletedAt().toEpochMilli();
        }
    }

    /**
     * Constructs FeedbackSessionData for a given user deadline.
     */
    public FeedbackSessionData(FeedbackSession feedbackSession, Instant extendedDeadline) {
        this(feedbackSession);

        this.submissionEndWithExtensionTimestamp = extendedDeadline.toEpochMilli();

        if (!feedbackSession.isVisible()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        } else if (feedbackSession.isVisible() && !feedbackSession.isOpenedGivenExtendedDeadline(extendedDeadline)
                && !feedbackSession.isClosedGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
        } else if (feedbackSession.isInGracePeriodGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        } else if (feedbackSession.isOpenedGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.OPEN;
        } else if (feedbackSession.isClosedGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
        }
    }

    public UUID getFeedbackSessionId() {
        return feedbackSessionId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getInstructions() {
        return instructions;
    }

    public long getSubmissionStartTimestamp() {
        return submissionStartTimestamp;
    }

    public long getSubmissionEndTimestamp() {
        return submissionEndTimestamp;
    }

    public long getSubmissionEndWithExtensionTimestamp() {
        return submissionEndWithExtensionTimestamp;
    }

    public Long getSessionVisibleFromTimestamp() {
        return sessionVisibleFromTimestamp;
    }

    public Long getResultVisibleFromTimestamp() {
        return resultVisibleFromTimestamp;
    }

    public Long getGracePeriod() {
        return gracePeriod;
    }

    public SessionVisibleSetting getSessionVisibleSetting() {
        return sessionVisibleSetting;
    }

    public Long getCustomSessionVisibleTimestamp() {
        return customSessionVisibleTimestamp;
    }

    public ResponseVisibleSetting getResponseVisibleSetting() {
        return responseVisibleSetting;
    }

    public Long getCustomResponseVisibleTimestamp() {
        return customResponseVisibleTimestamp;
    }

    public FeedbackSessionSubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public FeedbackSessionPublishStatus getPublishStatus() {
        return publishStatus;
    }

    public Boolean getIsClosingSoonEmailEnabled() {
        return isClosingSoonEmailEnabled;
    }

    public Boolean getIsPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setSessionVisibleFromTimestamp(Long sessionVisibleFromTimestamp) {
        this.sessionVisibleFromTimestamp = sessionVisibleFromTimestamp;
    }

    public void setResultVisibleFromTimestamp(Long resultVisibleFromTimestamp) {
        this.resultVisibleFromTimestamp = resultVisibleFromTimestamp;
    }

    public void setGracePeriod(Long gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public void setSessionVisibleSetting(SessionVisibleSetting sessionVisibleSetting) {
        this.sessionVisibleSetting = sessionVisibleSetting;
    }

    public void setCustomSessionVisibleTimestamp(Long customSessionVisibleTimestamp) {
        this.customSessionVisibleTimestamp = customSessionVisibleTimestamp;
    }

    public void setResponseVisibleSetting(ResponseVisibleSetting responseVisibleSetting) {
        this.responseVisibleSetting = responseVisibleSetting;
    }

    public void setCustomResponseVisibleTimestamp(Long customResponseVisibleTimestamp) {
        this.customResponseVisibleTimestamp = customResponseVisibleTimestamp;
    }

    public void setPublishStatus(FeedbackSessionPublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    public void setClosingSoonEmailEnabled(Boolean closingSoonEmailEnabled) {
        isClosingSoonEmailEnabled = closingSoonEmailEnabled;
    }

    public void setPublishedEmailEnabled(Boolean publishedEmailEnabled) {
        isPublishedEmailEnabled = publishedEmailEnabled;
    }

    public long getCreatedAtTimestamp() {
        return createdAtTimestamp;
    }

    public void setCreatedAtTimestamp(long timestamp) {
        createdAtTimestamp = timestamp;
    }

    public Long getDeletedAtTimestamp() {
        return deletedAtTimestamp;
    }

    public InstructorPermissionSet getPrivileges() {
        return privileges;
    }

    public void setPrivileges(InstructorPermissionSet privileges) {
        this.privileges = privileges;
    }

    /**
     * Hides some attributes to students and instructors.
     */
    public void hideInformation() {
        hideInformationForStudentAndInstructor();
        hideSessionVisibilityTimestamps();
    }

    private void hideSessionVisibilityTimestamps() {
        setSessionVisibleFromTimestamp(null);
        setResultVisibleFromTimestamp(null);
        setSessionVisibleSetting(null);
        setCustomSessionVisibleTimestamp(null);
        setResponseVisibleSetting(null);
        setCustomResponseVisibleTimestamp(null);
    }

    /**
     * Hide some attributes to students and instructors.
     */
    public void hideInformationForStudentAndInstructor() {
        setClosingSoonEmailEnabled(null);
        setPublishedEmailEnabled(null);
        setGracePeriod(null);
        setCreatedAtTimestamp(0);
    }
}
