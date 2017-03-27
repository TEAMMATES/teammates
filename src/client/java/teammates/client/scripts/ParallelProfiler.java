package teammates.client.scripts;

import teammates.test.driver.TestProperties;

/**
 * Usage: to run multiple instances of PerformanceProfiler.java in parallel.
 *
 * <p>Notes:
 * <ul>
 * <li>Read instructions in PerformanceProfiler.java</li>
 * <li>Remove all the BackDoor(BD) add/delete methods from PerformanceProfiler as they will cause data regression</li>
 * <li>Each profiler will have a separate report (thead1.txt,thread2.txt, etc)</li>
 * <li>The number of threads cannot be more than capacity in the BrowserInstancePool; modify that constant if needed</li>
 * </ul>
 */
public final class ParallelProfiler {

    private static final int NUM_OF_THREADS = 4;

    private ParallelProfiler() {
        // script, not meant to be instantiated
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            new PerformanceProfiler(TestProperties.TEST_DATA_FOLDER + "/thread" + i + ".txt").start();
        }
    }
}
