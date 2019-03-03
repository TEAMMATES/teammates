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
  selector: 'tm-numscale-question-additional-info',
  templateUrl: './numscale-question-additional-info.component.html',
  styleUrls: ['./numscale-question-additional-info.component.scss'],
})
export class NumscaleQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackNumericalScaleQuestionDetails> {

  constructor() {
    super({
      minScale: 1,
      maxScale: 5,
      step: 0.5,
      questionType: FeedbackQuestionType.NUMSCALE,
      questionText: '',
    });
  }

}
