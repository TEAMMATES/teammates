import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'individual-extension-confirm-modal',
  templateUrl: './individual-extension-confirm-modal.component.html',
  styleUrls: ['./individual-extension-confirm-modal.component.scss'],
})
export class IndividualExtensionConfirmModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  numberOfStudentsToExtend: number = 0;

  @Input()
  feedbackSessionName: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

  setA(a: boolean) {
    console.log(a);
  }
  
  onExtend() { 
    this.activeModal.close()
  }
  
  onDelete()
  {
  }

}