import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionType,
} from '../api-output';
import { NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from '../feedback-response-details';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackNumericalScaleResponseDetails}.
 */
export class FeedbackNumericalScaleResponseDetailsImpl
    extends AbstractFeedbackResponseDetails<FeedbackNumericalScaleQuestionDetails>
    implements FeedbackNumericalScaleResponseDetails {

  answer: number = NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED;
  questionType: FeedbackQuestionType = FeedbackQuestionType.NUMSCALE;

  constructor(apiOutput: FeedbackNumericalScaleResponseDetails) {
    super();
    this.answer = apiOutput.answer;
  }

  getResponseCsvAnswers(_: FeedbackNumericalScaleQuestionDetails): string[][] {
    const answer: number = this.answer;
    // up to three decimal places
    const roundedAnswer: number = Math.round((answer + Number.EPSILON) * 1000) / 1000;
    return [[String(roundedAnswer)]];
  }

}
