import { Directive, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackResponseRecipientSubmissionFormModel } from '../../question-submission-form/question-submission-form-model';

/**
 * Base class for constraint components.
 */
@Directive()
export abstract class QuestionConstraintComponent implements OnInit {
  @Input()
  get recipientSubmissionForms(): FeedbackResponseRecipientSubmissionFormModel[] {
    return this.formModels;
  }
  set recipientSubmissionForms(models: FeedbackResponseRecipientSubmissionFormModel[]) {
    this.formModels = models;
  }

  abstract get isValid(): boolean;

  @Output()
  isValidEvent: EventEmitter<boolean> = new EventEmitter();

  private formModels: FeedbackResponseRecipientSubmissionFormModel[] = [];

  ngOnInit(): void {
    this.isValidEvent.emit(this.isValid);
  }
}
