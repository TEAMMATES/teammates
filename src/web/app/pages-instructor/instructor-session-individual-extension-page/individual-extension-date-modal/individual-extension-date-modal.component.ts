import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { IndividualExtensionConfirmModalComponent } from './individual-extension-confirm-modal/individual-extension-confirm-modal.component';

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'individual-extension-date-modal',
  templateUrl: './individual-extension-date-modal.component.html',
  styleUrls: ['./individual-extension-date-modal.component.scss'],
})
export class IndividualExtensionDateModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  numberOfStudentsToExtend: number = 0;

  @Input()
  feedbackSessionName: string = '';

  constructor(public activeModal: NgbActiveModal,
              private ngbModal: NgbModal)
  {}

  ngOnInit(): void {
  }

  setA(a: boolean) {
    console.log(a);
  }
  
  onExtend() { 
    this.activeModal.close()
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    console.log("Open second modal" + modalRef)
  }
  
  onDelete()
  {
  }

}