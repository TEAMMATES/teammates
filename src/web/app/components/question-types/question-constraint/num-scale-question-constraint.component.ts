import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of numerical scale question.
 */
@Component({
  selector: 'tm-num-scale-question-constraint',
  templateUrl: './num-scale-question-constraint.component.html',
})
export class NumScaleQuestionConstraintComponent extends QuestionConstraintComponent<any> {
  constructor() {
    super(null);
  }

  override get isValid(): boolean {
    return true;
  }
}
