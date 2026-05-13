import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract recipient submission form.
 */
@Directive()
export abstract class QuestionEditAnswerFormComponent<R extends FeedbackResponseDetails> {
  @Input()
  isDisabled = false;

  @Input()
  responseDetails!: R;

  @Input()
  recipient = '';

  @Output()
  responseDetailsChange: EventEmitter<R> = new EventEmitter();

  getAriaLabel(): string {
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
