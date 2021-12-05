import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract recipient submission form.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class QuestionEditAnswerFormComponent<
    Q extends FeedbackQuestionDetails, R extends FeedbackResponseDetails> {

  @Input()
  isDisabled: boolean = false;

  @Input()
  questionDetails: Q;

  @Input()
  responseDetails: R;

  @Output()
  responseDetailsChange: EventEmitter<FeedbackResponseDetails> = new EventEmitter();

  protected constructor(questionDetails: Q, responseDetails: R) {
    this.questionDetails = questionDetails;
    this.responseDetails = responseDetails;
  }

  /**
   * Triggers the change of the response details for the form.
   */
  triggerResponseDetailsChange(field: string, data: any): void {
    this.responseDetailsChange.emit({ ...this.responseDetails, [field]: data });
  }

  /**
   * Triggers changes of the response details for the form.
   */
  triggerResponseDetailsChangeBatch(obj: {[key: string]: any}): void {
    this.responseDetailsChange.emit({ ...this.responseDetails, ...obj });
  }
}
