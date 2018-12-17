package teammates.ui.newcontroller;

import java.time.Instant;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;

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
     * The output format for a feedback session.
     */
    public static class FeedbackSessionResponse extends ActionResult.ActionOutput {
        private final String courseId;
        private final String timeZone;
        private final String feedbackSessionName;
        private final String instructions;

        private final long submissionStartTimestamp;
        private final long submissionEndTimestamp;
        private final long gracePeriod;

        private final SessionVisibleSetting sessionVisibleSetting;
        private Long customSessionVisibleTimestamp;

        private final ResponseVisibleSetting responseVisibleSetting;
        private Long customResponseVisibleTimestamp;

        private final String submissionStatus;
        private final String publishStatus;

        private final boolean isClosingEmailEnabled;
        private final boolean isPublishedEmailEnabled;

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

            if (feedbackSessionAttributes.isOpened()) {
                this.submissionStatus = "Open";
            } else if (feedbackSessionAttributes.isWaitingToOpen()) {
                this.submissionStatus = "Awaiting";
            } else {
                this.submissionStatus = "Closed";
            }

            if (feedbackSessionAttributes.isPublished()) {
                this.publishStatus = "Published";
            } else {
                this.publishStatus = "Not Published";
            }

            this.isClosingEmailEnabled = feedbackSessionAttributes.isClosingEmailEnabled();
            this.isPublishedEmailEnabled = feedbackSessionAttributes.isPublishedEmailEnabled();
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

        public String getSubmissionStatus() {
            return submissionStatus;
        }

        public String getPublishStatus() {
            return publishStatus;
        }

        public boolean isClosingEmailEnabled() {
            return isClosingEmailEnabled;
        }

        public boolean isPublishedEmailEnabled() {
            return isPublishedEmailEnabled;
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

        public Instant getSubmissionEndTimestamp() {
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
     * The basic request body format for saving of feedback session.
     */
    public static class FeedbackSessionSaveRequest extends FeedbackSessionBasicRequest {

    }

}
