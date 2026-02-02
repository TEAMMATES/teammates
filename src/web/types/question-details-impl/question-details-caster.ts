import {
  FeedbackConstantSumQuestionDetails,
  FeedbackContributionQuestionDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMsqQuestionDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackQuestionDetails,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRubricQuestionDetails,
  FeedbackTextQuestionDetails,
} from '../api-output';

export class QuestionDetailsCaster {
  static constSum(d: FeedbackQuestionDetails): FeedbackConstantSumQuestionDetails {
    return d as FeedbackConstantSumQuestionDetails;
  }

  static contrib(d: FeedbackQuestionDetails): FeedbackContributionQuestionDetails {
    return d as FeedbackContributionQuestionDetails;
  }

  static mcq(d: FeedbackQuestionDetails): FeedbackMcqQuestionDetails {
    return d as FeedbackMcqQuestionDetails;
  }

  static msq(d: FeedbackQuestionDetails): FeedbackMsqQuestionDetails {
    return d as FeedbackMsqQuestionDetails;
  }

  static numscale(d: FeedbackQuestionDetails): FeedbackNumericalScaleQuestionDetails {
    return d as FeedbackNumericalScaleQuestionDetails;
  }

  static rankOptions(d: FeedbackQuestionDetails): FeedbackRankOptionsQuestionDetails {
    return d as FeedbackRankOptionsQuestionDetails;
  }

  static rankRecipients(d: FeedbackQuestionDetails): FeedbackRankRecipientsQuestionDetails {
    return d as FeedbackRankRecipientsQuestionDetails;
  }

  static rubric(d: FeedbackQuestionDetails): FeedbackRubricQuestionDetails {
    return d as FeedbackRubricQuestionDetails;
  }

  static text(d: FeedbackQuestionDetails): FeedbackTextQuestionDetails {
    return d as FeedbackTextQuestionDetails;
  }
}
