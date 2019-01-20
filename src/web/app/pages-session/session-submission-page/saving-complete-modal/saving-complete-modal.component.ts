import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to inform the completion of the saving process
 */
@Component({
  selector: 'tm-saving-complete-modal',
  templateUrl: './saving-complete-modal.component.html',
  styleUrls: ['./saving-complete-modal.component.scss'],
})
export class SavingCompleteModalComponent implements OnInit {

  @Input()
  notYetAnsweredQuestions: string = '';

  @Input()
  failToSaveQuestions: string = '';

  @Input()
  hasSubmissionConfirmationError: boolean = false;

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
