package teammates.ui.output;

/**
 * API output of Feedback Session Stats.
 */
public class FeedbackSessionStatsData extends ApiOutput {

    private final int submittedTotal;
    private final int expectedTotal;

    public FeedbackSessionStatsData(int submittedTotal, int expectedTotal) {
        this.submittedTotal = submittedTotal;
        this.expectedTotal = expectedTotal;
    }

    public int getSubmittedTotal() {
        return submittedTotal;
    }

    public int getExpectedTotal() {
        return expectedTotal;
    }
}
