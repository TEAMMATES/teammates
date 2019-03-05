import { Component } from '@angular/core';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
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
    super({
      answer: 0,
      questionType: FeedbackQuestionType.NUMSCALE,
    }, {
      minScale: 1,
      maxScale: 5,
      step: 0.5,
      questionType: FeedbackQuestionType.NUMSCALE,
      questionText: '',
    });
  }

}
