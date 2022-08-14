import { Component, Input, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequest, AccountRequestStatus } from '../../../types/api-output';
import { AccountRequestUpdateRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { collapseAnim } from '../teammates-common/collapse-anim';

export enum ProcessAccountRequestPanelStatus {
  SUBMITTED,
  EDITING,
  APPROVED,
  REJECTED,
  REGISTERED,
  UNDEFINED,
}

/**
 * Panel used to display an account request being processed.
 */
@Component({
  selector: 'tm-process-account-request-panel',
  templateUrl: './process-account-request-panel.component.html',
  styleUrls: ['./process-account-request-panel.component.scss'],
  animations: [collapseAnim],
})
export class ProcessAccountRequestPanelComponent implements OnInit {

  /**
   * The index of the panel, which should be provided and unique if the component is rendered multiple times in a page.
   * The default value is an empty string.
   */
  @Input()
  identifier: string = '';

  @Input()
  accountRequest!: AccountRequest;

  @Input()
  isTabExpanded: boolean = true;

  @Input()
  showPanelHeader: boolean = true;

  @Input()
  panelHeaderColor: string = 'primary';

  @Input()
  showRegisteredAt: boolean = false;

  panelStatus: ProcessAccountRequestPanelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  isSavingChanges: boolean = false;
  errorMessage: string = '';

  editedName!: string;
  editedInstitute!: string;
  editedEmail!: string;

  // enums
  ProcessAccountRequestPanelStatus: typeof ProcessAccountRequestPanelStatus = ProcessAccountRequestPanelStatus;
  AccountRequestStatus: typeof AccountRequestStatus = AccountRequestStatus;

  timezone: string = '';

  constructor(private accountService: AccountService,
              private statusMessageService: StatusMessageService,
              private simpleModalService: SimpleModalService,
              private timezoneService: TimezoneService) {
  }

  ngOnInit(): void {
    this.panelStatus = this.getPanelStatusFromAccountRequestStatus(this.accountRequest.status);
    this.timezone = this.timezoneService.guessTimezone() || 'UTC';
  }

  editAccountRequest(): void {
    this.editedName = this.accountRequest!.name;
    this.editedInstitute = this.accountRequest!.institute;
    this.editedEmail = this.accountRequest!.email;

    this.errorMessage = '';
    this.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
  }

  cancelEditAccountRequest(): void {
    this.errorMessage = '';
    this.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  }

  /**
   * Updates the account request.
   */
  saveAccountRequest(): void {
    this.isSavingChanges = true;
    const updateRequest: AccountRequestUpdateRequest = {
      instructorName: this.editedName,
      instructorEmail: this.editedEmail,
      instructorInstitute: this.editedInstitute,
    };
    this.accountService.updateAccountRequest(this.accountRequest.email, this.accountRequest.institute, updateRequest)
      .pipe(
        finalize(() => {
          this.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        this.accountRequest = resp;
        this.errorMessage = '';
        this.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
        this.statusMessageService.showSuccessToast('Account request successfully updated.');
      }, (resp: ErrorMessageOutput) => {
        this.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to update account request.');
      });
  }

  /**
   * Approves the account request.
   */
  approveAccountRequest(): void {
    this.isSavingChanges = true;
    this.accountService.approveAccountRequest(this.accountRequest.email, this.accountRequest.institute)
      .pipe(
        finalize(() => {
          this.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        this.accountRequest = resp;
        this.errorMessage = '';
        this.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
        this.statusMessageService.showSuccessToast('Account request successfully approved.');
      }, (resp: ErrorMessageOutput) => {
        this.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to approve account request.');
      });
  }

  /**
   * Rejects the account request.
   */
  rejectAccountRequest(): void {
    this.isSavingChanges = true;
    this.accountService.rejectAccountRequest(this.accountRequest.email, this.accountRequest.institute)
      .pipe(
        finalize(() => {
          this.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        this.accountRequest = resp;
        this.errorMessage = '';
        this.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
        this.statusMessageService.showSuccessToast('Account request successfully rejected.');
      }, (resp: ErrorMessageOutput) => {
        this.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to reject account request.');
      });
  }

  /**
   * Deletes the account request.
   */
  deleteAccountRequest(): void {
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      'Delete account request?',
      SimpleModalType.WARNING,
      'Are you sure you want to delete this account request? This action is irreversible.');

    modalRef.result.then(() => {
      this.isSavingChanges = true;
      this.accountService.deleteAccountRequest(this.accountRequest.email, this.accountRequest.institute)
        .pipe(
          finalize(() => {
            this.isSavingChanges = false;
          }))
        .subscribe(() => {
          this.errorMessage = '';
          this.panelStatus = ProcessAccountRequestPanelStatus.UNDEFINED;
          this.statusMessageService.showSuccessToast('Account request successfully deleted.');
        }, (resp: ErrorMessageOutput) => {
          this.isSavingChanges = false;
          this.errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast('Failed to delete account request.');
        });
    }, () => {});
  }

  showOptionalResetAccountRequestWarning(): void {
    if (this.accountRequest.status === AccountRequestStatus.REGISTERED) {
      const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Reset account request?',
        SimpleModalType.WARNING,
        'It appears that the registration key with this account request has already been used to create an account'
        + ' and the instructor has joined TEAMMATES. Resetting it now will change its status back to SUBMITTED,'
        + ' after which it can be approved again and the join link can be reused to create another account.'
        + ' Are you sure you want to reset this account request? This action is irreversible.');
      modalRef.result.then(() => {
        this.resetAccountRequest();
      }, () => {});
    } else {
      this.resetAccountRequest();
    }
  }

  /**
   * Resets the account request.
   */
  resetAccountRequest(): void {
    this.isSavingChanges = true;
    this.accountService.resetAccountRequest(this.accountRequest.email, this.accountRequest.institute)
      .pipe(
        finalize(() => {
          this.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        this.accountRequest = resp;
        this.errorMessage = '';
        this.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
        this.statusMessageService.showSuccessToast('Account request successfully reset.');
      }, (resp: ErrorMessageOutput) => {
        this.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to reset account request.');
      });
  }

  toggleCard(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

  getPanelStatusFromAccountRequestStatus(arStatus : AccountRequestStatus): ProcessAccountRequestPanelStatus {
    switch (arStatus) {
      case AccountRequestStatus.SUBMITTED:
        return ProcessAccountRequestPanelStatus.SUBMITTED;
      case AccountRequestStatus.APPROVED:
        return ProcessAccountRequestPanelStatus.APPROVED;
      case AccountRequestStatus.REJECTED:
        return ProcessAccountRequestPanelStatus.REJECTED;
      case AccountRequestStatus.REGISTERED:
        return ProcessAccountRequestPanelStatus.REGISTERED;
      default:
        return ProcessAccountRequestPanelStatus.UNDEFINED;
    }
  }

}
