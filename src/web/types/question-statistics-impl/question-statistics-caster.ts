import {
  FeedbackContributionCourseWideStatistics,
  FeedbackContributionRecipientStatistics,
  FeedbackMcqMsqCourseWideStatistics,
  FeedbackMcqMsqRecipientStatistics,
  FeedbackQuestionResultsStatistics,
  FeedbackQuestionResultsStatisticsView,
  FeedbackQuestionType,
  FeedbackRubricStatistics,
} from '../api-output';

export class QuestionStatisticsTypeChecker {
  static isContributionCourseWide(
    s: FeedbackQuestionResultsStatistics | undefined,
  ): s is FeedbackContributionCourseWideStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.COURSE_WIDE &&
      s.questionType === FeedbackQuestionType.CONTRIB
    );
  }

  static isContributionRecipient(
    s: FeedbackQuestionResultsStatistics | undefined,
  ): s is FeedbackContributionRecipientStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.RECIPIENT &&
      s.questionType === FeedbackQuestionType.CONTRIB
    );
  }

  static isMcqMsqCourseWide(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackMcqMsqCourseWideStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.COURSE_WIDE &&
      (s.questionType === FeedbackQuestionType.MCQ || s.questionType === FeedbackQuestionType.MSQ)
    );
  }

  static isMcqMsqRecipient(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackMcqMsqRecipientStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.RECIPIENT &&
      (s.questionType === FeedbackQuestionType.MCQ || s.questionType === FeedbackQuestionType.MSQ)
    );
  }

  static isRubricCourseWide(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackRubricStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.COURSE_WIDE &&
      s.questionType === FeedbackQuestionType.RUBRIC
    );
  }

  static isRubricRecipient(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackRubricStatistics {
    return (
      s?.statisticsView === FeedbackQuestionResultsStatisticsView.RECIPIENT &&
      s.questionType === FeedbackQuestionType.RUBRIC
    );
  }
}
