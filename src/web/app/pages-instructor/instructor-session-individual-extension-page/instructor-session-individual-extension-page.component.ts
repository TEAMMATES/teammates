import { Component, OnInit } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { IndividualExtensionDateModalComponent } from './individual-extension-date-modal/individual-extension-date-modal.component';

/**
 * Send reminders to respondents modal.
 */
@Component({

  selector: 'tm-instructor-session-individual-extension-page',
  templateUrl: './instructor-session-individual-extension-page.component.html',
  styleUrls: ['./instructor-session-individual-extension-page.component.scss'],
})

export class InstructorSessionIndividualExtensionPageComponent implements OnInit {
  isOpen: boolean = true;

  ngOnInit(): void {
  }

  constructor(private simpleModalService: SimpleModalService,
              private ngbModal: NgbModal)
  {}

  onExtend(): void
  {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionDateModalComponent);
    console.log(modalRef)
  }

  onDelete(): void
  {
    const modalRef: NgbModalRef = this.simpleModalService
    .openConfirmationModal(
      'Confirm deleting feedback session extension?', SimpleModalType.DANGER,
      'Do you want to delete the feedback session extension(s) for <b>3 student(s)</b>? Their feedback session deadline will be reverted back to the original deadline.');
    modalRef.result.then(() => console.log("Confirmed!"))
  }

  setA(a: boolean) {
    this.isOpen = a;
  }
}