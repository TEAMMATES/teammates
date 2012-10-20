package teammates.test.scripts;

import teammates.common.Common;


/**
 * NOTE: remove all the BackDoor(BD) add/delete methods from 
 * PerformanceProfiler as they will cause data regression
 * 
 * Each profiler will have a separate report ( thead1.txt,thread2.txt....)
 *
 */
public class ParallelProfiler {
	// The number of threads cannot be more than capacity in the BrowserInstancePool
	// modify that constant if needed
	private static final int NUM_OF_THREADS = 4;
    public static void main(String args[]) {
        for (int i = 0; i < NUM_OF_THREADS ; i ++)
        {
        	(new PerformanceProfiler(Common.TEST_DATA_FOLDER + "/thread"+i+".txt")).start();
        }
    }
}
