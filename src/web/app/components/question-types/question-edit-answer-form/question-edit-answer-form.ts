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

  @Input()
  recipient: String = '';

  @Output()
  responseDetailsChange: EventEmitter<FeedbackResponseDetails> = new EventEmitter();

  protected constructor(questionDetails: Q, responseDetails: R) {
    this.questionDetails = questionDetails;
    this.responseDetails = responseDetails;
  }

  getAriaLabel(): String {
    if (this.recipient === '' || this.recipient === '%GENERAL%' || this.recipient === 'Myself') {
      return 'Response';
    }
    if (this.recipient === 'Unknown') {
      return 'Response for To-Be-Selected';
    }
    return `Response for ${this.recipient}`;
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
  triggerResponseDetailsChangeBatch(obj: { [key: string]: any }): void {
    this.responseDetailsChange.emit({ ...this.responseDetails, ...obj });
  }
}
