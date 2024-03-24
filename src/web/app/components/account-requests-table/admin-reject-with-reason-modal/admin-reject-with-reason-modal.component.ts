import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { RejectWithReasonModalComponentResult } from './admin-reject-with-reason-modal-model';

/**
 * Modal to select reject account requests with reason.
 */
@Component({
  selector: 'tm-reject-with-reason-modal',
  templateUrl: './admin-reject-with-reason-modal.component.html',
  styleUrls: ['./admin-reject-with-reason-modal.component.scss'],
})

export class RejectWithReasonModalComponent {

  @Input()
  accountRequestName: string = '';
  rejectionReasonBody: string = 'Hi {name},'
  + '\nThanks for your interest in using TEAMMATES. We are unable to create a TEAMMATES instructor account for you.\n'
  + '\nReason: The email address you provided is not an ‘official’ email address provided by your institution.'
  + '\nRemedy: Resubmit the request …\n'
  + '\nReason: The email address you have provided '
  + 'does not seem like it belongs to a student (i.e., not a staff member) of your institution.'
  + '\nRemedy: If you are a student but you still need an instructor account, please send your justification to …\n'
  + '\nReason: You already have an account for this email address and this institution.'
  + '\nRemedy: You can login to TEAMMATES using the Google account abc@gmail.com'
  + '\nIf you need further clarification or would like to appeal this decision, '
  + 'please feel free to contact us at teammates@comp.nus.edu.sg.\n'
  + '\n{sign off} ';
  rejectionReasonTitle: string = 'TEAMMATES: We are Unable to Create an Account for you';
  isRejected: boolean = false;

  constructor(public activeModal: NgbActiveModal) {}

  /**
   * Fires the reject event.
   */
  reject(): void {

      const result: RejectWithReasonModalComponentResult = {
        accountRequestName: this.accountRequestName,
        rejectionReasonTitle: this.rejectionReasonTitle,
        rejectionReasonBody: this.rejectionReasonBody,
        isRejected: true,
      };

      this.activeModal.close(result);
  }
}
