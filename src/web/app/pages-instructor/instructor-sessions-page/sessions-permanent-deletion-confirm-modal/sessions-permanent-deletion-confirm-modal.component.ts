import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSession } from '../../../../types/api-output';

/**
 * Modal to confirm permanent deletion of a list of feedback session.
 */
@Component({
  selector: 'tm-sessions-permanent-deletion-confirm-modal',
  templateUrl: './sessions-permanent-deletion-confirm-modal.component.html',
  styleUrls: ['./sessions-permanent-deletion-confirm-modal.component.scss'],
})
export class SessionsPermanentDeletionConfirmModalComponent {

  @Input()
  sessionsToDelete: FeedbackSession[] = [];

  constructor(public activeModal: NgbActiveModal) {}

}
