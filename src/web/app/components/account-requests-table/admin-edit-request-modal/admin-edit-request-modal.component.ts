import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditRequestModalComponentResult } from './admin-edit-request-modal-model';
import { StatusMessageService } from '../../../../services/status-message.service';
import { castAsInputElement, castAsTextAreaElement } from '../../../../types/event-target-caster';

/**
 * Modal to edit an account request (name, email, institution, country, comments).
 */
@Component({
    selector: 'tm-edit-request-modal',
    templateUrl: './admin-edit-request-modal.component.html',
    styleUrls: ['./admin-edit-request-modal.component.scss'],
})

export class EditRequestModalComponent {
  readonly castAsInputElement = castAsInputElement;
  readonly castAsTextAreaElement = castAsTextAreaElement;

  @Input()
  accountRequestName: string = '';
  @Input()
  accountRequestEmail: string = '';
  @Input()
  accountRequestInstitution: string = '';
  @Input()
  accountRequestCountry: string = '';
  @Input()
  accountRequestComments: string = '';

  constructor(
      public activeModal: NgbActiveModal,
      private statusMessageService: StatusMessageService,
  ) {}

  /**
   * Fires the edit event.
   */
  edit(): void {
    const name: string = this.accountRequestName.trim();
    const email: string = this.accountRequestEmail.trim();
    const institution: string = this.accountRequestInstitution.trim();
    const country: string = this.accountRequestCountry.trim();
    const comments: string = (this.accountRequestComments ?? '').trim();

    if (!name || !email || !institution || !country) {
      this.statusMessageService.showErrorToast(
          'Please fill in name, email, institution, and country.');
      return;
    }

    const result: EditRequestModalComponentResult = {
      accountRequestName: name,
      accountRequestEmail: email,
      accountRequestInstitution: institution,
      accountRequestCountry: country,
      accountRequestComment: comments,
    };

    this.activeModal.close(result);
  }
}
