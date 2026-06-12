package teammates.ui.output;

import java.util.List;

/**
 * The API output format of a list of usage statistics buckets.
 */
public class UsageStatisticsRangeData implements ApiOutput {

    private final List<UsageStatisticsData> result;

    public UsageStatisticsRangeData(List<UsageStatisticsData> result) {
        this.result = result;
    }

    public List<UsageStatisticsData> getResult() {
        return result;
    }

}
