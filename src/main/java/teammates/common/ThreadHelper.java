package teammates.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class ThreadHelper {
	public static final int WAIT_DURATION = 200;
	private static Logger log = Common.getLogger();

	public static void waitBriefly() {
		try {
			Thread.sleep(ThreadHelper.WAIT_DURATION);
		} catch (InterruptedException e) {
			log.severe(Common.stackTraceToString(e));
		}
	}

	/**
	 * Makes the thread sleep for the specified time. 
	 */
	public static void waitFor(int timeInMilliSeconds) {
		try {
			Thread.sleep(timeInMilliSeconds);
		} catch (InterruptedException e) {
			log.severe(Common.stackTraceToString(e));
		}
	}

	public static String getCurrentThreadStack() {
		StringWriter sw = new StringWriter();
		new Throwable("").printStackTrace(new PrintWriter(sw));
		return "\n" + sw.toString();
	}

}
