import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';
import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType, FeedbackTextQuestionDetails, FeedbackTextResponseDetails,
} from '../api-output';

/**
 * Concrete implementation of {@link FeedbackTextResponseDetails}.
 */
export class FeedbackTextResponseDetailsImpl extends AbstractFeedbackResponseDetails<FeedbackTextQuestionDetails>
    implements FeedbackTextResponseDetails {

  answer: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.TEXT;

  constructor(apiOutput: FeedbackTextResponseDetails) {
    super();
    this.answer = apiOutput.answer;
  }

  getResponseCsvAnswers(): string[][] {
    return [[StringHelper.getTextFromHtml(this.answer)]];
  }

}
