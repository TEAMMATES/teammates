package teammates.ui.webapi.action;

import java.time.Instant;

import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ResponseVisibleSetting;
import teammates.ui.webapi.output.SessionVisibleSetting;
import teammates.ui.webapi.request.BasicRequest;

/**
 * Data transfer objects for {@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes}
 * between controller and HTTP.
 */
public class FeedbackSessionInfo {

    /**
     * The basic request body format for creating/saving of feedback session.
     */
    private static class FeedbackSessionBasicRequest extends BasicRequest {
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
