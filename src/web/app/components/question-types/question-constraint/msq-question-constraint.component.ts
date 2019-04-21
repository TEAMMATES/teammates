import { Component, Input } from '@angular/core';
import { FeedbackMsqQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';

/**
 * Constraints of Msq question.
 */
@Component({
  selector: 'tm-msq-question-constraint',
  templateUrl: './msq-question-constraint.component.html',
  styleUrls: ['./msq-question-constraint.component.scss'],
})
export class MsqQuestionConstraintComponent {

  @Input()
  questionDetails: FeedbackMsqQuestionDetails = DEFAULT_MSQ_QUESTION_DETAILS();

  readonly NO_VALUE: number = NO_VALUE;

  constructor() { }

}
