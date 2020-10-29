package teammates.ui.output;

import java.time.Instant;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

/**
 * The API output format of {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionData extends ApiOutput {
    private final String courseId;
    private final String timeZone;
    private final String feedbackSessionName;
    private final String instructions;

    private final Long submissionStartTimestamp;
    private final Long submissionEndTimestamp;
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

    private Boolean isClosingEmailEnabled;
    private Boolean isPublishedEmailEnabled;

    private long createdAtTimestamp;
    @Nullable
    private final Long deletedAtTimestamp;
    @Nullable
    private InstructorPrivilegeData privileges;

    public FeedbackSessionData(FeedbackSessionAttributes feedbackSessionAttributes) {
        this.courseId = feedbackSessionAttributes.getCourseId();
        this.timeZone = feedbackSessionAttributes.getTimeZone().getId();
        this.feedbackSessionName = feedbackSessionAttributes.getFeedbackSessionName();
        this.instructions = feedbackSessionAttributes.getInstructions();
        this.submissionStartTimestamp = feedbackSessionAttributes.getStartTime().toEpochMilli();
        this.submissionEndTimestamp = feedbackSessionAttributes.getEndTime().toEpochMilli();
        this.resultVisibleFromTimestamp = feedbackSessionAttributes.getResultsVisibleFromTime().toEpochMilli();
        this.gracePeriod = feedbackSessionAttributes.getGracePeriodMinutes();

        Instant sessionVisibleTime = feedbackSessionAttributes.getSessionVisibleFromTime();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            this.sessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        } else {
            this.sessionVisibleSetting = SessionVisibleSetting.CUSTOM;
            this.customSessionVisibleTimestamp = sessionVisibleTime.toEpochMilli();
        }

        Instant responseVisibleTime = feedbackSessionAttributes.getResultsVisibleFromTime();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            this.responseVisibleSetting = ResponseVisibleSetting.AT_VISIBLE;
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            this.responseVisibleSetting = ResponseVisibleSetting.LATER;
        } else {
            this.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
            this.customResponseVisibleTimestamp = responseVisibleTime.toEpochMilli();
        }

        if (!feedbackSessionAttributes.isVisible()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        }
        if (feedbackSessionAttributes.isVisible() && !feedbackSessionAttributes.isOpened()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
        }
        if (feedbackSessionAttributes.isOpened()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.OPEN;
        }
        if (feedbackSessionAttributes.isClosed()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
        }
        if (feedbackSessionAttributes.isInGracePeriod()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        }

        if (feedbackSessionAttributes.isPublished()) {
            this.publishStatus = FeedbackSessionPublishStatus.PUBLISHED;
        } else {
            this.publishStatus = FeedbackSessionPublishStatus.NOT_PUBLISHED;
        }

        this.isClosingEmailEnabled = feedbackSessionAttributes.isClosingEmailEnabled();
        this.isPublishedEmailEnabled = feedbackSessionAttributes.isPublishedEmailEnabled();

        this.createdAtTimestamp = feedbackSessionAttributes.getCreatedTime().toEpochMilli();
        if (feedbackSessionAttributes.getDeletedTime() == null) {
            this.deletedAtTimestamp = null;
        } else {
            this.deletedAtTimestamp = feedbackSessionAttributes.getDeletedTime().toEpochMilli();
        }
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

    public Boolean getIsClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public Boolean getIsPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
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

    public void setClosingEmailEnabled(Boolean closingEmailEnabled) {
        isClosingEmailEnabled = closingEmailEnabled;
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

    public InstructorPrivilegeData getPrivileges() {
        return privileges;
    }

    public void setPrivileges(InstructorPrivilegeData privileges) {
        this.privileges = privileges;
    }

    /**
     * Hides some attributes to student.
     */
    public void hideInformationForStudent() {
        hideInformationForInstructor();
        setResultVisibleFromTimestamp(null);
        setSessionVisibleSetting(null);
        setCustomSessionVisibleTimestamp(null);
        setResponseVisibleSetting(null);
        setCustomResponseVisibleTimestamp(null);
    }

    /**
     * Hides some attributes to instructor without appropriate privilege.
     */
    public void hideInformationForInstructor() {
        setClosingEmailEnabled(null);
        setPublishedEmailEnabled(null);
        setGracePeriod(null);
        setCreatedAtTimestamp(0);
    }
}
