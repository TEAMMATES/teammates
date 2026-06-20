package teammates.logic.statistics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.statistics.FeedbackQuestionRecipientResultsStatistics;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatistics;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;

/**
 * Dispatcher for backend-owned results statistics calculation.
 */
public final class FeedbackQuestionResultsStatisticsFactory {
    private static final FeedbackContributionQuestionStatisticsCalculator CONTRIBUTION_CALCULATOR =
            new FeedbackContributionQuestionStatisticsCalculator();

    private FeedbackQuestionResultsStatisticsFactory() {
        // utility class
    }

    /**
     * Calculates course-wide statistics.
     */
    public static FeedbackQuestionResultsStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        if (question.getQuestionType() != FeedbackQuestionType.CONTRIB) {
            return null;
        }

        return CONTRIBUTION_CALCULATOR.calculateCourseWide(question, responses, bundle);
    }

    /**
     * Calculates recipient-specific statistics.
     */
    public static FeedbackQuestionRecipientResultsStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, UUID recipientId) {
        if (question.getQuestionType() != FeedbackQuestionType.CONTRIB) {
            return null;
        }

        return CONTRIBUTION_CALCULATOR.calculateForRecipient(question, responses, bundle, recipientId);
    }

    /**
     * Calculates normalized contribution response values keyed by response ID.
     */
    public static Map<UUID, Integer> calculateNormalizedContributionResponseValues(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, UUID recipientId) {
        if (question.getQuestionType() != FeedbackQuestionType.CONTRIB) {
            return Map.of();
        }

        return CONTRIBUTION_CALCULATOR.calculateNormalizedResponseValues(responses, bundle, recipientId);
    }
}
