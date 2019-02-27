import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Confirm publishing session modal.
 */
@Component({
  selector: 'tm-confirm-publishing-session-modal',
  templateUrl: './confirm-publishing-session-modal.component.html',
  styleUrls: ['./confirm-publishing-session-modal.component.scss'],
})
export class ConfirmPublishingSessionModalComponent implements OnInit {

  @Input()
  feedbackSessionName: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
