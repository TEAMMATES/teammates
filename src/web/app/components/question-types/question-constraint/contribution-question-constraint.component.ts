import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of contribution question.
 */
@Component({
  selector: 'tm-contribution-question-constraint',
  templateUrl: './contribution-question-constraint.component.html',
  styleUrls: ['./contribution-question-constraint.component.scss'],
})
export class ContributionQuestionConstraintComponent extends QuestionConstraintComponent implements OnInit {

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

  isValid(): Observable<boolean> {
    return of(true);
  }
}
