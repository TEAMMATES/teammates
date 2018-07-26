package teammates.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * DecimalFormat with TEAMMATES specific defaults (i.e. US locale).
 */
public final class TeammatesDecimalFormat {

    private TeammatesDecimalFormat() {
        // Utility class
    }

    public static String format(double doubleVal, String pattern) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(doubleVal);
    }
}
