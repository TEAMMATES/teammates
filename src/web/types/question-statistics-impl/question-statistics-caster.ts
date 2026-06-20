import {
  FeedbackContributionCourseWideStatistics,
  FeedbackContributionRecipientStatistics,
  FeedbackMcqMsqCourseWideStatistics,
  FeedbackMcqMsqRecipientStatistics,
  FeedbackQuestionRecipientResultsStatistics,
  FeedbackQuestionResultsStatistics,
  FeedbackQuestionResultsStatisticsView,
  FeedbackQuestionType,
} from '../api-output';

export class QuestionStatisticsTypeChecker {
  static isContributionCourseWide(
    s: FeedbackQuestionResultsStatistics | FeedbackQuestionRecipientResultsStatistics | undefined,
  ): s is FeedbackContributionCourseWideStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.COURSE_WIDE &&
      s.questionType === FeedbackQuestionType.CONTRIB
    );
  }

  static isContributionRecipient(
    s: FeedbackQuestionResultsStatistics | FeedbackQuestionRecipientResultsStatistics | undefined,
  ): s is FeedbackContributionRecipientStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.RECIPIENT &&
      s.questionType === FeedbackQuestionType.CONTRIB
    );
  }

  static isMcqMsqCourseWide(
    s: FeedbackQuestionResultsStatistics | FeedbackQuestionRecipientResultsStatistics | undefined,
  ): s is FeedbackMcqMsqCourseWideStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.COURSE_WIDE &&
      (s.questionType === FeedbackQuestionType.MCQ || s.questionType === FeedbackQuestionType.MSQ)
    );
  }

  static isMcqMsqRecipient(
    s: FeedbackQuestionResultsStatistics | FeedbackQuestionRecipientResultsStatistics | undefined,
  ): s is FeedbackMcqMsqRecipientStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.RECIPIENT &&
      (s.questionType === FeedbackQuestionType.MCQ || s.questionType === FeedbackQuestionType.MSQ)
    );
  }
}
