package teammates.e2e.util;

/**
 * Represents the L&P test results statistics.
 */
public class LNPResultsStatistics {

    private double meanResTime;
    private double pct1ResTime; // 90th percentile
    private double throughput;
    private int errorCount;
    private int sampleCount;
    private String resultsErrorMessage = null;
    private String testName;

    /**
     * Display the feedback message based on LNP results statistics with the provided threshold.
     */
    public void displayLnpResultsStatistics(double errorRateLimit, double meanResTimeLimit) {
        checkErrorLimit(errorRateLimit);
        checkMeanResTimeLimit(meanResTimeLimit);
        if ("".equals(resultsErrorMessage)) {
            System.out.println("You have successfully passed the default profiling threshold for " + testName);
        } else {
            throw new AssertionError(resultsErrorMessage);
        }
    }

    /**
     * Generate the statistics from a given the test result.
     */
    public void generateResultsStatistics() {
        System.out.println(formatResultsAnalysis());
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Checks if the mean response time exceeds the limited time specified.
     */
    private void checkMeanResTimeLimit(double meanResTimeLimit) {
        if (meanResTimeLimit < pct1ResTime) {
            double exceededMeanResTime = pct1ResTime - meanResTimeLimit;
            resultsErrorMessage += "Avg resp time is " + String.format("%.2f", exceededMeanResTime)
                    + "ms higher than the threshold.";
        }
    }

    /**
     * Checks if the error rate exceeds the limited error percentage specified.
     */
    private void checkErrorLimit(double errorRateLimit) {
        if (errorRateLimit < getErrorRate()) {
            double exceededErrorRate = getErrorRate() - errorRateLimit;
            resultsErrorMessage += "Error rate is " + String.format("%.2f", exceededErrorRate)
                    + "percent higher than the threshold.";
        }
    }

    private double getErrorRate() {
        return (double) errorCount / sampleCount;
    }

    /**
     * Reorganise existing result statistics into one line with labels.
     */
    private String formatResultsAnalysis() {
        return testName + "Results = "
                                + " #Req: " + sampleCount
                                + ",  Throughput: " + String.format("%.2f", throughput) + "/s"
                                + ",  Avg resp time: " + String.format("%.2f", meanResTime) + "ms"
                                + ",  90th percentile: " + String.format("%.2f", pct1ResTime) + "ms"
                                + ",  Err: " + errorCount + " (" + String.format("%.2f", getErrorRate()) + "%)";
    }
}
