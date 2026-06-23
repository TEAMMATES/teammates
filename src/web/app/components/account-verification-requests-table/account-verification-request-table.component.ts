import { Component, Input, inject } from '@angular/core';
import { AccountVerificationRequestTableRowModel } from './account-verification-request-table-model';
import { AccountService } from '../../../services/account.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountVerificationRequest } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { CountryNamePipe } from '../../pipes/country-name.pipe';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';

/**
 * Pending account verification requests table component for approval workflow.
 */
@Component({
  selector: 'tm-account-verification-request-table',
  templateUrl: './account-verification-request-table.component.html',
  styleUrls: ['./account-verification-request-table.component.scss'],
  imports: [AjaxLoadingComponent, CountryNamePipe],
})
export class AccountVerificationRequestTableComponent {
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly accountService = inject(AccountService);
  private readonly navigationService = inject(NavigationService);

  @Input()
  accountVerificationRequests: AccountVerificationRequestTableRowModel[] = [];

  isApprovingAccountVerificationRequest: boolean[] = new Array(this.accountVerificationRequests.length).fill(false);

  approveAccountVerificationRequest(
    accountVerificationRequest: AccountVerificationRequestTableRowModel,
    index: number,
  ): void {
    this.isApprovingAccountVerificationRequest[index] = true;
    this.accountService.approveAccountVerificationRequest(accountVerificationRequest.id).subscribe({
      next: (resp: AccountVerificationRequest) => {
        accountVerificationRequest.status = resp.status;
        this.statusMessageService.showSuccessToast(
          `Account verification request was successfully approved. Email has been sent to ${accountVerificationRequest.email}.`,
        );
        this.isApprovingAccountVerificationRequest[index] = false;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isApprovingAccountVerificationRequest[index] = false;
      },
    });
  }

  reviewAccountVerificationRequest(accountVerificationRequest: AccountVerificationRequestTableRowModel): void {
    this.navigationService.navigateByURL(`/web/admin/account-verification-requests/${accountVerificationRequest.id}`);
  }
}
