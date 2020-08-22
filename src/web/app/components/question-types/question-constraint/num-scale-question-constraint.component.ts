import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of numerical scale question.
 */
@Component({
  selector: 'tm-num-scale-question-constraint',
  templateUrl: './num-scale-question-constraint.component.html',
  styleUrls: ['./num-scale-question-constraint.component.scss'],
})
export class NumScaleQuestionConstraintComponent extends QuestionConstraintComponent<any> {

  constructor() {
    super(null);
  }

  get isValid(): boolean {
    return true;
  }
}
