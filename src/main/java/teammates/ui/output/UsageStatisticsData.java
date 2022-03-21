package teammates.ui.output;

import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;

/**
 * The API output format of {@link UsageStatisticsAttributes}.
 */
public class UsageStatisticsData extends ApiOutput {

    private final long startTime;
    private final int timePeriod;
    private final int numResponses;

    public UsageStatisticsData(UsageStatisticsAttributes attributes) {
        this.startTime = attributes.getStartTime().toEpochMilli();
        this.timePeriod = attributes.getTimePeriod();
        this.numResponses = attributes.getNumResponses();
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public int getNumResponses() {
        return numResponses;
    }

}
