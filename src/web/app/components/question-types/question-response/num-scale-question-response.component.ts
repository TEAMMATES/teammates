import { Component } from '@angular/core';
import { FeedbackNumericalScaleResponseDetails, FeedbackQuestionType } from '../../../../types/api-output';
import { NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import { QuestionResponse } from './question-response';

/**
 * Numerical Scale question response.
 */
@Component({
  selector: 'tm-num-scale-question-response',
  templateUrl: './num-scale-question-response.component.html',
  styleUrls: ['./num-scale-question-response.component.scss'],
})
export class NumScaleQuestionResponseComponent extends QuestionResponse<FeedbackNumericalScaleResponseDetails> {

  constructor() {
    super({
      answer: NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED,
      questionType: FeedbackQuestionType.NUMSCALE,
    });
  }

}
