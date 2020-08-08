import { EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';
import { FeedbackResponseRecipientSubmissionFormModel } from '../../question-submission-form/question-submission-form-model';

/**
 * Base class for constraint components.
 */
export abstract class QuestionConstraintComponent<Q extends FeedbackQuestionDetails> {

  @Input()
  get recipientSubmissionForms(): FeedbackResponseRecipientSubmissionFormModel[] {
    return this.formModels;
  }
  set recipientSubmissionForms(models: FeedbackResponseRecipientSubmissionFormModel[]) {
    this.formModels = models;
    this.isValidEvent.emit(this.isValid);
  }

  abstract get isValid(): boolean;

  @Input()
  questionDetails: Q;

  @Output()
  isValidEvent: EventEmitter<boolean> = new EventEmitter();

  private formModels: FeedbackResponseRecipientSubmissionFormModel[] = [];

  protected constructor(questionDetails: Q) {
    this.questionDetails = questionDetails;
  }
}
