import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'tm-session-permanent-deletion-confirm-modal',
  templateUrl: './session-permanent-deletion-confirm-modal.component.html',
  styleUrls: ['./session-permanent-deletion-confirm-modal.component.scss'],
})
export class SessionPermanentDeletionConfirmModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
