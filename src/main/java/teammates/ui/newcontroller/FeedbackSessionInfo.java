package teammates.ui.newcontroller;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Data transfer objects for {@link FeedbackSessionAttributes} between controller and HTTP.
 */
public class FeedbackSessionInfo {

    /**
     * The option for session visible setting.
     */
    public enum SessionVisibleSetting {
        /**
         * Customized session visible time.
         */
        CUSTOM,

        /**
         * Session visible when open.
         */
        AT_OPEN
    }

    /**
     * The option for response visible setting.
     */
    public enum ResponseVisibleSetting {
        /**
         * Customized response visible time.
         */
        CUSTOM,

        /**
         * Response visible when session is visible.
         */
        AT_VISIBLE,

        /**
         * Response won't be visible automatically.
         */
        LATER
    }

    /**
     * Represents the submission status of a feedback session.
     */
    public enum FeedbackSessionSubmissionStatus {

        /**
         * Feedback session is not visible.
         */
        NOT_VISIBLE,

        /**
         * Feedback session is visible to view but not open for submission.
         */
        VISIBLE_NOT_OPEN,

        /**
         * Feedback session is open for submission.
         */
        OPEN,

        /**
         * Feedback session is in grace period.
         */
        GRACE_PERIOD,

        /**
         * Feedback session is closed for submission.
         */
        CLOSED
    }

    /**
     * Represents the publish status of the a feedback session.
     */
    public enum FeedbackSessionPublishStatus {

        /**
         * Feedback session is published.
         */
        PUBLISHED,

        /**
         * Feedback session is not published.
         */
        NOT_PUBLISHED,
    }

    /**
     * The output format for a list of feedback session.
     */
    public static class FeedbackSessionsResponse extends ApiOutput {
        private final List<FeedbackSessionResponse> feedbackSessions;

        public FeedbackSessionsResponse(List<FeedbackSessionAttributes> feedbackSessionAttributesList) {
            this.feedbackSessions =
                    feedbackSessionAttributesList.stream().map(FeedbackSessionResponse::new).collect(Collectors.toList());
        }

        public List<FeedbackSessionResponse> getFeedbackSessions() {
            return feedbackSessions;
        }
    }

    /**
     * The output format for a feedback session.
     */
    public static class FeedbackSessionResponse extends ApiOutput {
        private final String courseId;
        private final String timeZone;
        private final String feedbackSessionName;
        private final String instructions;

        private final Long submissionStartTimestamp;
        private final Long submissionEndTimestamp;
        private Long gracePeriod;

        private SessionVisibleSetting sessionVisibleSetting;
        private Long customSessionVisibleTimestamp;

        private ResponseVisibleSetting responseVisibleSetting;
        private Long customResponseVisibleTimestamp;

        private FeedbackSessionSubmissionStatus submissionStatus;
        private FeedbackSessionPublishStatus publishStatus;

        private Boolean isClosingEmailEnabled;
        private Boolean isPublishedEmailEnabled;

        private final long createdAtTimestamp;
        private final Long deletedAtTimestamp;

