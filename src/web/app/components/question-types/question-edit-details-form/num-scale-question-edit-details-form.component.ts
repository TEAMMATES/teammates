import { Component } from '@angular/core';
import { FeedbackNumericalScaleQuestionDetails, FeedbackQuestionType } from '../../../../types/api-output';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';

/**
 * Question details edit form component for numerical scale question.
 */
@Component({
  selector: 'tm-num-scale-question-edit-details-form',
  templateUrl: './num-scale-question-edit-details-form.component.html',
  styleUrls: ['./num-scale-question-edit-details-form.component.scss'],
})
export class NumScaleQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackNumericalScaleQuestionDetails> {

  constructor() {
    super({
      minScale: 1,
      maxScale: 5,
      step: 1,
      questionText: '',
      questionType: FeedbackQuestionType.CONTRIB,
    });
  }

}
