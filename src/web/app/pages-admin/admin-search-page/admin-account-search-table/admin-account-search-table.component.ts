import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import {
  NgbModalRef,
  NgbModal,
  NgbTooltip,
  NgbDropdown,
  NgbDropdownToggle,
  NgbDropdownMenu,
} from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../../services/account.service';
import { AccountRequestSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { AccountRequest, MessageOutput } from '../../../../types/api-output';
import { ErrorMessageOutput } from '../../../error-message-output';
import { SearchTermsHighlighterPipe } from '../../../pipes/search-terms-highlighter.pipe';
import { EditRequestModalComponentResult } from '../../../components/account-requests-table/admin-edit-request-modal/admin-edit-request-modal-model';
import { EditRequestModalComponent } from '../../../components/account-requests-table/admin-edit-request-modal/admin-edit-request-modal.component';
import { RejectWithReasonModalComponentResult } from '../../../components/account-requests-table/admin-reject-with-reason-modal/admin-reject-with-reason-modal-model';
import { RejectWithReasonModalComponent } from '../../../components/account-requests-table/admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';

/**
 * Account requests table component for admin search.
 */
@Component({
  selector: 'tm-admin-account-search-table',
  templateUrl: './admin-account-search-table.component.html',
  styleUrls: ['./admin-account-search-table.component.scss'],
  animations: [collapseAnim],
  imports: [
    NgbTooltip,
    AjaxLoadingComponent,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    SearchTermsHighlighterPipe,
  ],
})
export class AdminAccountSearchTableComponent implements OnChanges {
  @Input()
  accountRequests: AccountRequestSearchResult[] = [];

  @Input()
  searchString = '';

  isRejectingAccount: boolean[] = [];
  isApprovingAccount: boolean[] = [];
  isResettingAccount: boolean[] = [];

  constructor(
    private statusMessageService: StatusMessageService,
    private simpleModalService: SimpleModalService,
    private accountService: AccountService,
    private ngbModal: NgbModal,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['accountRequests']) {
      this.isRejectingAccount = new Array(this.accountRequests.length).fill(false);
      this.isApprovingAccount = new Array(this.accountRequests.length).fill(false);
      this.isResettingAccount = new Array(this.accountRequests.length).fill(false);
    }
  }

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

  toggleAccountRequestLinks(accountRequest: AccountRequestSearchResult): void {
    accountRequest.showLinks = !accountRequest.showLinks;
  }

  editAccountRequest(accountRequest: AccountRequestSearchResult): void {
    const modalRef: NgbModalRef = this.ngbModal.open(EditRequestModalComponent);
    modalRef.componentInstance.accountRequestName = accountRequest.name;
    modalRef.componentInstance.accountRequestEmail = accountRequest.email;
    modalRef.componentInstance.accountRequestInstitution = accountRequest.institute;
    modalRef.componentInstance.accountRequestComments = accountRequest.comments;

    modalRef.result.then(
      (res: EditRequestModalComponentResult) => {
        this.accountService
          .editAccountRequest(accountRequest.id, {
            name: res.accountRequestName,
            email: res.accountRequestEmail,
            institute: res.accountRequestInstitution,
            status: accountRequest.status,
            comments: res.accountRequestComment,
          })
          .subscribe({
            next: (resp: AccountRequest) => {
              accountRequest.comments = resp.comments ?? '';
              accountRequest.name = resp.name;
              accountRequest.email = resp.email;
              accountRequest.institute = resp.institute;
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

  approveAccountRequest(accountRequest: AccountRequestSearchResult, index: number): void {
    this.isApprovingAccount[index] = true;
    this.accountService.approveAccountRequest(accountRequest.id).subscribe({
      next: (resp: AccountRequest) => {
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

  resetAccountRequest(accountRequest: AccountRequestSearchResult, index: number): void {
    this.isResettingAccount[index] = true;
    const modalContent = `Are you sure you want to reset the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.institute}</strong>?
        An email with the account registration link will also be sent to the instructor.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Reset account request for <strong>${accountRequest.name}</strong>?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.dismissed.subscribe(() => {
      this.isResettingAccount[index] = false;
    });

    modalRef.result.then(
      () => {
        this.accountService.resetAccountRequest(accountRequest.id).subscribe({
          next: () => {
            this.statusMessageService.showSuccessToast(
              `Reset successful. An email has been sent to ${accountRequest.email}.`,
            );
            accountRequest.registeredAtText = '';
            this.isResettingAccount[index] = false;
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
            this.isResettingAccount[index] = false;
          },
        });
      },
      () => {},
    );
  }

  deleteAccountRequest(accountRequest: AccountRequestSearchResult): void {
    const modalContent = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.institute}</strong>?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Delete account request for <strong>${accountRequest.name}</strong>?`,
      SimpleModalType.DANGER,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.accountService.deleteAccountRequest(accountRequest.id).subscribe({
          next: (resp: MessageOutput) => {
            this.statusMessageService.showSuccessToast(resp.message);
            this.accountRequests = this.accountRequests.filter((x: AccountRequestSearchResult) => x !== accountRequest);
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  viewAccountRequest(accountRequest: AccountRequestSearchResult): void {
    const modalContent = `<strong>Comment:</strong> ${accountRequest.comments || 'No comments'}`;
    const modalRef: NgbModalRef = this.simpleModalService.openInformationModal(
      `Comments for <strong>${accountRequest.name}</strong> Request`,
      SimpleModalType.INFO,
      modalContent,
    );

    modalRef.result.then(
      () => {},
      () => {},
    );
  }

  rejectAccountRequest(accountRequest: AccountRequestSearchResult, index: number): void {
    this.isRejectingAccount[index] = true;
    this.accountService.rejectAccountRequest(accountRequest.id).subscribe({
      next: (resp: AccountRequest) => {
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

  rejectAccountRequestWithReason(accountRequest: AccountRequestSearchResult, index: number): void {
    this.isRejectingAccount[index] = true;
    const modalRef: NgbModalRef = this.ngbModal.open(RejectWithReasonModalComponent);
    modalRef.componentInstance.accountRequestName = accountRequest.name;
    modalRef.componentInstance.accountRequestEmail = accountRequest.email;

    modalRef.dismissed.subscribe(() => {
      this.isRejectingAccount[index] = false;
    });

    modalRef.result.then(
      (res: RejectWithReasonModalComponentResult) => {
        this.accountService
          .rejectAccountRequest(accountRequest.id, res.rejectionReasonTitle, res.rejectionReasonBody)
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
      },
      () => {},
    );
  }

  trackAccountRequest(_: number, accountRequest: AccountRequestSearchResult): string {
    return accountRequest.id;
  }
}
