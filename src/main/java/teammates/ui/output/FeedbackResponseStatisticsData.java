package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;

/**
 * The API output format of a list of {@link FeedbackResponseStatisticAttributes}.
 */
public class FeedbackResponseStatisticsData extends ApiOutput {

    private List<FeedbackResponseStatisticData> statistics;

    public FeedbackResponseStatisticsData(List<FeedbackResponseStatisticAttributes> statistics) {
        this.statistics = statistics.stream().map(FeedbackResponseStatisticData::new).collect(Collectors.toList());
    }

    public FeedbackResponseStatisticsData() {
        statistics = Collections.emptyList();
    }

    public void setResponses(List<FeedbackResponseStatisticData> statistics) {
        this.statistics = statistics;
    }

    public List<FeedbackResponseStatisticData> getStatistics() {
        return statistics;
    }
}
