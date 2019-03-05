import { Component } from '@angular/core';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_NUMSCALE_QUESTION_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { QuestionResponse } from './question-response';

/**
 * Numerical scale question response.
 */
@Component({
  selector: 'tm-num-scale-question-response',
  templateUrl: './num-scale-question-response.component.html',
  styleUrls: ['./num-scale-question-response.component.scss'],
})
export class NumScaleQuestionResponseComponent
    extends QuestionResponse<FeedbackNumericalScaleResponseDetails, FeedbackNumericalScaleQuestionDetails> {

  constructor() {
    super(DEFAULT_NUMSCALE_RESPONSE_DETAILS(), DEFAULT_NUMSCALE_QUESTION_DETAILS());
  }

}
