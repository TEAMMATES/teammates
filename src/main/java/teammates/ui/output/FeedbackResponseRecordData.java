package teammates.ui.output;

import teammates.common.datatransfer.attributes.FeedbackResponseRecordAttributes;

/**
 * The API output format of {@link FeedbackResponseRecordAttributes}.
 */
public class FeedbackResponseRecordData extends ApiOutput {

    private final int count;

    private final int timestamp;

    public FeedbackResponseRecordData(FeedbackResponseRecordAttributes feedbackResponseRecordAttributes) {
        this.count = feedbackResponseRecordAttributes.getCount();
        this.timestamp = feedbackResponseRecordAttributes.getTimestamp();
    }

    public int getCount() {
        return count;
    }

    public int getTimestamp() {
        return timestamp;
    }

}
