package teammates.common.util;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * DateTimeFormatter for TEAMMATES with default Locale.US settings.
 */
public final class TeammatesDateTimeFormatter {

    private TeammatesDateTimeFormatter() {
        // Utility class
    }

    /**
     * Returns DateTimeFormatter instance with Locale.US as default locale.
     */
    public static DateTimeFormatter ofPattern(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.US);
    }
}
