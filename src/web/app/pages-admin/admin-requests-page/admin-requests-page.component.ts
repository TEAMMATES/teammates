import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequest, AccountRequests, AccountRequestStatus } from '../../../types/api-output';
import { AccountRequestUpdateRequest } from '../../../types/api-request';
import { DateFormat } from '../../components/datepicker/datepicker.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { removeAnim } from '../../components/teammates-common/remove-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  EditedAccountRequestInfoModel,
  ProcessAccountRequestPanelStatus,
} from './process-account-request-panel/process-account-request-panel.component';

export interface AccountRequestTab {
  accountRequest: AccountRequest;
  isTabExpanded: boolean;
  panelStatus: ProcessAccountRequestPanelStatus;
  isSavingChanges: boolean;
  errorMessage: string;
}

interface FormQueryModel {
  fromDate: DateFormat;
  toDate: DateFormat;
}

/**
 * Admin requests page.
 */
@Component({
  selector: 'tm-admin-requests-page',
  templateUrl: './admin-requests-page.component.html',
  styleUrls: ['./admin-requests-page.component.scss'],
  animations: [collapseAnim, removeAnim],
})
export class AdminRequestsPageComponent implements OnInit {

  accountRequestPendingProcessingTabs: AccountRequestTab[] = [];
  hasAccountRequestsPendingProcessingLoadingFailed: boolean = false;
  isLoadingAccountRequestsPendingProcessing: boolean = false;

  accountRequestWithinPeriodTabs: AccountRequestTab[] = [];
  hasAccountRequestsWithinPeriodLoadingFailed: boolean = false;
  isLoadingAccountRequestsWithinPeriod: boolean = false;
  hasQueried: boolean = false;

