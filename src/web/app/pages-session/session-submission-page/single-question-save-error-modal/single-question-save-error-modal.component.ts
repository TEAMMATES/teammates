import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

/**
 * Modal that explains why a single question submission failed.
 */
@Component({
  selector: 'tm-single-question-save-error-modal',
  templateUrl: './single-question-save-error-modal.component.html',
})
export class SingleQuestionSaveErrorModalComponent {
  activeModal = inject(NgbActiveModal);

  @Input()
  questionNumber = 0;

  @Input()
  errorMessage = '';
}
