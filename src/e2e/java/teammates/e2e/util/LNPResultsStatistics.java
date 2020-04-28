package teammates.e2e.util;

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
    private String resultsErrorMessage = "";

    /**
     * Verify the LNP results statistics with the specified threshold.
     */
    public void verifyLnpTestSuccess(double errorRateLimit, double meanResTimeLimit) {
        checkErrorLimit(errorRateLimit);
        checkMeanResTimeLimit(meanResTimeLimit);

        if (!resultsErrorMessage.isEmpty()) {
            throw new AssertionError(resultsErrorMessage);
        }
    }

    /**
     * Display the statistics from a given test result.
     */
    public void displayLnpResultsStatistics() {
        Reporter.log(formatResultsStats(), true);
    }

    /**
     * Checks if the mean response time exceeds the specified time limit.
     */
    private void checkMeanResTimeLimit(double meanResTimeLimit) {
        if (meanResTimeLimit < meanResTime / 1000) {
            double exceededMeanResTime = meanResTime / 1000 - meanResTimeLimit;
            resultsErrorMessage += "Avg resp time is " + String.format("%.2f", exceededMeanResTime)
                    + "s higher than the specified threshold.";
        }
    }

    /**
     * Checks if the error rate exceeds the specified error percentage limit.
     */
    private void checkErrorLimit(double errorRateLimit) {
        if (errorRateLimit < errorPct) {
            double exceededErrorRate = errorPct - errorRateLimit;
            resultsErrorMessage += "Error rate is " + String.format("%.2f", exceededErrorRate)
                    + "% higher than the specified threshold. ";
        }
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
