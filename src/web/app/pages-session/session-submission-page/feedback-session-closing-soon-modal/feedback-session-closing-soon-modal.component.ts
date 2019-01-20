import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to alert that a feedback session is closing soon.
 */
@Component({
  selector: 'tm-feedback-session-closing-soon-modal',
  templateUrl: './feedback-session-closing-soon-modal.component.html',
  styleUrls: ['./feedback-session-closing-soon-modal.component.scss'],
})
export class FeedbackSessionClosingSoonModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
