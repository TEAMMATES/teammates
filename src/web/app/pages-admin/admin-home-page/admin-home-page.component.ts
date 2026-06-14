import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequests } from '../../../types/api-output';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { AccountRequestTableComponent } from '../../components/account-requests-table/account-request-table.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { DateFormatService } from '../../../services/date-format.service';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  imports: [FormsModule, AccountRequestTableComponent],
})
export class AdminHomePageComponent implements OnInit {
  private accountService = inject(AccountService);
  private statusMessageService = inject(StatusMessageService);
  private timezoneService = inject(TimezoneService);
  private dateFormatService = inject(DateFormatService);

  accountReqs: AccountRequestTableRowModel[] = [];

  ngOnInit(): void {
    this.fetchAccountRequests();
  }

  private formatAccountRequests(requests: AccountRequests): AccountRequestTableRowModel[] {
    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    return requests.accountRequests.map((request) => {
      return {
        id: request.accountRequestId,
        name: request.name,
        email: request.email,
        status: request.status,
        institute: request.institute,
        country: request.country,
        createdAtText: this.dateFormatService.formatDateDetailed(request.createdAt, timezone),
        registeredAtText: request.registeredAt
          ? this.dateFormatService.formatDateDetailed(request.registeredAt, timezone)
          : '',
        comments: request.comments ?? '',
        registrationLink: '',
        showLinks: false,
      };
    });
  }

  fetchAccountRequests(): void {
    this.accountService.getPendingAccountRequests().subscribe({
      next: (resp: AccountRequests) => {
        this.accountReqs = this.formatAccountRequests(resp);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }
}
