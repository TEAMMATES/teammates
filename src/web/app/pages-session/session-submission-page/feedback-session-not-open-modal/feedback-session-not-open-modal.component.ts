import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to alert that a feedback session is not open yet.
 */
@Component({
  selector: 'tm-feedback-session-not-open-modal',
  templateUrl: './feedback-session-not-open-modal.component.html',
  styleUrls: ['./feedback-session-not-open-modal.component.scss'],
})
export class FeedbackSessionNotOpenModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
