package teammates.ui.output;

import java.util.Set;

import teammates.storage.entity.FeedbackResponseRecord;

/**
 * Output format of response record data.
 */
public class FeedbackResponseRecordData extends ApiOutput {

    private final Set<FeedbackResponseRecord> feedbackResponseRecords;

    public FeedbackResponseRecordData(Set<FeedbackResponseRecord> feedbackResponseRecords) {
        this.feedbackResponseRecords = feedbackResponseRecords;
    }

    public Set<FeedbackResponseRecord> getFeedbackResponseRecords() {
        return feedbackResponseRecords;
    }

}
