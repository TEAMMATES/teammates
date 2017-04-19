package teammates.client.scripts.util;

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

    /**
     * Gets the time elapsed since the start.
     */
    public double getTimeElapsedInSeconds() {
        return (System.nanoTime() - startTime) / 1000000000.0;
    }
}
