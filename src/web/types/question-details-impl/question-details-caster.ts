import {
  FeedbackConstantSumQuestionDetails,
  FeedbackContributionQuestionDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMsqQuestionDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRubricQuestionDetails,
  FeedbackTextQuestionDetails,
} from '../api-output';

export class QuestionDetailsTypeChecker {
  static isConstSumOptions(d: FeedbackQuestionDetails): d is FeedbackConstantSumQuestionDetails {
    return d.questionType === FeedbackQuestionType.CONSTSUM_OPTIONS;
  }

  static isConstSumRecipients(d: FeedbackQuestionDetails): d is FeedbackConstantSumQuestionDetails {
    return d.questionType === FeedbackQuestionType.CONSTSUM_RECIPIENTS;
  }

  static isContrib(d: FeedbackQuestionDetails): d is FeedbackContributionQuestionDetails {
    return d.questionType === FeedbackQuestionType.CONTRIB;
  }

  static isMcq(d: FeedbackQuestionDetails): d is FeedbackMcqQuestionDetails {
    return d.questionType === FeedbackQuestionType.MCQ;
  }

  static isMsq(d: FeedbackQuestionDetails): d is FeedbackMsqQuestionDetails {
    return d.questionType === FeedbackQuestionType.MSQ;
  }

  static isNumscale(d: FeedbackQuestionDetails): d is FeedbackNumericalScaleQuestionDetails {
    return d.questionType === FeedbackQuestionType.NUMSCALE;
  }

  static isRankOptions(d: FeedbackQuestionDetails): d is FeedbackRankOptionsQuestionDetails {
    return d.questionType === FeedbackQuestionType.RANK_OPTIONS;
  }

  static isRankRecipients(d: FeedbackQuestionDetails): d is FeedbackRankRecipientsQuestionDetails {
    return d.questionType === FeedbackQuestionType.RANK_RECIPIENTS;
  }

  static isRubric(d: FeedbackQuestionDetails): d is FeedbackRubricQuestionDetails {
    return d.questionType === FeedbackQuestionType.RUBRIC;
  }

  static isText(d: FeedbackQuestionDetails): d is FeedbackTextQuestionDetails {
    return d.questionType === FeedbackQuestionType.TEXT;
  }
}
