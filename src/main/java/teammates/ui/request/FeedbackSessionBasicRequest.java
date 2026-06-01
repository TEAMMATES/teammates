package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import java.time.Duration;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import java.time.Instant;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import jakarta.annotation.Nullable;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.exception.InvalidHttpRequestBodyException;
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
    public Instant getResultsVisibleFromTime() {
        switch (responseVisibleSetting) {
        case AT_VISIBLE:
            return Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
        case LATER:
            return Const.TIME_REPRESENTS_LATER;
        case CUSTOM:
            return Instant.ofEpochMilli(customResponseVisibleTimestamp);
        default:
            throw new IllegalStateException("Unknown responseVisibleSetting");
        }
    }

    /**
     * Gets the session visible from time.
     */
    public Instant getSessionVisibleFromTime() {
        switch (sessionVisibleSetting) {
        case AT_OPEN:
            return Const.TIME_REPRESENTS_FOLLOW_OPENING;
        case CUSTOM:
            return Instant.ofEpochMilli(customSessionVisibleTimestamp);
        default:
            throw new IllegalStateException("Unknown sessionVisibleSetting");
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
        validateTrue(instructions != null, "Instructions cannot be null");
        validateTrue(submissionStartTimestamp > 0L, "Start timestamp should be more than zero");
        validateTrue(submissionEndTimestamp > 0L, "End timestamp should be more than zero");

        validateTrue(sessionVisibleSetting != null, "sessionVisibleSetting cannot be null");
        if (sessionVisibleSetting == SessionVisibleSetting.CUSTOM) {
            validateTrue(customSessionVisibleTimestamp != null,
                    "Session visible timestamp should not be null");
            validateTrue(customSessionVisibleTimestamp > 0L,
                    "Session visible timestamp should be more than zero");
        }

        validateTrue(responseVisibleSetting != null, "responseVisibleSetting cannot be null");
        if (responseVisibleSetting == ResponseVisibleSetting.CUSTOM) {
            validateTrue(customResponseVisibleTimestamp != null,
                    "Response visible timestamp should not be null");
            validateTrue(customResponseVisibleTimestamp > 0L,
                    "Response visible timestamp should be more than zero");
        }
    }
}
