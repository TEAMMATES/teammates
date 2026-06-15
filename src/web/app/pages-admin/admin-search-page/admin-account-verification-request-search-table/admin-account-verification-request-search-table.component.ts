import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModalRef, NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { AccountService } from '../../../../services/account.service';
import { AccountVerificationRequestSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { AccountVerificationRequest, MessageOutput } from '../../../../types/api-output';
import { ErrorMessageOutput } from '../../../error-message-output';
import { CountryNamePipe } from '../../../pipes/country-name.pipe';
import { SearchTermsHighlighterPipe } from '../../../pipes/search-terms-highlighter.pipe';
import { EditRequestModalComponentResult } from '../../../components/account-verification-requests-table/admin-edit-request-modal/admin-edit-request-modal-model';
import { EditRequestModalComponent } from '../../../components/account-verification-requests-table/admin-edit-request-modal/admin-edit-request-modal.component';
import { RejectWithReasonModalComponentResult } from '../../../components/account-verification-requests-table/admin-reject-with-reason-modal/admin-reject-with-reason-modal-model';
import { RejectWithReasonModalComponent } from '../../../components/account-verification-requests-table/admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';

/**
 * Account requests table component for admin search.
 */
@Component({
  selector: 'tm-admin-account-verification-request-search-table',
  templateUrl: './admin-account-verification-request-search-table.component.html',
  styleUrls: ['./admin-account-verification-request-search-table.component.scss'],
  imports: [
    NgbTooltip,
    NgbCollapse,
    AjaxLoadingComponent,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    SearchTermsHighlighterPipe,
    CountryNamePipe,
  ],
})
export class AdminAccountVerificationRequestSearchTableComponent implements OnChanges {
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly accountService = inject(AccountService);
  private readonly ngbModal = inject(NgbModal);

  @Input()
  accountVerificationRequests: AccountVerificationRequestSearchResult[] = [];

  @Input()
  searchString = '';

  isRejectingAccountVerificationRequest: boolean[] = [];
  isApprovingAccountVerificationRequest: boolean[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['accountVerificationRequests']) {
      this.isRejectingAccountVerificationRequest = new Array(this.accountVerificationRequests.length).fill(false);
      this.isApprovingAccountVerificationRequest = new Array(this.accountVerificationRequests.length).fill(false);
    }
  }

  /**
   * Shows all account requests' links in the page.
   */
  showAllAccountVerificationRequestsLinks(): void {
    for (const accountVerificationRequest of this.accountVerificationRequests) {
      accountVerificationRequest.showLinks = true;
    }
  }

  /**
   * Hides all account requests' links in the page.
   */
  hideAllAccountVerificationRequestsLinks(): void {
    for (const accountVerificationRequest of this.accountVerificationRequests) {
      accountVerificationRequest.showLinks = false;
    }
  }

  toggleAccountVerificationRequestLinks(accountVerificationRequest: AccountVerificationRequestSearchResult): void {
    accountVerificationRequest.showLinks = !accountVerificationRequest.showLinks;
  }

  editAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestSearchResult): void {
    const modalRef: NgbModalRef = this.ngbModal.open(EditRequestModalComponent);
    modalRef.componentInstance.accountVerificationRequestName = accountVerificationRequest.name;
    modalRef.componentInstance.accountVerificationRequestEmail = accountVerificationRequest.email;
    modalRef.componentInstance.accountVerificationRequestInstitution = accountVerificationRequest.institute;
    modalRef.componentInstance.accountVerificationRequestCountry = accountVerificationRequest.country;
    modalRef.componentInstance.accountVerificationRequestComments = accountVerificationRequest.comments;

    modalRef.result.then(
      (res: EditRequestModalComponentResult) => {
        this.accountService
          .editAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId, {
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

  approveAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestSearchResult, index: number): void {
    this.isApprovingAccountVerificationRequest[index] = true;
    this.accountService.approveAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId).subscribe({
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

  deleteAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestSearchResult): void {
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
        this.accountService.deleteAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId).subscribe({
          next: (resp: MessageOutput) => {
            this.statusMessageService.showSuccessToast(resp.message);
            this.accountVerificationRequests = this.accountVerificationRequests.filter((x: AccountVerificationRequestSearchResult) => x !== accountVerificationRequest);
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  viewAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestSearchResult): void {
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

  rejectAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestSearchResult, index: number): void {
    this.isRejectingAccountVerificationRequest[index] = true;
    this.accountService.rejectAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId).subscribe({
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

  rejectAccountVerificationRequestWithReason(accountVerificationRequest: AccountVerificationRequestSearchResult, index: number): void {
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
          .rejectAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId, res.rejectionReasonTitle, res.rejectionReasonBody)
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
