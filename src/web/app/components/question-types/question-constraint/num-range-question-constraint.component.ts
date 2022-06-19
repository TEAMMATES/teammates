import { Component } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';

@Component({
  selector: 'tm-num-range-question-constraint',
  templateUrl: './num-range-question-constraint.component.html',
  styleUrls: ['./num-range-question-constraint.component.scss']
})
export class NumRangeQuestionConstraintComponent extends QuestionConstraintComponent<any> {

  constructor() { 
    super(null)
  }

  get isValid(): boolean {
    return true;
  }

}
