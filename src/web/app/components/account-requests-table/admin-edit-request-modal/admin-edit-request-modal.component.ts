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
  accountRequestComments: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  /**
   * Fires the edit event.
   */
  edit(): void {
      const result: EditRequestModalComponentResult = {
        accountRequestName: this.accountRequestName,
        accountRequestEmail: this.accountRequestEmail,
        accountRequestInstitution: this.accountRequestInstitution,
        accountRequestComment: this.accountRequestComments,
      };

      this.activeModal.close(result);
  }
}
