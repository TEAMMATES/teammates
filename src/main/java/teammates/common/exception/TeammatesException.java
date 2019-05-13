package teammates.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Contains utility methods to process exceptions.
 */
@SuppressWarnings("serial")
public final class TeammatesException {

    private TeammatesException() {
        // Utility class
    }

    /**
     * Returns the throwable's printed stack trace as string.
     */
    public static String toStringWithStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
            return System.lineSeparator() + sw.toString();
        }
    }

}
