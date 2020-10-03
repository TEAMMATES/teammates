package teammates.ui.webapi;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.output.LocalDateTimeInfo;

/**
 * Resolve local date time under certain timezone to an UNIX timestamp.
 */
class GetLocalDateTimeInfoAction extends Action {

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Only instructor can get local date time information");
        }
    }

    @Override
    JsonResult execute() {
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

        LocalDateTimeInfo ldtInfo = null;
        switch(LocalDateTimeInfo.LocalDateTimeAmbiguityStatus.of(localDateTime, zoneId)) {
        case UNAMBIGUOUS:
            ldtInfo = LocalDateTimeInfo.unambiguous(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
            break;
        case GAP:
            ldtInfo = LocalDateTimeInfo.gap(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
            break;
        case OVERLAP:
            Instant earlierInterpretation = localDateTime.atZone(zoneId).withEarlierOffsetAtOverlap().toInstant();
            Instant laterInterpretation = localDateTime.atZone(zoneId).withLaterOffsetAtOverlap().toInstant();
            ldtInfo = LocalDateTimeInfo.overlap(localDateTime.atZone(zoneId).toInstant().toEpochMilli(),
                    earlierInterpretation.toEpochMilli(), laterInterpretation.toEpochMilli());
            break;
        default:
            Assumption.fail("Unreachable case");
            break;
        }

        return new JsonResult(ldtInfo);
    }
}