  formModel: FormQueryModel = {
    fromDate: { year: 0, month: 0, day: 0 },
    toDate: { year: 0, month: 0, day: 0 },
  };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 2016, month: 1, day: 1 };
  timezone: string = '';

  constructor(private accountService: AccountService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) {
  }

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone() || 'UTC';

    const now = new Date();
    this.dateToday.year = now.getFullYear();
    this.dateToday.month = now.getMonth() + 1;
    this.dateToday.day = now.getDate();

    // Default start date is one week before
    const fromDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    this.formModel.fromDate = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };
    this.formModel.toDate = { ...this.dateToday };

    this.loadAccountRequestsPendingProcessing();
  }

  /**
   * Loads all account requests pending processing.
   */
  loadAccountRequestsPendingProcessing(): void {
    this.accountRequestPendingProcessingTabs = [];
    this.hasAccountRequestsPendingProcessingLoadingFailed = false;
    this.isLoadingAccountRequestsPendingProcessing = true;

    this.accountService.getAccountRequestsPendingProcessing()
      .pipe(finalize(() => {
        this.isLoadingAccountRequestsPendingProcessing = false;
      }))
      .subscribe((resp: AccountRequests) => {
        resp.accountRequests.forEach((ar: AccountRequest) => {
          const accountRequestTab: AccountRequestTab = {
            accountRequest: ar,
            isTabExpanded: true,
            panelStatus: ProcessAccountRequestPanelStatus.SUBMITTED,
            isSavingChanges: false,
            errorMessage: '',
          };
          this.accountRequestPendingProcessingTabs.push(accountRequestTab);
        });
        // TODO: sort account requests
      }, (resp: ErrorMessageOutput) => {
        this.accountRequestPendingProcessingTabs = [];
        this.hasAccountRequestsPendingProcessingLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Sets the panel status to EDITING.
   */
  editAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.errorMessage = '';
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
  }

  /**
   * Sets the panel status to SUBMITTED.
   */
  cancelEditAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.errorMessage = '';
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  }

  /**
   * Updates the account request in the tab.
   */
  saveAccountRequest(accountRequestTab: AccountRequestTab, editedInfo: EditedAccountRequestInfoModel): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    const updateRequest: AccountRequestUpdateRequest = {
      instructorName: editedInfo.editedName,
      instructorEmail: editedInfo.editedEmail,
      instructorInstitute: editedInfo.editedInstitute,
    };
    this.accountService.updateAccountRequest(accountRequest.email, accountRequest.institute, updateRequest)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
      }))
      .subscribe((resp: AccountRequest) => {
        accountRequestTab.accountRequest = resp;
        accountRequestTab.errorMessage = '';
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
        this.statusMessageService.showSuccessToast('Account request successfully updated.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to update account request.');
      });
  }

  /**
   * Approves the account request in the tab.
   */
  approveAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.approveAccountRequest(accountRequest.email, accountRequest.institute)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        accountRequestTab.accountRequest = resp;
        accountRequestTab.errorMessage = '';
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
        this.statusMessageService.showSuccessToast('Account request successfully approved.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to approve account request.');
      });
  }

  /**
   * Rejects the account request in the tab.
   */
  rejectAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.rejectAccountRequest(accountRequest.email, accountRequest.institute)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        accountRequestTab.accountRequest = resp;
        accountRequestTab.errorMessage = '';
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
        this.statusMessageService.showSuccessToast('Account request successfully rejected.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to reject account request.');
      });
  }

  /**
   * Deletes the account request in the tab.
   */
  deleteAccountRequest(accountRequestTab: AccountRequestTab, index: number): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.deleteAccountRequest(accountRequest.email, accountRequest.institute)
      .subscribe(() => {
        accountRequestTab.errorMessage = '';
        this.accountRequestPendingProcessingTabs.splice(index, 1);
        this.statusMessageService.showSuccessToast('Account request successfully deleted.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.isSavingChanges = false;
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to delete account request.');
      });
  }

  /**
   * Resets the account request in the tab.
   */
  resetAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isSavingChanges = true;
    const accountRequest: AccountRequest = accountRequestTab.accountRequest;
    this.accountService.resetAccountRequest(accountRequest.email, accountRequest.institute)
      .pipe(
        finalize(() => {
          accountRequestTab.isSavingChanges = false;
        }))
      .subscribe((resp: AccountRequest) => {
        accountRequestTab.accountRequest = resp;
        accountRequestTab.errorMessage = '';
        accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
        this.statusMessageService.showSuccessToast('Account request successfully reset.');
      }, (resp: ErrorMessageOutput) => {
        accountRequestTab.errorMessage = resp.error.message;
        this.statusMessageService.showErrorToast('Failed to reset account request.');
      });
  }

  /**
   * Toggles the specific account request card.
   */
  toggleCard(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isTabExpanded = !accountRequestTab.isTabExpanded;
  }

  /**
   * Loads all account requests submitted within the period specified in formModel.
   */
  loadAccountRequestsWithinPeriod(): void {
    if (this.formModel.fromDate === null) {
      this.statusMessageService.showErrorToast('Please enter a start date');
      return;
    }
    if (this.formModel.toDate === null) {
      this.statusMessageService.showErrorToast('Please enter a end date');
      return;
    }

    this.hasQueried = true;
    this.accountRequestWithinPeriodTabs = [];
    this.hasAccountRequestsWithinPeriodLoadingFailed = false;
    this.isLoadingAccountRequestsWithinPeriod = true;

    const timestampFrom = this.timezoneService.resolveLocalDateTime(
      this.formModel.fromDate, { hour: 0, minute: 0 }, this.timezone);
    const timestampTo = this.timezoneService.resolveLocalDateTime(
      this.formModel.toDate, { hour: 23, minute: 59 }, this.timezone);
    this.accountService.getAccountRequestsWithinPeriod(timestampFrom, timestampTo)
      .pipe(finalize(() => {
        this.isLoadingAccountRequestsWithinPeriod = false;
      }))
      .subscribe((resp: AccountRequests) => {
        resp.accountRequests.forEach((ar: AccountRequest) => {
          const accountRequestTab: AccountRequestTab = {
            accountRequest: ar,
            isTabExpanded: false,
            panelStatus: this.getPanelStatusFromAccountRequestStatus(ar.status),
            isSavingChanges: false,
            errorMessage: '',
          };
          this.accountRequestWithinPeriodTabs.push(accountRequestTab);
        });
        // TODO: sort account requests
      }, (resp: ErrorMessageOutput) => {
        this.accountRequestWithinPeriodTabs = [];
        this.hasAccountRequestsWithinPeriodLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
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
