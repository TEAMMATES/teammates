package teammates.e2e.util;

/**
 * Result statistics class for L&P tests.
 */
public class ResultsStatistics {

    private double meanResTime;
    private double pct1ResTime; // 90th percentile
    private double throughput;
    private int errorCount;
    private int sampleCount;
    private String resultsFeedback = "";
    private String testName;

    /**
     * Generate a feedback message based on results statistics with the provided threshold.
     */
    public void generateResultsFeedback(double errorRateLimit, double meanResTimeLimit) {
        checkErrorLimit(errorRateLimit);
        checkMeanResTimeLimit(meanResTimeLimit);
        if ("".equals(resultsFeedback)) {
            System.out.println("You have successfully passed the default profiling threshold for " + testName);
        } else {
            throw new AssertionError(resultsFeedback);
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
            resultsFeedback += " You caused " + exceededMeanResTime + "ms higher in mean response time.\n";
        }
    }

    /**
     * Checks if the error rate exceeds the limited error percentage specified.
     */
    private void checkErrorLimit(double errorRateLimit) {
        if (errorRateLimit < getErrorRate()) {
            double exceededErrorRate = getErrorRate() - errorRateLimit;
            resultsFeedback += " You caused " + exceededErrorRate + "% higher in errors.\n";
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
                                + ",  Q1: " + String.format("%.2f", pct1ResTime) + "ms"
                                + ",  Err: " + errorCount + " (" + String.format("%.2f", getErrorRate()) + "%)";
    }
}
