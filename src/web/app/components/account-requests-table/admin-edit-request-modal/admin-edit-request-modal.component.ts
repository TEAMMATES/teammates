import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditRequestModalComponentResult } from './admin-edit-request-modal-model';

/**
 * Modal to select reject account requests with reason.
 */
@Component({
  selector: 'tm-edit-request-modal',
  templateUrl: './admin-edit-request-modal.component.html',
  styleUrls: ['./admin-edit-request-modal.component.scss'],
})

export class EditRequestModalComponent {

  @Input()
  accountRequestName: string = '';
  @Input()
  accountRequestEmail: string = '';
  @Input()
  accountRequestInstitution: string = '';
  @Input()
  accountRequestComment: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  /**
   * Fires the edit event.
   */
  edit(): void {
      // this.accountRequestToReject.status = "REJECTED";

      const result: EditRequestModalComponentResult = {
        accountRequestName: this.accountRequestName,
        accountRequestEmail: this.accountRequestEmail,
        accountRequestInstitution: this.accountRequestInstitution,
        accountRequestComment: this.accountRequestComment,
      };

      this.activeModal.close(result);
  }
}
