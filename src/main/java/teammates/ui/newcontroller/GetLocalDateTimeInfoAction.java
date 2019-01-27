package teammates.ui.newcontroller;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

/**
 * Resolve local date time under certain timezone to an UNIX timestamp.
 */
public class GetLocalDateTimeInfoAction extends Action {

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor()) {
            throw new UnauthorizedAccessException("Only instructor can get local date time information");
        }
    }

    @Override
    public ActionResult execute() {
        String localDateTimeStr = getNonNullRequestParamValue(Const.ParamsNames.LOCAL_DATE_TIME);
        String zoneIdStr = getNonNullRequestParamValue(Const.ParamsNames.TIME_ZONE);

        LocalDateTime localDateTime = null;
        ZoneId zoneId = null;

        try {
            localDateTime = LocalDateTime.parse(localDateTimeStr, LOCAL_DATE_TIME_FORMATTER);
            zoneId = ZoneId.of(zoneIdStr);
        } catch (DateTimeException e) {
            throw new InvalidHttpParameterException(e.getMessage(), e);
        }

        LocalDateTimeInfo localDateTimeInfo = null;
        switch(TimeHelper.LocalDateTimeAmbiguityStatus.of(localDateTime, zoneId)) {
        case UNAMBIGUOUS:
            localDateTimeInfo = LocalDateTimeInfo.unambiguous(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
            break;
        case GAP:
            localDateTimeInfo = LocalDateTimeInfo.gap(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
            break;
        case OVERLAP:
            Instant earlierInterpretation = localDateTime.atZone(zoneId).withEarlierOffsetAtOverlap().toInstant();
            Instant laterInterpretation = localDateTime.atZone(zoneId).withLaterOffsetAtOverlap().toInstant();
            localDateTimeInfo = LocalDateTimeInfo.overlap(localDateTime.atZone(zoneId).toInstant().toEpochMilli(),
                    earlierInterpretation.toEpochMilli(), laterInterpretation.toEpochMilli());
            break;
        default:
            Assumption.fail("Unreachable case");
            break;
        }

        return new JsonResult(localDateTimeInfo);
    }

    /**
     * Output format for {@link GetLocalDateTimeInfoAction}.
     */
    public static class LocalDateTimeInfo extends ActionResult.ActionOutput {

        private long resolvedTimestamp;
        private TimeHelper.LocalDateTimeAmbiguityStatus resolvedStatus;

        private Long earlierInterpretationTimestamp;
        private Long laterInterpretationTimestamp;

        /**
         * Constructs {@link LocalDateTimeInfo} with UNAMBIGUOUS status.
         */
        public static LocalDateTimeInfo unambiguous(long resolvedTimestamp) {
            LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

            localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.UNAMBIGUOUS;
            localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

            return localDateTimeInfo;
        }

        /**
         * Constructs {@link LocalDateTimeInfo} with GAP status.
         */
        public static LocalDateTimeInfo gap(long resolvedTimestamp) {
            LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

            localDateTimeInfo.resolvedStatus = TimeHelper.LocalDateTimeAmbiguityStatus.GAP;
            localDateTimeInfo.resolvedTimestamp = resolvedTimestamp;

            return localDateTimeInfo;
        }

        /**
         * Constructs {@link LocalDateTimeInfo} with OVERLAP status.
         */
        public static LocalDateTimeInfo overlap(long resolvedTimestamp,
                                                long earlierInterpretationTimestamp, long laterInterpretationTimestamp) {
            LocalDateTimeInfo localDateTimeInfo = new LocalDateTimeInfo();

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
}
