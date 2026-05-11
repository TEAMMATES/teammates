import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditRequestModalComponentResult } from './admin-edit-request-modal-model';
import { castAsInputElement, castAsTextAreaElement } from '../../../../types/event-target-caster';

/**
 * Modal to select reject account requests with reason.
 */
@Component({
  selector: 'tm-edit-request-modal',
  templateUrl: './admin-edit-request-modal.component.html',
})
export class EditRequestModalComponent {
  activeModal = inject(NgbActiveModal);

  readonly castAsInputElement = castAsInputElement;
  readonly castAsTextAreaElement = castAsTextAreaElement;

  @Input()
  accountRequestName = '';
  @Input()
  accountRequestEmail = '';
  @Input()
  accountRequestInstitution = '';
  @Input()
  accountRequestComments = '';

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
