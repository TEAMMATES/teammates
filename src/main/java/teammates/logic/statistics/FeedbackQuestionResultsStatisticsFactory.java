package teammates.logic.statistics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatistics;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Dispatcher for backend-owned results statistics calculation.
 */
public final class FeedbackQuestionResultsStatisticsFactory {
    private static final FeedbackConstsumOptionsQuestionStatisticsCalculator CONSTSUM_OPTIONS_CALCULATOR =
            new FeedbackConstsumOptionsQuestionStatisticsCalculator();
    private static final FeedbackConstsumRecipientsQuestionStatisticsCalculator CONSTSUM_RECIPIENTS_CALCULATOR =
            new FeedbackConstsumRecipientsQuestionStatisticsCalculator();
    private static final FeedbackContributionQuestionStatisticsCalculator CONTRIBUTION_CALCULATOR =
            new FeedbackContributionQuestionStatisticsCalculator();
    private static final FeedbackMcqMsqQuestionStatisticsCalculator MCQ_MSQ_CALCULATOR =
            new FeedbackMcqMsqQuestionStatisticsCalculator();
    private static final FeedbackNumScaleQuestionStatisticsCalculator NUMSCALE_CALCULATOR =
            new FeedbackNumScaleQuestionStatisticsCalculator();
    private static final FeedbackRubricQuestionStatisticsCalculator RUBRIC_CALCULATOR =
            new FeedbackRubricQuestionStatisticsCalculator();

    private FeedbackQuestionResultsStatisticsFactory() {
        // utility class
    }

    /**
     * Calculates course-wide statistics.
     */
    public static FeedbackQuestionResultsStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        return switch (question.getQuestionType()) {
        case CONSTSUM_OPTIONS -> CONSTSUM_OPTIONS_CALCULATOR.calculateCourseWide(question, responses, bundle);
        case CONSTSUM_RECIPIENTS -> CONSTSUM_RECIPIENTS_CALCULATOR.calculateCourseWide(question, responses, bundle);
        case CONTRIB -> CONTRIBUTION_CALCULATOR.calculateCourseWide(question, responses, bundle);
        case MCQ, MSQ -> MCQ_MSQ_CALCULATOR.calculateCourseWide(question, responses, bundle);
        case NUMSCALE -> NUMSCALE_CALCULATOR.calculateCourseWide(question, responses, bundle);
        case RUBRIC -> RUBRIC_CALCULATOR.calculateCourseWide(question, responses, bundle);
        default -> null;
        };
    }

    /**
     * Calculates recipient-specific statistics.
     */
    public static FeedbackQuestionResultsStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, User recipient) {
        return switch (question.getQuestionType()) {
        case CONSTSUM_OPTIONS ->
                CONSTSUM_OPTIONS_CALCULATOR.calculateForRecipient(question, responses, bundle, recipient);
        case CONSTSUM_RECIPIENTS ->
                CONSTSUM_RECIPIENTS_CALCULATOR.calculateForRecipient(question, responses, bundle, recipient);
        case CONTRIB -> CONTRIBUTION_CALCULATOR.calculateForRecipient(question, responses, bundle, recipient);
        case MCQ, MSQ -> MCQ_MSQ_CALCULATOR.calculateForRecipient(question, responses, bundle, recipient);
        case NUMSCALE -> NUMSCALE_CALCULATOR.calculateForRecipient(question, responses, bundle, recipient);
        case RUBRIC -> RUBRIC_CALCULATOR.calculateForRecipient(question, responses, bundle, recipient);
        default -> null;
        };
    }

    /**
     * Calculates normalized contribution response values keyed by response ID.
     */
    public static Map<UUID, Integer> calculateNormalizedContributionResponseValues(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, Student recipient) {
        if (question.getQuestionType() != FeedbackQuestionType.CONTRIB) {
            return Map.of();
        }

        return CONTRIBUTION_CALCULATOR.calculateNormalizedResponseValues(responses, bundle, recipient);
    }
}
