import { EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract recipient submission form.
 */
export abstract class QuestionEditAnswerFormComponent<
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
    this.responseDetailsChange.emit(Object.assign({}, this.responseDetails, { [field]: data }));
  }

  /**
   * Triggers changes of the response details for the form.
   */
  triggerResponseDetailsChangeBatch(obj: {[key: string]: any}): void {
    this.responseDetailsChange.emit(Object.assign({}, this.responseDetails, obj));
  }
}
