package teammates.client.scripts.util;

import teammates.common.util.Logger;

/**
 * Helper class to measure event durations.
 */
public class Stopwatch {
    private long startTime;

    /**
     * Starts the Stopwatch.
     */
    public void start() {
        startTime = System.nanoTime();
    }

    public double getTimeElapsedInSeconds() {
        return (System.nanoTime() - startTime) / 1000000000.0;
    }

    /**
     * Logs the time elapsed since the start using the supplied logger.
     */
    public void logTimeElapsedInSeconds(Logger log) {
        log.info("Time taken: " + getTimeElapsedInSeconds());
    }
}
