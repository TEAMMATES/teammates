import { Component, Input } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * Account requests table component.
 */
@Component({
  selector: 'tm-account-request-table',
  templateUrl: './account-request-table.component.html',
  styleUrls: ['./account-request-table.component.scss'],
  animations: [collapseAnim],
})

export class AccountRequestTableComponent {

  @Input()
  accountRequests: AccountRequestTableRowModel[] = [];

  @Input()
  searchString = '';

  constructor(
    private statusMessageService: StatusMessageService,
    private simpleModalService: SimpleModalService,
    private accountService: AccountService,
  ) {}

  /**
   * Shows all account requests' links in the page.
   */
  showAllAccountRequestsLinks(): void {
    for (const accountRequest of this.accountRequests) {
      accountRequest.showLinks = true;
    }
  }

  /**
   * Hides all account requests' links in the page.
   */
  hideAllAccountRequestsLinks(): void {
    for (const accountRequest of this.accountRequests) {
      accountRequest.showLinks = false;
    }
  }

  resetAccountRequest(accountRequest: AccountRequestTableRowModel): void {
    const modalContent = `Are you sure you want to reset the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.instituteAndCountry}</strong>?
        An email with the account registration link will also be sent to the instructor.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Reset account request for <strong>${accountRequest.name}</strong>?`, SimpleModalType.WARNING, modalContent);

    modalRef.result.then(() => {
      this.accountService.resetAccountRequest(accountRequest.email, accountRequest.instituteAndCountry)
        .subscribe({
          next: () => {
            this.statusMessageService
                .showSuccessToast(`Reset successful. An email has been sent to ${accountRequest.email}.`);
            accountRequest.registeredAtText = '';
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
    }, () => {});
  }

  deleteAccountRequest(accountRequest: AccountRequestTableRowModel): void {
    const modalContent: string = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.instituteAndCountry}</strong>?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete account request for <strong>${accountRequest.name}</strong>?`, SimpleModalType.DANGER, modalContent);

    modalRef.result.then(() => {
      this.accountService.deleteAccountRequest(accountRequest.email, accountRequest.instituteAndCountry)
      .subscribe({
        next: (resp: MessageOutput) => {
          this.statusMessageService.showSuccessToast(resp.message);
          this.accountRequests = this.accountRequests.filter((x: AccountRequestTableRowModel) => x !== accountRequest);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  viewAccountRequest(accountRequest: AccountRequestTableRowModel): void {
    const modalContent: string = `<strong>Comment:</strong> ${accountRequest.comments || ''}`;
    const modalRef: NgbModalRef = this.simpleModalService.openInformationModal(
        `Comments for <strong>${accountRequest.name}</strong> Request`, SimpleModalType.INFO, modalContent);

    modalRef.result.then(() => {}, () => {});
  }
}
