package teammates.ui.output;

import teammates.common.datatransfer.attributes.FeedbackResponseRecordAttributes;

/**
 * The API output format of {@link FeedbackResponseRecordAttributes}.
 */
public class FeedbackResponseRecordData extends ApiOutput {

    private final long count;

    private final long timestamp;

    public FeedbackResponseRecordData(FeedbackResponseRecordAttributes feedbackResponseRecordAttributes) {
        this.count = feedbackResponseRecordAttributes.getCount();
        this.timestamp = feedbackResponseRecordAttributes.getTimestamp();
    }

    public long getCount() {
        return count;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
