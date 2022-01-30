package teammates.ui.output;

import java.time.Instant;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;

/**
 * The API output format of {@link FeedbackResponseAttributes}.
 */
public class FeedbackResponseStatisticData extends ApiOutput {

    private final Instant timeStamp;

    private final int count;

    public FeedbackResponseStatisticData(FeedbackResponseStatisticAttributes feedbackResponseStatisticAttributes) {
        this.timeStamp = feedbackResponseStatisticAttributes.getTimeStamp();
        this.count = feedbackResponseStatisticAttributes.getCount();
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public int getCount() {
        return count;
    }
}
