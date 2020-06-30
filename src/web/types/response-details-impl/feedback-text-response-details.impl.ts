import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType, FeedbackTextQuestionDetails, FeedbackTextResponseDetails,
} from '../api-output';
import { AbstractFeedbackResponseDetails } from './abstract-feedback-response-details';

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

  getResponseCsvAnswers(_: FeedbackTextQuestionDetails): string[][] {
    return [[StringHelper.getTextFromHtml(this.answer)]];
  }

}
