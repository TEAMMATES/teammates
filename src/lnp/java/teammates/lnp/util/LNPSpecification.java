package teammates.lnp.util;

/**
 * Stores the specifications for a LNP test, and verifies whether the results meet the criteria.
 */
public class LNPSpecification {

    /**
     * Maximum allowable threshold for the ratio of failed request
     * (between 0 and 1) to the test endpoint.
     */
    private double errorRateLimit;

    /**
     * Maximum allowable threshold for the mean response time
     * (in seconds) for the test endpoint.
     */
    private double meanResTimeLimit;

    private String resultsErrorMessage = "";

    // This class should always be constructed using builder() instead of constructor
    private LNPSpecification() {
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

    /**
     * Returns a builder for {@link LNPSpecification}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class for {@link LNPSpecification}.
     */
    public static class Builder {

        private LNPSpecification specification;

        private Builder() {
            specification = new LNPSpecification();
        }

        //CHECKSTYLE.OFF:MissingJavadocMethod
        public Builder withErrorRateLimit(double errorRateLimit) {
            specification.errorRateLimit = errorRateLimit;
            return this;
        }

        public Builder withMeanRespTimeLimit(double meanResTimeLimit) {
            specification.meanResTimeLimit = meanResTimeLimit;
            return this;
        }

        public LNPSpecification build() {
            return specification;
        }
        //CHECKSTYLE.ON:MissingJavadocMethod
    }
}
