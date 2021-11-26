package teammates.test;

/**
 * Holds thread-related helper functions.
 */
public final class ThreadHelper {

    private ThreadHelper() {
        // utility class
    }

    /**
     * Makes the thread sleep for the specified time.
     */
    public static void waitFor(int timeInMilliSeconds) {
        try {
            Thread.sleep(timeInMilliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