        public FeedbackSessionResponse(FeedbackSessionAttributes feedbackSessionAttributes) {
            this.courseId = feedbackSessionAttributes.getCourseId();
            this.timeZone = feedbackSessionAttributes.getTimeZone().getId();
            this.feedbackSessionName = feedbackSessionAttributes.getFeedbackSessionName();
            this.instructions = feedbackSessionAttributes.getInstructions();
            this.submissionStartTimestamp = feedbackSessionAttributes.getStartTime().toEpochMilli();
            this.submissionEndTimestamp = feedbackSessionAttributes.getEndTime().toEpochMilli();
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

        public long getGracePeriod() {
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

        public boolean isClosingEmailEnabled() {
            return isClosingEmailEnabled;
        }

        public boolean isPublishedEmailEnabled() {
            return isPublishedEmailEnabled;
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

        public Long getDeletedAtTimestamp() {
            return deletedAtTimestamp;
        }
    }

    /**
     * The basic request body format for creating/saving of feedback session.
     */
    private static class FeedbackSessionBasicRequest extends Action.RequestBody {
        private String instructions;

        private long submissionStartTimestamp;
        private long submissionEndTimestamp;
        private long gracePeriod;

        private SessionVisibleSetting sessionVisibleSetting;
        private Long customSessionVisibleTimestamp;

        private ResponseVisibleSetting responseVisibleSetting;
        private Long customResponseVisibleTimestamp;

        private boolean isClosingEmailEnabled;
        private boolean isPublishedEmailEnabled;

        public String getInstructions() {
            return instructions;
        }

        public Instant getSubmissionStartTime() {
            return Instant.ofEpochMilli(submissionStartTimestamp);
        }

        public Instant getSubmissionEndTime() {
            return Instant.ofEpochMilli(submissionEndTimestamp);
        }

        public long getGracePeriod() {
            return gracePeriod;
        }

        public Instant getResultsVisibleFromTime() {
            switch (responseVisibleSetting) {
            case AT_VISIBLE:
                return Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
            case LATER:
                return Const.TIME_REPRESENTS_LATER;
            case CUSTOM:
                return Instant.ofEpochMilli(customResponseVisibleTimestamp);
            default:
                throw new InvalidHttpRequestBodyException("Unknown responseVisibleSetting");
            }
        }

        public Instant getSessionVisibleFromTime() {
            switch (sessionVisibleSetting) {
            case AT_OPEN:
                return Const.TIME_REPRESENTS_FOLLOW_OPENING;
            case CUSTOM:
                return Instant.ofEpochMilli(customSessionVisibleTimestamp);
            default:
                throw new InvalidHttpRequestBodyException("Unknown sessionVisibleSetting");
            }
        }

        public boolean isClosingEmailEnabled() {
            return isClosingEmailEnabled;
        }

        public boolean isPublishedEmailEnabled() {
            return isPublishedEmailEnabled;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public void setSubmissionStartTimestamp(long submissionStartTimestamp) {
            this.submissionStartTimestamp = submissionStartTimestamp;
        }

        public void setSubmissionEndTimestamp(long submissionEndTimestamp) {
            this.submissionEndTimestamp = submissionEndTimestamp;
        }

        public void setGracePeriod(long gracePeriod) {
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

        public void setClosingEmailEnabled(boolean closingEmailEnabled) {
            isClosingEmailEnabled = closingEmailEnabled;
        }

        public void setPublishedEmailEnabled(boolean publishedEmailEnabled) {
            isPublishedEmailEnabled = publishedEmailEnabled;
        }

        @Override
        public void validate() {
            assertTrue(instructions != null, "Instructions cannot be null");
            assertTrue(submissionStartTimestamp > 0L, "Start timestamp should be more than zero");
            assertTrue(submissionEndTimestamp > 0L, "End timestamp should be more than zero");

            assertTrue(sessionVisibleSetting != null, "sessionVisibleSetting cannot be null");
            if (sessionVisibleSetting == SessionVisibleSetting.CUSTOM) {
                assertTrue(customSessionVisibleTimestamp != null,
                        "session visible timestamp should not be null");
                assertTrue(customSessionVisibleTimestamp > 0L,
                        "session visible timestamp should be more than zero");
            }

            assertTrue(responseVisibleSetting != null, "responseVisibleSetting cannot be null");
            if (responseVisibleSetting == ResponseVisibleSetting.CUSTOM) {
                assertTrue(customResponseVisibleTimestamp != null,
                        "response visible timestamp should not be null");
                assertTrue(customResponseVisibleTimestamp > 0L,
                        "response visible timestamp should be more than zero");
            }
        }
    }

    /**
     * The request body format for saving of feedback session.
     */
    public static class FeedbackSessionSaveRequest extends FeedbackSessionBasicRequest {

    }

    /**
     * The request body format for creation of feedback session.
     */
    public static class FeedbackSessionCreateRequest extends FeedbackSessionBasicRequest {
        private String feedbackSessionName;

        public String getFeedbackSessionName() {
            return feedbackSessionName;
        }

        public void setFeedbackSessionName(String feedbackSessionName) {
            this.feedbackSessionName = feedbackSessionName;
        }

        @Override
        public void validate() {
            super.validate();

            assertTrue(feedbackSessionName != null, "Session name cannot be null");
            assertTrue(!feedbackSessionName.isEmpty(), "Session name cannot be empty");
        }
    }
}
