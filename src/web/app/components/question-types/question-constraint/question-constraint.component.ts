import { Directive, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionDetails } from '../../../../types/api-output';
import {
  FeedbackResponseRecipientSubmissionFormModel,
} from '../../question-submission-form/question-submission-form-model';

/**
 * Base class for constraint components.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class QuestionConstraintComponent<Q extends FeedbackQuestionDetails> implements OnInit {

  @Input()
  get recipientSubmissionForms(): FeedbackResponseRecipientSubmissionFormModel[] {
    return this.formModels;
  }
  set recipientSubmissionForms(models: FeedbackResponseRecipientSubmissionFormModel[]) {
    this.formModels = models;
  }

  abstract get isValid(): boolean;

  @Input()
  questionDetails: Q;

  @Output()
  isValidEvent: EventEmitter<boolean> = new EventEmitter();

  private formModels: FeedbackResponseRecipientSubmissionFormModel[] = [];

  ngOnInit(): void {
    this.isValidEvent.emit(this.isValid);
  }

  protected constructor(questionDetails: Q) {
    this.questionDetails = questionDetails;
  }
}
