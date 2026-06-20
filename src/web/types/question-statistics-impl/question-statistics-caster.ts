import {
  FeedbackConstsumOptionsStatistics,
  FeedbackConstsumRecipientsStatistics,
  FeedbackContributionCourseWideStatistics,
  FeedbackContributionRecipientStatistics,
  FeedbackMcqMsqCourseWideStatistics,
  FeedbackMcqMsqRecipientStatistics,
  FeedbackNumScaleStatistics,
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

  static isConstsumOptions(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackConstsumOptionsStatistics {
    return s?.questionType === FeedbackQuestionType.CONSTSUM_OPTIONS;
  }

  static isConstsumRecipients(
    s: FeedbackQuestionResultsStatistics | undefined,
  ): s is FeedbackConstsumRecipientsStatistics {
    return s?.questionType === FeedbackQuestionType.CONSTSUM_RECIPIENTS;
  }

  static isNumscale(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackNumScaleStatistics {
    return s?.questionType === FeedbackQuestionType.NUMSCALE;
  }

  static isMcqMsq(
    s: FeedbackQuestionResultsStatistics | undefined,
  ): s is FeedbackMcqMsqCourseWideStatistics | FeedbackMcqMsqRecipientStatistics {
    return s?.questionType === FeedbackQuestionType.MCQ || s?.questionType === FeedbackQuestionType.MSQ;
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

  static isRubric(s: FeedbackQuestionResultsStatistics | undefined): s is FeedbackRubricStatistics {
    return s?.questionType === FeedbackQuestionType.RUBRIC;
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
