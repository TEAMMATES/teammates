import { Component } from '@angular/core';
import { QuestionAdditionalInfo } from './question-additional-info';
import { FeedbackNumericalScaleQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-additional-info',
  templateUrl: './num-scale-question-additional-info.component.html',
})
export class NumScaleQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackNumericalScaleQuestionDetails> {
  constructor() {
    super(DEFAULT_NUMSCALE_QUESTION_DETAILS());
  }
}
