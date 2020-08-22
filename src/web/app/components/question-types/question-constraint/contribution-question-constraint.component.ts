import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of contribution question.
 */
@Component({
  selector: 'tm-contribution-question-constraint',
  templateUrl: './contribution-question-constraint.component.html',
  styleUrls: ['./contribution-question-constraint.component.scss'],
})
export class ContributionQuestionConstraintComponent extends QuestionConstraintComponent<any> {

  constructor() {
    super(null);
  }

  get isValid(): boolean {
    return true;
  }
}
