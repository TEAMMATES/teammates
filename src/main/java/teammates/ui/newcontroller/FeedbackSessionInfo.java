package teammates.ui.newcontroller;

import java.time.Instant;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
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

}
