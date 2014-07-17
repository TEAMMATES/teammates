package teammates.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import teammates.common.exception.TeammatesException;

public class ThreadHelper {
    public static final int WAIT_DURATION = 20;
    private static Logger log = Utils.getLogger();

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
        new Throwable("").printStackTrace(new PrintWriter(sw));
        return "\n" + sw.toString();
    }

}
