package teammates.ui.webapi;

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRulesProvider;
import java.util.Map;
import java.util.TreeMap;

import teammates.ui.output.TimeZonesData;

/**
 * Action: get supported time zones.
 */
public class GetTimeZonesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isMaintainer && !userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Only Maintainers or Admin are allowed to access this resource.");
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
