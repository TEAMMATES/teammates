import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import { FeedbackConstantSumResponseDetailsImpl } from './feedback-constsum-response-details.impl';
import { FeedbackContributionResponseDetailsImpl } from './feedback-contribution-response-details.impl';
import { FeedbackMcqResponseDetailsImpl } from './feedback-mcq-response-details.impl';
import { FeedbackMsqResponseDetailsImpl } from './feedback-msq-response-details.impl';
import { FeedbackNumericalScaleResponseDetailsImpl } from './feedback-num-scale-response-details.impl';
import { FeedbackRankOptionsResponseDetailsImpl } from './feedback-rank-options-response-details.impl';
import { FeedbackRankRecipientsResponseDetailsImpl } from './feedback-rank-recipients-response-details.impl';
import { FeedbackRubricResponseDetailsImpl } from './feedback-rubric-response-details.impl';
import { FeedbackTextResponseDetailsImpl } from './feedback-text-response-details.impl';
import {
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponseDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
} from '../api-output';

/**
 * Factory to generate frontend response details implementation classes.
 */
export class FeedbackResponseDetailsFactory {

  /**
   * Converts API output ({@link FeedbackResponseDetails})
   * to frontend implementation class {@link AbstractFeedbackResponseDetails}.
   */
  static fromApiOutput(details: FeedbackResponseDetails):
      AbstractFeedbackResponseDetails<FeedbackQuestionDetails> {
    switch (details.questionType) {
      case FeedbackQuestionType.CONSTSUM:
        return new FeedbackConstantSumResponseDetailsImpl(details as FeedbackConstantSumResponseDetails);
      case FeedbackQuestionType.CONTRIB:
        return new FeedbackContributionResponseDetailsImpl(details as FeedbackContributionResponseDetails);
      case FeedbackQuestionType.MCQ:
        return new FeedbackMcqResponseDetailsImpl(details as FeedbackMcqResponseDetails);
      case FeedbackQuestionType.MSQ:
        return new FeedbackMsqResponseDetailsImpl(details as FeedbackMsqResponseDetails);
      case FeedbackQuestionType.NUMSCALE:
        return new FeedbackNumericalScaleResponseDetailsImpl(details as FeedbackNumericalScaleResponseDetails);
      case FeedbackQuestionType.RANK_OPTIONS:
        return new FeedbackRankOptionsResponseDetailsImpl(details as FeedbackRankOptionsResponseDetails);
      case FeedbackQuestionType.RANK_RECIPIENTS:
        return new FeedbackRankRecipientsResponseDetailsImpl(details as FeedbackRankRecipientsResponseDetails);
      case FeedbackQuestionType.RUBRIC:
        return new FeedbackRubricResponseDetailsImpl(details as FeedbackRubricResponseDetails);
      case FeedbackQuestionType.TEXT:
        return new FeedbackTextResponseDetailsImpl(details as FeedbackTextResponseDetails);
      default:
        throw new Error(`Unknown question type: ${details.questionType}`);
    }
  }

}
