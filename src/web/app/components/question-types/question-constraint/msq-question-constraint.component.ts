import { Component, Input } from '@angular/core';
import { QuestionConstraintComponent } from './question-constraint.component';
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
  imports: [],
})
export class MsqQuestionConstraintComponent extends QuestionConstraintComponent {
  readonly NO_VALUE: number = NO_VALUE;

  @Input()
  questionDetails: FeedbackMsqQuestionDetails = DEFAULT_MSQ_QUESTION_DETAILS();

  override get isValid(): boolean {
    return true;
  }
}
