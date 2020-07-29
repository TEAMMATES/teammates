import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { QuestionConstraintComponent } from './question-constraint.component';

/**
 * Constraint of text question.
 */
@Component({
  selector: 'tm-text-question-constraint',
  templateUrl: './text-question-constraint.component.html',
  styleUrls: ['./text-question-constraint.component.scss'],
})
export class TextQuestionConstraintComponent extends QuestionConstraintComponent implements OnInit {

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

  isValid(): Observable<boolean> {
    return of(true);
  }
}
