package teammates.logic.statistics;

import java.util.List;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatistics;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.User;

/**
 * Calculates backend-owned question statistics for results pages.
 *
 * @param <C> type of course-wide statistics
 * @param <R> type of recipient-specific statistics
 */
public interface FeedbackQuestionStatisticsCalculator<
        C extends FeedbackQuestionResultsStatistics,
        R extends FeedbackQuestionResultsStatistics> {

    /**
     * Calculates course-wide statistics for a question.
     */
    C calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle);

    /**
     * Calculates recipient-specific statistics for a question.
     */
    R calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, User recipient);
}
