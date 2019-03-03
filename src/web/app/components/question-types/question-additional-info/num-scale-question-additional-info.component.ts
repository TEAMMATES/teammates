import { Component } from '@angular/core';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-additional-info',
  templateUrl: './num-scale-question-additional-info.component.html',
  styleUrls: ['./num-scale-question-additional-info.component.scss'],
})
export class NumScaleQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackNumericalScaleQuestionDetails> {

  constructor() {
    super({
      minScale: 1,
      maxScale: 5,
      step: 1,
      questionType: FeedbackQuestionType.NUMSCALE,
      questionText: '',
    });
  }

}
