package teammates.client.scripts.util;

import teammates.common.util.Logger;

/**
 * Helper class to measure event durationss.
 */
public class Stopwatch {
    long startTime;

    // CHECKSTYLE.OFF:JavadocMethod
    public void start() {
        // CHECKSTYLE.ON:JavadocMethod
        startTime = System.nanoTime();
    }

    public double getTimeElapsedInSeconds() {
        return (System.nanoTime() - startTime) / 1000000000.0;
    }

    // CHECKSTYLE.OFF:JavadocMethod
    public void logTimeElapsedInSeconds(Logger logger) {
        // CHECKSTYLE.ON:JavadocMethod
        logger.info("Time taken: " + getTimeElapsedInSeconds());
    }
}
