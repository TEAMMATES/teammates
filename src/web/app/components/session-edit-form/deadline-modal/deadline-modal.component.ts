import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Send reminders to respondents modal.
 */
@Component({
  selector: 'tm-deadline-modal',
  templateUrl: './deadline-modal.component.html',
  styleUrls: ['./deadline-modal.component.scss'],
})
export class DeadlineModalComponent implements OnInit {
  isOpen: boolean = true;

  constructor(public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
  }

  setA(a: boolean) {
    this.isOpen = a;
  }
}
