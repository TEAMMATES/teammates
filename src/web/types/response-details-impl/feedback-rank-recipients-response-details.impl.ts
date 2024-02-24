import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import {
  FeedbackQuestionType, FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../api-output';
import { RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED } from '../feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackRankRecipientsResponseDetails}.
 */
export class FeedbackRankRecipientsResponseDetailsImpl
    extends AbstractFeedbackResponseDetails<FeedbackRankRecipientsQuestionDetails>
    implements FeedbackRankRecipientsResponseDetails {

  answer: number = RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED;
  questionType: FeedbackQuestionType = FeedbackQuestionType.RANK_RECIPIENTS;

  constructor(apiOutput: FeedbackRankRecipientsResponseDetails) {
    super();
    this.answer = apiOutput.answer;
  }

  getResponseCsvAnswers(): string[][] {
    return [[String(this.answer)]];
  }

}
