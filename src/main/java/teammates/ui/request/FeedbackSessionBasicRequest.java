package teammates.ui.request;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Nullable;

import teammates.common.util.Const;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;

/**
 * The basic request body format for creating/saving of feedback session.
 */
public class FeedbackSessionBasicRequest extends BasicRequest {
    private String instructions;

    private long submissionStartTimestamp;
    private long submissionEndTimestamp;
    private long gracePeriod;

    private SessionVisibleSetting sessionVisibleSetting;
    @Nullable
    private Long customSessionVisibleTimestamp;

    private ResponseVisibleSetting responseVisibleSetting;
    @Nullable
    private Long customResponseVisibleTimestamp;

    private boolean isClosingSoonEmailEnabled;
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

    public Duration getGracePeriod() {
        return Duration.ofMinutes(gracePeriod);
    }

    /**
     * Gets the result visible from time of the session.
     */
    public Instant getResultsVisibleFromTime() throws InvalidHttpRequestBodyException {
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

    /**
     * Gets the session visible from time.
     */
    public Instant getSessionVisibleFromTime() throws InvalidHttpRequestBodyException {
        switch (sessionVisibleSetting) {
        case AT_OPEN:
            return Const.TIME_REPRESENTS_FOLLOW_OPENING;
        case CUSTOM:
            return Instant.ofEpochMilli(customSessionVisibleTimestamp);
        default:
            throw new InvalidHttpRequestBodyException("Unknown sessionVisibleSetting");
        }
    }

    public boolean isClosingSoonEmailEnabled() {
        return isClosingSoonEmailEnabled;
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

    public void setClosingSoonEmailEnabled(boolean closingSoonEmailEnabled) {
        isClosingSoonEmailEnabled = closingSoonEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean publishedEmailEnabled) {
        isPublishedEmailEnabled = publishedEmailEnabled;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(instructions != null, "Instructions cannot be null");
        assertTrue(submissionStartTimestamp > 0L, "Start timestamp should be more than zero");
        assertTrue(submissionEndTimestamp > 0L, "End timestamp should be more than zero");

        assertTrue(sessionVisibleSetting != null, "sessionVisibleSetting cannot be null");
        if (sessionVisibleSetting == SessionVisibleSetting.CUSTOM) {
            assertTrue(customSessionVisibleTimestamp != null,
                    "Session visible timestamp should not be null");
            assertTrue(customSessionVisibleTimestamp > 0L,
                    "Session visible timestamp should be more than zero");
        }

        assertTrue(responseVisibleSetting != null, "responseVisibleSetting cannot be null");
        if (responseVisibleSetting == ResponseVisibleSetting.CUSTOM) {
            assertTrue(customResponseVisibleTimestamp != null,
                    "Response visible timestamp should not be null");
            assertTrue(customResponseVisibleTimestamp > 0L,
                    "Response visible timestamp should be more than zero");
        }
    }
}
