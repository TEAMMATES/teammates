import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'tm-session-permanent-deletion-confirm-modal',
  templateUrl: './session-permanent-deletion-confirm-modal.component.html',
})
export class SessionPermanentDeletionConfirmModalComponent {
  activeModal = inject(NgbActiveModal);

  @Input()
  courseId = '';

  @Input()
  feedbackSessionName = '';
}
