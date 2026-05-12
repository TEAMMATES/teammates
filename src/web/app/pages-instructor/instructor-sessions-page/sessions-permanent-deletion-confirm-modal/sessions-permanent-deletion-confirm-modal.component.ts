import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSession } from '../../../../types/api-output';

/**
 * Modal to confirm permanent deletion of a list of feedback session.
 */
@Component({
  selector: 'tm-sessions-permanent-deletion-confirm-modal',
  templateUrl: './sessions-permanent-deletion-confirm-modal.component.html',
  imports: [],
})
export class SessionsPermanentDeletionConfirmModalComponent {
  activeModal = inject(NgbActiveModal);

  @Input()
  sessionsToDelete: FeedbackSession[] = [];
}
