import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Confirm unpublishing session modal.
 */
@Component({
  selector: 'tm-confirm-unpublishing-session-modal',
  templateUrl: './confirm-unpublishing-session-modal.component.html',
  styleUrls: ['./confirm-unpublishing-session-modal.component.scss'],
})
export class ConfirmUnpublishingSessionModalComponent implements OnInit {

  @Input()
  feedbackSessionName: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
