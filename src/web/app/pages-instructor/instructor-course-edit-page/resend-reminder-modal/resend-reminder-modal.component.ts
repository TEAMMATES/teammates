import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to resend a reminder email to an instructor.
 */
@Component({
  selector: 'tm-resend-reminder-modal',
  templateUrl: './resend-reminder-modal.component.html',
  styleUrls: ['./resend-reminder-modal.component.scss'],
})
export class ResendReminderModalComponent implements OnInit {

  @Input()
  instructorname: string = '';

  @Input()
  courseId: string = '';

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
