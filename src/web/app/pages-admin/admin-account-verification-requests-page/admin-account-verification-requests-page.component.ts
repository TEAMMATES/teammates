import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { EMPTY, catchError, map, switchMap } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { DateFormatService } from '../../../services/date-format.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountVerificationRequests } from '../../../types/api-output';
import { AccountVerificationRequestTableRowModel } from '../../components/account-verification-requests-table/account-verification-request-table-model';
import { AccountVerificationRequestTableComponent } from '../../components/account-verification-requests-table/account-verification-request-table.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin page for listing and searching account verification requests.
 */
@Component({
  selector: 'tm-admin-account-verification-requests-page',
  templateUrl: './admin-account-verification-requests-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, AccountVerificationRequestTableComponent],
})
export class AdminAccountVerificationRequestsPageComponent {
  private readonly accountService = inject(AccountService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly dateFormatService = inject(DateFormatService);

  readonly searchQuery = signal('');
  private readonly committedSearch = signal('');

  readonly accountReqs = toSignal(
    toObservable(this.committedSearch).pipe(
      switchMap((query) =>
        this.accountService.getAccountVerificationRequests({ searchKey: query || undefined }).pipe(
          map((resp) => this.mapRequests(resp)),
          catchError((err: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(err.error.message);
            return EMPTY;
          }),
        ),
      ),
    ),
    { initialValue: [] },
  );

  search(): void {
    this.committedSearch.set(this.searchQuery());
  }

  private mapRequests(resp: AccountVerificationRequests): AccountVerificationRequestTableRowModel[] {
    const timezone = this.timezoneService.guessTimezone() || 'UTC';
    return resp.accountVerificationRequests.map((request) => ({
      id: request.accountVerificationRequestId,
      accountId: request.accountId,
      name: request.name,
      email: request.email,
      status: request.status,
      institute: request.institute,
      country: request.country,
      createdAtText: this.dateFormatService.formatDateBrief(request.createdAt, timezone),
      createdDemoCourseAtText: request.createdDemoCourseAt
        ? this.dateFormatService.formatDateDetailed(request.createdDemoCourseAt, timezone)
        : '',
      comments: request.comments ?? '',
      registrationLink: '',
      showLinks: false,
    }));
  }
}
