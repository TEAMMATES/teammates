import { Component, Input } from '@angular/core';
import { NgbModalRef, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import {
  RejectWithReasonModalComponent,
} from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * Contains details about a the account request to be displayed in the list.
 */
export interface AccountRequestData {
  name: string;
  email: string;
  status: string;
  institute: string;
  country: string;
  createdAtText: string;
  registeredAtText: string;
  comments: string;
  registrationLink: string;
  showLinks: boolean;
}

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-account-requests-table',
  templateUrl: './account-requests-table.component.html',
  styleUrls: ['./account-requests-table.component.scss'],
  animations: [collapseAnim],
})

export class AccountRequestsTableComponent {

  @Input()
  accountRequests: AccountRequestData[] = [];

  @Input()
  searchString = '';

  constructor(
    private statusMessageService: StatusMessageService,
    private simpleModalService: SimpleModalService,
    private accountService: AccountService,
    private ngbModal: NgbModal,
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

  resetAccountRequest(accountRequest: AccountRequestData): void {
    const modalContent = `Are you sure you want to reset the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.institute}</strong>?
        An email with the account registration link will also be sent to the instructor.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Reset account request for <strong>${accountRequest.name}</strong>?`, SimpleModalType.WARNING, modalContent);

    modalRef.result.then(() => {
      this.accountService.resetAccountRequest(accountRequest.email, accountRequest.institute)
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

  editAccountRequest(accountRequest: AccountRequestData): void {
    const modalRef: NgbModalRef = this.ngbModal.open(EditRequestModalComponent);
    modalRef.componentInstance.accountRequestName = accountRequest.name;
    modalRef.componentInstance.accountRequestEmail = accountRequest.email;
    modalRef.componentInstance.accountRequestInstitution = accountRequest.institute;
    modalRef.componentInstance.accountRequestComment = accountRequest.comments;

    modalRef.result.then(() => {
      this.accountService.editAccountRequest(
        modalRef.componentInstance.accountRequestName,
        modalRef.componentInstance.accountRequestEmail,
        modalRef.componentInstance.accountRequestInstitution,
        modalRef.componentInstance.accountRequestComment)
      .subscribe({
        next: (resp: MessageOutput) => {
          this.statusMessageService.showSuccessToast(resp.message);
          accountRequest.comments = modalRef.componentInstance.accountRequestComment;
          accountRequest.name = modalRef.componentInstance.accountRequestName;
          accountRequest.email = modalRef.componentInstance.accountRequestEmail;
          accountRequest.institute = modalRef.componentInstance.accountRequestInstitution;
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    });
  }

  deleteAccountRequest(accountRequest: AccountRequestData): void {
    const modalContent: string = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>${accountRequest.name}</strong> with email <strong>${accountRequest.email}</strong> from
        <strong>${accountRequest.institute}</strong>?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete account request for <strong>${accountRequest.name}</strong>?`, SimpleModalType.DANGER, modalContent);

    modalRef.result.then(() => {
      this.accountService.deleteAccountRequest(accountRequest.email, accountRequest.institute)
      .subscribe({
        next: (resp: MessageOutput) => {
          this.statusMessageService.showSuccessToast(resp.message);
          this.accountRequests = this.accountRequests.filter((x: AccountRequestData) => x !== accountRequest);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  viewAccountRequest(accountRequest: AccountRequestData): void {
    const modalContent: string = `<strong>Comment:</strong> ${accountRequest.comments || ''}`;
    const modalRef: NgbModalRef = this.simpleModalService.openInformationModal(
        `Comments for <strong>${accountRequest.name}</strong> Request`, SimpleModalType.INFO, modalContent);

    modalRef.result.then(() => {}, () => {});
  }

  private getRegistrationKeyFromLink(link: string): string {
    return link.split('key=')[1];
  }

  approveAccountRequest(accountRequest: AccountRequestData): void {
    this.accountService.createAccount(this.getRegistrationKeyFromLink(accountRequest.registrationLink), '')
    .subscribe({
      next: () => {
        accountRequest.status = 'APPROVED';
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  rejectAccountRequest(accountRequest: AccountRequestData): void {
    this.accountService.rejectAccountRequest(accountRequest.name, accountRequest.email,
      accountRequest.institute)
    .subscribe({
      next: () => {
        accountRequest.status = 'REJECTED';
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  rejectAccountRequestWithReason(accountRequest: AccountRequestData): void {
    const modalRef: NgbModalRef = this.ngbModal.open(RejectWithReasonModalComponent);
    modalRef.componentInstance.accountRequestName = accountRequest.name;

    modalRef.result.then(() => {
      this.accountService.rejectAccountRequest(accountRequest.email, accountRequest.institute,
        modalRef.componentInstance.rejectionReasonTitle, modalRef.componentInstance.rejectionReasonBody)
      .subscribe({
        next: () => {
          accountRequest.status = 'REJECTED';
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

}
