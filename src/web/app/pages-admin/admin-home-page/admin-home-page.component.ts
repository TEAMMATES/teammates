import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountVerificationRequestStatus, AccountVerificationRequests } from '../../../types/api-output';
import { AccountVerificationRequestTableRowModel } from '../../components/account-verification-requests-table/account-verification-request-table-model';
import { AccountVerificationRequestTableComponent } from '../../components/account-verification-requests-table/account-verification-request-table.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { DateFormatService } from '../../../services/date-format.service';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  imports: [FormsModule, AccountVerificationRequestTableComponent],
})
export class AdminHomePageComponent implements OnInit {
  private accountService = inject(AccountService);
  private statusMessageService = inject(StatusMessageService);
  private timezoneService = inject(TimezoneService);
  private dateFormatService = inject(DateFormatService);

  accountReqs: AccountVerificationRequestTableRowModel[] = [];

  ngOnInit(): void {
    this.fetchAccountVerificationRequests();
  }

  private formatAccountVerificationRequests(
    requests: AccountVerificationRequests,
  ): AccountVerificationRequestTableRowModel[] {
    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    return requests.accountVerificationRequests.map((request) => {
      return {
        id: request.accountVerificationRequestId,
        name: request.name,
        email: request.email,
        status: request.status,
        institute: request.institute,
        country: request.country,
        createdAtText: this.dateFormatService.formatDateDetailed(request.createdAt, timezone),
        createdDemoCourseAtText: request.createdDemoCourseAt
          ? this.dateFormatService.formatDateDetailed(request.createdDemoCourseAt, timezone)
          : '',
        comments: request.comments ?? '',
        registrationLink: '',
        showLinks: false,
      };
    });
  }

  fetchAccountVerificationRequests(): void {
    this.accountService.getAccountVerificationRequests({
      status: AccountVerificationRequestStatus.PENDING,
    }).subscribe({
      next: (resp: AccountVerificationRequests) => {
        this.accountReqs = this.formatAccountVerificationRequests(resp);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }
}
