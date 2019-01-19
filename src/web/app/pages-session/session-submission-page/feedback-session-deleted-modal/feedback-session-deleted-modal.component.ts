import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to alert the deletion of a feedback session.
 */
@Component({
  selector: 'tm-feedback-session-deleted-modal',
  templateUrl: './feedback-session-deleted-modal.component.html',
  styleUrls: ['./feedback-session-deleted-modal.component.scss'],
})
export class FeedbackSessionDeletedModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
