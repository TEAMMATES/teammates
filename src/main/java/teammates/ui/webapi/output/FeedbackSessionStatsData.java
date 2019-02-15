package teammates.ui.webapi.output;

/**
 * Api output of Feedback Session Status to hold information
 * of submitted feedback responses status
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
