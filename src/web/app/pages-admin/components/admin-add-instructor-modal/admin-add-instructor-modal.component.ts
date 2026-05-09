import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AdminAddInstructorModalComponentResult } from './admin-add-instructor-modal-model';
import { castAsInputElement } from '../../../../types/event-target-caster';

/**
 * Modal to add an instructor.
 */
@Component({
  selector: 'tm-admin-add-instructor-modal',
  templateUrl: './admin-add-instructor-modal.component.html',
  imports: [FormsModule],
})
export class AdminAddInstructorModalComponent {
  readonly castAsInputElement = castAsInputElement;

  instructorName = '';
  instructorEmail = '';
  instructorInstitution = '';

  constructor(public activeModal: NgbActiveModal) {}

  /**
   * Fires the confirm event.
   */
  confirm(): void {
    const result: AdminAddInstructorModalComponentResult = {
      instructorName: this.instructorName,
      instructorEmail: this.instructorEmail,
      instructorInstitution: this.instructorInstitution,
    };

    this.activeModal.close(result);
  }
}
