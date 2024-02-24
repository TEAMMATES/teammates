import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import { FeedbackConstantSumOptionsQuestionDetailsImpl } from './feedback-constsum-options-question-details.impl';
import { FeedbackConstantSumRecipientsQuestionDetailsImpl } from './feedback-constsum-recipient-question-details.impl';
import { FeedbackContributionQuestionDetailsImpl } from './feedback-contribution-question-details.impl';
import { FeedbackMcqQuestionDetailsImpl } from './feedback-mcq-question-details.impl';
import { FeedbackMsqQuestionDetailsImpl } from './feedback-msq-question-details.impl';
import { FeedbackNumericalScaleQuestionDetailsImpl } from './feedback-num-scale-question-details.impl';
import { FeedbackRankOptionsQuestionDetailsImpl } from './feedback-rank-options-question-details.impl';
import { FeedbackRankRecipientsQuestionDetailsImpl } from './feedback-rank-recipients-question-details.impl';
import { FeedbackRubricQuestionDetailsImpl } from './feedback-rubric-question-details.impl';
import { FeedbackTextQuestionDetailsImpl } from './feedback-text-question-details.impl';
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

/**
 * Factory to generate frontend question details implementation classes.
 */
export class FeedbackQuestionDetailsFactory {

  /**
   * Converts API output ({@link FeedbackQuestionDetails})
   * to frontend implementation class {@link AbstractFeedbackQuestionDetails}.
   */
  static fromApiOutput(details: FeedbackQuestionDetails): AbstractFeedbackQuestionDetails {
    switch (details.questionType) {
      case FeedbackQuestionType.CONSTSUM_OPTIONS:
        return new FeedbackConstantSumOptionsQuestionDetailsImpl(details as FeedbackConstantSumQuestionDetails);
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS:
        return new FeedbackConstantSumRecipientsQuestionDetailsImpl(details as FeedbackConstantSumQuestionDetails);
      case FeedbackQuestionType.CONTRIB:
        return new FeedbackContributionQuestionDetailsImpl(details as FeedbackContributionQuestionDetails);
      case FeedbackQuestionType.MCQ:
        return new FeedbackMcqQuestionDetailsImpl(details as FeedbackMcqQuestionDetails);
      case FeedbackQuestionType.MSQ:
        return new FeedbackMsqQuestionDetailsImpl(details as FeedbackMsqQuestionDetails);
      case FeedbackQuestionType.NUMSCALE:
        return new FeedbackNumericalScaleQuestionDetailsImpl(details as FeedbackNumericalScaleQuestionDetails);
      case FeedbackQuestionType.RANK_OPTIONS:
        return new FeedbackRankOptionsQuestionDetailsImpl(details as FeedbackRankOptionsQuestionDetails);
      case FeedbackQuestionType.RANK_RECIPIENTS:
        return new FeedbackRankRecipientsQuestionDetailsImpl(details as FeedbackRankRecipientsQuestionDetails);
      case FeedbackQuestionType.RUBRIC:
        return new FeedbackRubricQuestionDetailsImpl(details as FeedbackRubricQuestionDetails);
      case FeedbackQuestionType.TEXT:
        return new FeedbackTextQuestionDetailsImpl(details as FeedbackTextQuestionDetails);
      default:
        throw new Error(`Unknown question type: ${details.questionType}`);
    }
  }

}
