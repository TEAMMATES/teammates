import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to alert that a feedback session is closed.
 */
@Component({
  selector: 'tm-feedback-session-closed-modal',
  templateUrl: './feedback-session-closed-modal.component.html',
  styleUrls: ['./feedback-session-closed-modal.component.scss'],
})
export class FeedbackSessionClosedModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
