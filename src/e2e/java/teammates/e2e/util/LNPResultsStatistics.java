package teammates.e2e.util;

import org.testng.Reporter;

/**
 * Represents the L&P test results statistics.
 */
public class LNPResultsStatistics {

    private double errPct;
    private double meanResTime;
    private double pct1ResTime; // 90th percentile
    private double throughput;
    private int errorCount;
    private int sampleCount;
    private String resultsErrorMessage;
    private String testName;

    /**
     * Display the feedback message based on LNP results statistics with the provided threshold.
     */
    public void displayLnpResultsStatistics(double errorRateLimit, double meanResTimeLimit) {
        checkErrorLimit(errorRateLimit);
        checkMeanResTimeLimit(meanResTimeLimit);
        if (resultsErrorMessage != null) {
            throw new AssertionError(resultsErrorMessage);
        }
    }

    /**
     * Generate the statistics from a given the test result.
     */
    public void generateResultsStatistics() {
        Reporter.log(formatResultsAnalysis(), true);
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Checks if the mean response time exceeds the limited time specified.
     */
    private void checkMeanResTimeLimit(double meanResTimeLimit) {
        if (meanResTimeLimit < meanResTime / 1000) {
            double exceededMeanResTime = meanResTime / 1000 - meanResTimeLimit;
            resultsErrorMessage += "Avg resp time is " + String.format("%.2f", exceededMeanResTime)
                    + "s higher than the threshold.";
        }
    }

    /**
     * Checks if the error rate exceeds the limited error percentage specified.
     */
    private void checkErrorLimit(double errorRateLimit) {
        if (errorRateLimit < errPct * 100) {
            double exceededErrorRate = errPct * 100 - errorRateLimit;
            resultsErrorMessage += "Error rate is " + String.format("%.2f", exceededErrorRate)
                    + "percent higher than the threshold.";
        }
    }

    /**
     * Reorganise existing result statistics into one line with labels.
     */
    private String formatResultsAnalysis() {
        return testName + "Results = "
                                + " #Req: " + sampleCount
                                + ",  Throughput: " + String.format("%.2f", throughput) + "/s"
                                + ",  Avg resp time: " + String.format("%.2f", meanResTime / 1000) + "s"
                                + ",  90th percentile: " + String.format("%.2f", pct1ResTime / 1000) + "s"
                                + ",  Err: " + errorCount + " (" + String.format("%.2f", errPct * 100) + "%)";
    }
}
