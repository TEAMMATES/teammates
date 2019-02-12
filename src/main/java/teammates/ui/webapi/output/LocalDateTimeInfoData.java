package teammates.ui.webapi.output;

import teammates.common.util.TimeHelper;

/**
 * The API output format of a {@code LocalDateTimeInfo} to hold information for resolving DST overlaps/gaps.
 */
public class LocalDateTimeInfoData extends ApiOutput {
    private long resolvedTimestamp;
    private TimeHelper.LocalDateTimeAmbiguityStatus resolvedStatus;

    private Long earlierInterpretationTimestamp;
    private Long laterInterpretationTimestamp;

    /**
     * Constructs {@link LocalDateTimeInfoData} with UNAMBIGUOUS status.
     */
    public static LocalDateTimeInfoData unambiguous(long resolvedTimestamp) {
        LocalDateTimeInfoData localDateTimeInfo = new LocalDateTimeInfoData();

        localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.UNAMBIGUOUS;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        return localDateTimeInfo;
    }

    /**
     * Constructs {@link LocalDateTimeInfoData} with GAP status.
     */
    public static LocalDateTimeInfoData gap(long resolvedTimestamp) {
        LocalDateTimeInfoData localDateTimeInfo = new LocalDateTimeInfoData();

        localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.GAP;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        return localDateTimeInfo;
    }

    /**
     * Constructs {@link LocalDateTimeInfoData} with OVERLAP status.
     */
    public static LocalDateTimeInfoData overlap(long resolvedTimestamp,
                                                long earlierInterpretationTimestamp, long laterInterpretationTimestamp) {
        LocalDateTimeInfoData localDateTimeInfo = new LocalDateTimeInfoData();

        localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.OVERLAP;
        localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

        localDateTimeInfo.earlierInterpretationTimestamp = earlierInterpretationTimestamp;
        localDateTimeInfo.laterInterpretationTimestamp = laterInterpretationTimestamp;

        return localDateTimeInfo;
    }

    public long getResolvedTimestamp() {
        return resolvedTimestamp;
    }

    public TimeHelper.LocalDateTimeAmbiguityStatus getResolvedStatus() {
        return resolvedStatus;
    }

    public Long getEarlierInterpretationTimestamp() {
        return earlierInterpretationTimestamp;
    }

    public Long getLaterInterpretationTimestamp() {
        return laterInterpretationTimestamp;
    }
}
