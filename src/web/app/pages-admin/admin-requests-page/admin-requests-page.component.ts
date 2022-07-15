import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequest, AccountRequests } from '../../../types/api-output';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { removeAnim } from '../../components/teammates-common/remove-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  ProcessAccountRequestPanelStatus,
} from './process-account-request-panel/process-account-request-panel.component';

export interface AccountRequestTab {
  accountRequest: AccountRequest;
  isTabExpanded: boolean;
  panelStatus: ProcessAccountRequestPanelStatus;
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

  constructor(private accountService: AccountService,
              private statusMessageService: StatusMessageService) {
  }

  ngOnInit(): void {
    this.loadAccountRequestsPendingProcessing();
  }

  /**
   * Loads account requests pending processing.
   */
  loadAccountRequestsPendingProcessing(): void {
    this.hasAccountRequestsPendingProcessingLoadingFailed = false;
    this.isLoadingAccountRequestsPendingProcessing = true;

    setTimeout(() => {this.accountService.getAccountRequestsPendingProcessing()
      .pipe(finalize(() => {
        this.isLoadingAccountRequestsPendingProcessing = false;
      }))
      .subscribe((resp: AccountRequests) => {
        // this.hasAccountRequestsPendingProcessingLoadingFailed = true;
        resp.accountRequests.forEach((accountRequest: AccountRequest) => {
          const accountRequestTab: AccountRequestTab = {
            accountRequest: accountRequest,
            isTabExpanded: true,
            panelStatus: ProcessAccountRequestPanelStatus.SUBMITTED,
          };
          this.accountRequestPendingProcessingTabs.push(accountRequestTab);
        });
        // TODO: sort courses

        // console.log(resp.accountRequests);
      }, (resp: ErrorMessageOutput) => {
        this.accountRequestPendingProcessingTabs = [];
        this.hasAccountRequestsPendingProcessingLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });}, 1000);

  }

  /**
   * Sets the panel status to EDITING.
   */
  editAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
  }

  /**
   * Sets the panel status to SUBMITTED.
   */
  cancelEditAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  }

  /**
   * Updates the account request in the tab.
   */
  saveAccountRequest(accountRequestTab: AccountRequestTab): void { // TODO: add parameter of edited info
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  }

  /**
   * Approves the account request in the tab.
   */
  approveAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
  }

  /**
   * Rejects the account request in the tab.
   */
  rejectAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
  }

  /**
   * Deletes the account request in the tab.
   */
  deleteAccountRequest(accountRequestTab: AccountRequestTab, index: number): void {
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED; // doesn't matter, the tab should be removed
    this.accountRequestPendingProcessingTabs.splice(index, 1);
  }

  /**
   * Resets the account request in the tab.
   */
  resetAccountRequest(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.panelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;
  }

  /**
   * Toggles the specific account request card.
   */
  toggleCard(accountRequestTab: AccountRequestTab): void {
    accountRequestTab.isTabExpanded = !accountRequestTab.isTabExpanded;
  }

}
