import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of numerical scale question.
 */
@Component({
  selector: 'tm-num-scale-question-constraint',
  templateUrl: './num-scale-question-constraint.component.html',
  styleUrls: ['./num-scale-question-constraint.component.scss'],
})
export class NumScaleQuestionConstraintComponent extends QuestionConstraintComponent implements OnInit {

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

  isValid(): Observable<boolean> {
    return of(true);
  }
}
