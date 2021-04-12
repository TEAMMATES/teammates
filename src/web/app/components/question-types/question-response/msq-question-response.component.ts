import { Component } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_MSQ_QUESTION_DETAILS,
  DEFAULT_MSQ_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { MSQ_ANSWER_NONE_OF_THE_ABOVE } from '../../../../types/feedback-response-details';
import { QuestionResponse } from './question-response';

/**
 * MSQ question response.
 */
@Component({
  selector: 'tm-msq-question-response',
  templateUrl: './msq-question-response.component.html',
  styleUrls: ['./msq-question-response.component.scss'],
})
export class MsqQuestionResponseComponent
    extends QuestionResponse<FeedbackMsqResponseDetails, FeedbackMsqQuestionDetails> {

  constructor() {
    super(DEFAULT_MSQ_RESPONSE_DETAILS(), DEFAULT_MSQ_QUESTION_DETAILS());
  }

  /**
   * Checks whether the MSQ answer is 'None of the above'.
   */
  get isNoneOfTheAboveAnswer(): boolean {
    return this.responseDetails.answers.length === 1
        && this.responseDetails.answers[0] === MSQ_ANSWER_NONE_OF_THE_ABOVE;
  }

}
