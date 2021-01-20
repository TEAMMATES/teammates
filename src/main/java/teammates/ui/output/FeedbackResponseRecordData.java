package teammates.ui.output;

import teammates.storage.entity.FeedbackResponseRecord;

import java.util.Set;

public class FeedbackResponseRecordData extends ApiOutput {
    private final Set<FeedbackResponseRecord> feedbackResponseRecords;
    public FeedbackResponseRecordData(Set<FeedbackResponseRecord> feedbackResponseRecords) {
        this.feedbackResponseRecords = feedbackResponseRecords;
    }
    public Set<FeedbackResponseRecord> getFeedbackResponseRecords() {
        return feedbackResponseRecords;
    }

}
