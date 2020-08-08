import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of text question.
 */
@Component({
  selector: 'tm-text-question-constraint',
  templateUrl: './text-question-constraint.component.html',
  styleUrls: ['./text-question-constraint.component.scss'],
})
export class TextQuestionConstraintComponent extends QuestionConstraintComponent<any> {

  constructor() {
    super(null);
  }

  get isValid(): boolean {
    return true;
  }
}
