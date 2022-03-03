import { Directive, Input } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract question response.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class QuestionResponse<R extends FeedbackResponseDetails, Q extends FeedbackQuestionDetails> {

  @Input() responseDetails: R;
  @Input() questionDetails: Q;
  @Input() isStudentPage: boolean = false;

  protected constructor(responseDetails: R, questionDetails: Q) {
    this.responseDetails = responseDetails;
    this.questionDetails = questionDetails;
  }

}
