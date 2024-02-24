import { Component } from '@angular/core';
import { QuestionResponse } from './question-response';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_NUMSCALE_QUESTION_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';

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
