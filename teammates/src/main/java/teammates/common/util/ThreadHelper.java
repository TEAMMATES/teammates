package teammates.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import teammates.common.exception.TeammatesException;

public final class ThreadHelper {
    public static final int WAIT_DURATION = 20;
    private static final Logger log = Logger.getLogger();

    private ThreadHelper() {
        // utility class
    }

    public static void waitBriefly() {
        try {
            Thread.sleep(ThreadHelper.WAIT_DURATION);
        } catch (InterruptedException e) {
            log.severe(TeammatesException.toStringWithStackTrace(e));
        }
    }

    /**
     * Makes the thread sleep for the specified time.
     */
    public static void waitFor(int timeInMilliSeconds) {
        try {
            Thread.sleep(timeInMilliSeconds);
        } catch (InterruptedException e) {
            log.severe(TeammatesException.toStringWithStackTrace(e));
        }
    }

    public static String getCurrentThreadStack() {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            new Throwable("").printStackTrace(pw);
            return "\n" + sw.toString();
        }
    }

}
