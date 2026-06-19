package teammates.logic.statistics;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.statistics.FeedbackQuestionRecipientResultsStatistics;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatistics;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;

/**
 * Calculates backend-owned question statistics for results pages.
 *
 * @param <C> type of course-wide statistics
 * @param <R> type of recipient-specific statistics
 */
public interface FeedbackQuestionStatisticsCalculator<
        C extends FeedbackQuestionResultsStatistics,
        R extends FeedbackQuestionRecipientResultsStatistics> {

    /**
     * Calculates course-wide statistics for a question.
     */
    C calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle);

    /**
     * Calculates recipient-specific statistics for a question.
     */
    R calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, UUID recipientId);
}
