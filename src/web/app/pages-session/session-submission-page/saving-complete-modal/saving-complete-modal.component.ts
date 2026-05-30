import { NgClass, KeyValuePipe } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { QuestionSubmissionFormModel } from '../../../components/question-submission-form/question-submission-form-model';

/**
 * Modal to inform the completion of the saving process
 */
@Component({
  selector: 'tm-saving-complete-modal',
  templateUrl: './saving-complete-modal.component.html',
  imports: [NgClass, KeyValuePipe],
})
export class SavingCompleteModalComponent {
  activeModal = inject(NgbActiveModal);

  @Input()
  questions: QuestionSubmissionFormModel[] = [];

  @Input()
  notYetAnsweredQuestions: number[] = [];

  @Input()
  failToSaveQuestions: Record<number, string> = {}; // Map of question number to error message

  get hasFailToSaveQuestions(): boolean {
    return Object.keys(this.failToSaveQuestions).length !== 0;
  }
}
