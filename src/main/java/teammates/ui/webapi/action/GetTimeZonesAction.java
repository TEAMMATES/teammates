package teammates.ui.webapi.action;

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRulesProvider;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.ui.webapi.output.TimeZonesData;

/**
 * Action: get supported time zones.
 */
public class GetTimeZonesAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String tzVersion = ZoneRulesProvider.getVersions("UTC").firstKey();
        Instant now = Instant.now();
        Map<String, Integer> tzOffsets = new TreeMap<>();
        for (String tz : ZoneId.getAvailableZoneIds()) {
            if (!tz.contains("SystemV")) {
                int offset = ZoneId.of(tz).getRules().getOffset(now).getTotalSeconds();
                tzOffsets.put(tz, offset);
            }
        }
        TimeZonesData output = new TimeZonesData(tzVersion, tzOffsets);
        return new JsonResult(output);
    }
}
