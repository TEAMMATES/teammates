package teammates.e2e.util;

/**
 * Stores the specifications for a LNP test, and verifies whether the results meet the criteria.
 */
public class LNPSpecification {

    private double errorRateLimit;
    private double meanResTimeLimit;
    private String resultsErrorMessage = "";

    /**
     * Create a specification class with the specified limits.
     * @param errorRateLimit Maximum allowable threshold for the percentage of failed requests
     *                       (0 to 100) to the test endpoint.
     * @param meanResTimeLimit Maximum allowable threshold for the mean response time
     *                         (in seconds) for the test endpoint.
     */
    public LNPSpecification(double errorRateLimit, double meanResTimeLimit) {
        this.errorRateLimit = errorRateLimit;
        this.meanResTimeLimit = meanResTimeLimit;
    }

    /**
     * Verify the LNP results statistics with the specified threshold.
     * @param resultStatistics {@link LNPResultsStatistics} object that contains
     *                         the result statistics from running this test.
     */
    public void verifyLnpTestSuccess(LNPResultsStatistics resultStatistics) {
        checkErrorLimit(resultStatistics.getErrorPct());
        checkMeanResTimeLimit(resultStatistics.getMeanResTime());

        if (!resultsErrorMessage.isEmpty()) {
            throw new AssertionError(resultsErrorMessage);
        }
    }

    /**
     * Checks if the mean response time exceeds the specified time limit.
     */
    private void checkMeanResTimeLimit(double meanResTime) {
        if (meanResTimeLimit < meanResTime / 1000) {
            double exceededMeanResTime = meanResTime / 1000 - meanResTimeLimit;
            resultsErrorMessage += "Avg resp time is " + String.format("%.2f", exceededMeanResTime)
                    + "s higher than the specified threshold.";
        }
    }

    /**
     * Checks if the error rate exceeds the specified error percentage limit.
     */
    private void checkErrorLimit(double errorPct) {
        if (errorRateLimit < errorPct) {
            double exceededErrorRate = errorPct - errorRateLimit;
            resultsErrorMessage += "Error rate is " + String.format("%.2f", exceededErrorRate)
                    + "% higher than the specified threshold. ";
        }
    }
}
