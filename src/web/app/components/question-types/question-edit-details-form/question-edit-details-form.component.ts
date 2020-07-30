import { EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';

/**
 * The abstract question details edit form component.
 */
export abstract class QuestionEditDetailsFormComponent<D extends FeedbackQuestionDetails> implements OnInit {

  model: D;

  @Input()
  isEditable: boolean = true;

  @Input()
  set details(details: FeedbackQuestionDetails) {
    this.model = details as D;
  }

  @Output()
  detailsChange: EventEmitter<FeedbackQuestionDetails> = new EventEmitter();

  protected constructor(model: D) {
    this.model = model;
  }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: keyof D, data: D[keyof D]): void {
    this.detailsChange.emit(Object.assign({}, this.model, { [field]: data }));
  }

  /**
   * Triggers changes of the question details for the form.
   */
  triggerModelChangeBatch(obj: Partial<D>): void {
    this.detailsChange.emit(Object.assign({}, this.model, obj));
  }
}
