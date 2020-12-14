package teammates.ui.output;

import java.util.Map;

/**
 * The API output format for available timezones and their offsets-in-seconds.
 */
public class TimeZonesData extends ApiOutput {
    private String version;
    private Map<String, Integer> offsets; // timezone name => offset from UTC in seconds

    public TimeZonesData(String version, Map<String, Integer> offsets) {
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
