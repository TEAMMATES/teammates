import { Component, Input } from '@angular/core';
import { NgbModalRef, NgbModal, NgbTooltip, NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { EditRequestModalComponentResult } from './admin-edit-request-modal/admin-edit-request-modal-model';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import {
  RejectWithReasonModalComponentResult,
} from './admin-reject-with-reason-modal/admin-reject-with-reason-modal-model';
import {
  RejectWithReasonModalComponent,
} from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequest, MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

/**
 * Pending account requests table component for approval workflow.
 */
@Component({
  selector: 'tm-account-request-table',
  templateUrl: './account-request-table.component.html',
  styleUrls: ['./account-request-table.component.scss'],
  imports: [
    NgbTooltip,
    AjaxLoadingComponent,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
  ],
})
export class AccountRequestTableComponent {

  @Input()
  accountRequests: AccountRequestTableRowModel[] = [];

  isRejectingAccount: boolean[] = new Array(this.accountRequests.length).fill(false);
  isApprovingAccount: boolean[] = new Array(this.accountRequests.length).fill(false);

  constructor(
    private statusMessageService: StatusMessageService,
    private simpleModalService: SimpleModalService,
    private accountService: AccountService,
    private ngbModal: NgbModal,
  ) {}

  editAccountRequest(accountRequest: AccountRequestTableRowModel): void {
    const modalRef: NgbModalRef = this.ngbModal.open(EditRequestModalComponent);
    modalRef.componentInstance.accountRequestName = accountRequest.name;
    modalRef.componentInstance.accountRequestEmail = accountRequest.email;
    modalRef.componentInstance.accountRequestInstitution = accountRequest.instituteAndCountry;
    modalRef.componentInstance.accountRequestComments = accountRequest.comments;

    modalRef.result.then((res: EditRequestModalComponentResult) => {
      this.accountService.editAccountRequest(
        accountRequest.id,
        {
            name: res.accountRequestName,
            email: res.accountRequestEmail,
            institute: res.accountRequestInstitution,
            status: accountRequest.status,
            comments: res.accountRequestComment,
        },
      )
      .subscribe({
        next: (resp: AccountRequest) => {
          accountRequest.comments = resp.comments ?? '';
          accountRequest.name = resp.name;
          accountRequest.email = resp.email;
          accountRequest.instituteAndCountry = resp.institute;
          this.statusMessageService.showSuccessToast('Account request was successfully updated.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  approveAccountRequest(accountRequest: AccountRequestTableRowModel, index: number): void {
    this.isApprovingAccount[index] = true;
    this.accountService.approveAccountRequest(accountRequest.id)
    .subscribe({
      next: (resp : AccountRequest) => {
        accountRequest.status = resp.status;
        this.statusMessageService.showSuccessToast(
          `Account request was successfully approved. Email has been sent to ${accountRequest.email}.`,
        );
        this.isApprovingAccount[index] = false;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isApprovingAccount[index] = false;
      },
    });
  }

  deleteAccountRequest(accountRequest: AccountRequestTableRowModel): void {
    const modalContent: string = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.instituteAndCountry}</strong>?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete account request for <strong>${accountRequest.name}</strong>?`, SimpleModalType.DANGER, modalContent);

    modalRef.result.then(() => {
      this.accountService.deleteAccountRequest(accountRequest.id)
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
    const modalContent: string = `<strong>Comment:</strong> ${accountRequest.comments || 'No comments'}`;
    const modalRef: NgbModalRef = this.simpleModalService.openInformationModal(
        `Comments for <strong>${accountRequest.name}</strong> Request`, SimpleModalType.INFO, modalContent);

    modalRef.result.then(() => {}, () => {});
  }

  rejectAccountRequest(accountRequest: AccountRequestTableRowModel, index: number): void {
    this.isRejectingAccount[index] = true;
    this.accountService.rejectAccountRequest(accountRequest.id)
    .subscribe({
      next: (resp : AccountRequest) => {
        accountRequest.status = resp.status;
        this.statusMessageService.showSuccessToast('Account request was successfully rejected.');
        this.isRejectingAccount[index] = false;
        },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isRejectingAccount[index] = false;
      },
    });
  }

  rejectAccountRequestWithReason(accountRequest: AccountRequestTableRowModel, index: number): void {
    this.isRejectingAccount[index] = true;
    const modalRef: NgbModalRef = this.ngbModal.open(RejectWithReasonModalComponent);
    modalRef.componentInstance.accountRequestName = accountRequest.name;
    modalRef.componentInstance.accountRequestEmail = accountRequest.email;

    modalRef.dismissed.subscribe(() => {
      this.isRejectingAccount[index] = false;
    });

    modalRef.result.then((res: RejectWithReasonModalComponentResult) => {
      this.accountService.rejectAccountRequest(accountRequest.id,
        res.rejectionReasonTitle, res.rejectionReasonBody)
      .subscribe({
        next: (resp: AccountRequest) => {
          accountRequest.status = resp.status;
          this.statusMessageService.showSuccessToast(
            `Account request was successfully rejected. Email has been sent to ${accountRequest.email}.`,
          );
          this.isRejectingAccount[index] = false;
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.isRejectingAccount[index] = false;
        },
      });
    }, () => {});
  }

  trackAccountRequest(_: number, accountRequest: AccountRequestTableRowModel): string {
    return accountRequest.id;
  }
}
