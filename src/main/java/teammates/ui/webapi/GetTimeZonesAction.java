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
class GetTimeZonesAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
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
