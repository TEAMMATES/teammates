package teammates.common.util;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * DateTimeFormatter with TEAMMATES specific defaults (i.e. US locale).
 */
public final class TeammatesDateTimeFormatter {

    private TeammatesDateTimeFormatter() {
        // utility class
    }

    /**
     * Returns DateTimeFormatter instance with Locale.US as default locale.
     */
    public static DateTimeFormatter ofPattern(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.US);
    }
}
