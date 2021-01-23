package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseRecordAttributes;

/**
 * The API output format of a list of {@link FeedbackResponseRecordAttributes}.
 */
public class FeedbackResponseRecordsData extends ApiOutput {

    private List<FeedbackResponseRecordData> responseRecords;

    public FeedbackResponseRecordsData(List<FeedbackResponseRecordAttributes> responseRecords) {
        this.responseRecords = responseRecords.stream().map(FeedbackResponseRecordData::new).collect(Collectors.toList());
    }

    public FeedbackResponseRecordsData() {
        responseRecords = Collections.emptyList();
    }

    public List<FeedbackResponseRecordData> getResponseRecords() {
        return responseRecords;
    }

}
