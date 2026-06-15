import { Component, Input, inject } from '@angular/core';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModalRef, NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { AccountVerificationRequestTableRowModel } from './account-verification-request-table-model';
import { EditRequestModalComponentResult } from './admin-edit-request-modal/admin-edit-request-modal-model';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import { RejectWithReasonModalComponentResult } from './admin-reject-with-reason-modal/admin-reject-with-reason-modal-model';
import { RejectWithReasonModalComponent } from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountVerificationRequest, MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { CountryNamePipe } from '../../pipes/country-name.pipe';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

/**
 * Pending account requests table component for approval workflow.
 */
@Component({
  selector: 'tm-account-verification-request-table',
  templateUrl: './account-verification-request-table.component.html',
  styleUrls: ['./account-verification-request-table.component.scss'],
  imports: [NgbTooltip, AjaxLoadingComponent, NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, CountryNamePipe],
})
export class AccountVerificationRequestTableComponent {
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly accountService = inject(AccountService);
  private readonly ngbModal = inject(NgbModal);

  @Input()
  accountVerificationRequests: AccountVerificationRequestTableRowModel[] = [];

  isRejectingAccountVerificationRequest: boolean[] = new Array(this.accountVerificationRequests.length).fill(false);
  isApprovingAccountVerificationRequest: boolean[] = new Array(this.accountVerificationRequests.length).fill(false);

  editAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestTableRowModel): void {
    const modalRef: NgbModalRef = this.ngbModal.open(EditRequestModalComponent);
    modalRef.componentInstance.accountVerificationRequestName = accountVerificationRequest.name;
    modalRef.componentInstance.accountVerificationRequestEmail = accountVerificationRequest.email;
    modalRef.componentInstance.accountVerificationRequestInstitution = accountVerificationRequest.institute;
    modalRef.componentInstance.accountVerificationRequestCountry = accountVerificationRequest.country;
    modalRef.componentInstance.accountVerificationRequestComments = accountVerificationRequest.comments;

    modalRef.result.then(
      (res: EditRequestModalComponentResult) => {
        this.accountService
          .editAccountVerificationRequest(accountVerificationRequest.id, {
            name: res.accountVerificationRequestName,
            email: res.accountVerificationRequestEmail,
            institute: res.accountVerificationRequestInstitution,
            country: res.accountVerificationRequestCountry,
            status: accountVerificationRequest.status,
            comments: res.accountVerificationRequestComment,
          })
          .subscribe({
            next: (resp: AccountVerificationRequest) => {
              accountVerificationRequest.comments = resp.comments ?? '';
              accountVerificationRequest.name = resp.name;
              accountVerificationRequest.email = resp.email;
              accountVerificationRequest.institute = resp.institute;
              accountVerificationRequest.country = resp.country;
              this.statusMessageService.showSuccessToast('Account request was successfully updated.');
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
      },
      () => {},
    );
  }

  approveAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestTableRowModel, index: number): void {
    this.isApprovingAccountVerificationRequest[index] = true;
    this.accountService.approveAccountVerificationRequest(accountVerificationRequest.id).subscribe({
      next: (resp: AccountVerificationRequest) => {
        accountVerificationRequest.status = resp.status;
        this.statusMessageService.showSuccessToast(
          `Account request was successfully approved. Email has been sent to ${accountVerificationRequest.email}.`,
        );
        this.isApprovingAccountVerificationRequest[index] = false;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isApprovingAccountVerificationRequest[index] = false;
      },
    });
  }

  deleteAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestTableRowModel): void {
    const modalContent = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>${accountVerificationRequest.name}</strong> with email <strong>${accountVerificationRequest.email}</strong> from
        <strong>${accountVerificationRequest.institute}</strong>?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Delete account request for <strong>${accountVerificationRequest.name}</strong>?`,
      SimpleModalType.DANGER,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.accountService.deleteAccountVerificationRequest(accountVerificationRequest.id).subscribe({
          next: (resp: MessageOutput) => {
            this.statusMessageService.showSuccessToast(resp.message);
            this.accountVerificationRequests = this.accountVerificationRequests.filter(
              (x: AccountVerificationRequestTableRowModel) => x !== accountVerificationRequest,
            );
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  viewAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestTableRowModel): void {
    const modalContent = `<strong>Comment:</strong> ${accountVerificationRequest.comments || 'No comments'}`;
    const modalRef: NgbModalRef = this.simpleModalService.openInformationModal(
      `Comments for <strong>${accountVerificationRequest.name}</strong> Request`,
      SimpleModalType.INFO,
      modalContent,
    );

    modalRef.result.then(
      () => {},
      () => {},
    );
  }

  rejectAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestTableRowModel, index: number): void {
    this.isRejectingAccountVerificationRequest[index] = true;
    this.accountService.rejectAccountVerificationRequest(accountVerificationRequest.id).subscribe({
      next: (resp: AccountVerificationRequest) => {
        accountVerificationRequest.status = resp.status;
        this.statusMessageService.showSuccessToast('Account request was successfully rejected.');
        this.isRejectingAccountVerificationRequest[index] = false;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isRejectingAccountVerificationRequest[index] = false;
      },
    });
  }

  rejectAccountVerificationRequestWithReason(accountVerificationRequest: AccountVerificationRequestTableRowModel, index: number): void {
    this.isRejectingAccountVerificationRequest[index] = true;
    const modalRef: NgbModalRef = this.ngbModal.open(RejectWithReasonModalComponent);
    modalRef.componentInstance.accountVerificationRequestName = accountVerificationRequest.name;
    modalRef.componentInstance.accountVerificationRequestEmail = accountVerificationRequest.email;

    modalRef.dismissed.subscribe(() => {
      this.isRejectingAccountVerificationRequest[index] = false;
    });

    modalRef.result.then(
      (res: RejectWithReasonModalComponentResult) => {
        this.accountService
          .rejectAccountVerificationRequest(accountVerificationRequest.id, res.rejectionReasonTitle, res.rejectionReasonBody)
          .subscribe({
            next: (resp: AccountVerificationRequest) => {
              accountVerificationRequest.status = resp.status;
              this.statusMessageService.showSuccessToast(
                `Account request was successfully rejected. Email has been sent to ${accountVerificationRequest.email}.`,
              );
              this.isRejectingAccountVerificationRequest[index] = false;
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
              this.isRejectingAccountVerificationRequest[index] = false;
            },
          });
      },
      () => {},
    );
  }
}
