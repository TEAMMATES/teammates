package teammates.ui.newcontroller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRulesProvider;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.ui.webapi.output.ApiOutput;

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
        TimezoneData output = new TimezoneData(tzVersion, tzOffsets);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetTimeZonesAction}.
     */
    public static class TimezoneData extends ApiOutput {

        private final String version;
        private final Map<String, Integer> offsets;

        public TimezoneData(String version, Map<String, Integer> offsets) {
            this.version = version;
            this.offsets = offsets;
        }

        public String getVersion() {
            return version;
        }

        public Map<String, Integer> getOffsets() {
            return offsets;
        }

    }

}
