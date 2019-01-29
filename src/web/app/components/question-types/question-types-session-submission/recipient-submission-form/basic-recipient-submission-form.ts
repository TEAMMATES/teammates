import { EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../feedback-question';
import { FeedbackResponseDetails } from '../../../../feedback-response';

/**
 * The abstract recipient submission form.
 */
export abstract class BasicRecipientSubmissionFormComponent<
    Q extends FeedbackQuestionDetails, R extends FeedbackResponseDetails> implements OnInit {

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

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the response details for the form.
   */
  triggerResponseDetailsChange(field: string, data: any): void {
    this.responseDetailsChange.emit(Object.assign(this.responseDetails, { [field]: data }));
  }
}
