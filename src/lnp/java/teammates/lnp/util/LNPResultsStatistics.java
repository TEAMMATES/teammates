package teammates.lnp.util;

import org.testng.Reporter;

/**
 * Represents the L&P test results statistics.
 */
public class LNPResultsStatistics {

    private double errorPct;
    private double meanResTime;
    private double pct1ResTime; // 90th percentile
    private double throughput;
    private int errorCount;
    private int sampleCount;

    /**
     * Display the statistics from a given test result.
     */
    public void displayLnpResultsStatistics() {
        Reporter.log(formatResultsStats(), true);
    }

    public double getMeanResTime() {
        return this.meanResTime;
    }

    public double getErrorPct() {
        return this.errorPct;
    }

    /**
     * Reorganise existing result statistics into one line with labels.
     */
    private String formatResultsStats() {
        return "\n#Req: " + sampleCount
                + ",  Throughput: " + String.format("%.2f", throughput) + "/s"
                + ",  Avg resp time: " + String.format("%.2f", meanResTime / 1000) + "s"
                + ",  90th percentile: " + String.format("%.2f", pct1ResTime / 1000) + "s"
                + ",  Err: " + errorCount + " (" + String.format("%.2f", errorPct) + "%)";
    }
}
