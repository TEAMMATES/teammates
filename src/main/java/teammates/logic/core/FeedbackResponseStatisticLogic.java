package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;

/**
 * Handles operations related to feedback response statistics.
 */
public class FeedbackResponseStatisticLogic {

	private static final FeedbackResponseStatisticLogic instance = new FeedbackResponseStatisticLogic();

	private final FeedbackResponseStatisticDb feedbackResponseStatisticDb = FeedbackResponseStatisticDb.inst();

	private FeedbackResponseStatisticLogic() {
		// prevent initialization
	}
	
	public static FeedbackResponseStatisticLogic inst() {
		return instance;
	}

	/**
     * Gets all feedback response statistics in time period.
     */
    public List<FeedbackResponseStatisticAttributes> getFeedbackResponseStatistics(Instant startTime, Instant endTime) {
        return feedbackResponseStatisticDb.getFeedbackResponseStatistics(Instant startTime, Instant endTime);
    }
}
