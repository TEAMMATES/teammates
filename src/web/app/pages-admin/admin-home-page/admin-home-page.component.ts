import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { CountryService } from '../../../services/country.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequests } from '../../../types/api-output';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { AccountRequestTableComponent } from '../../components/account-requests-table/account-request-table.component';
import {
  ComboboxOption,
  SearchableComboboxComponent,
} from '../../components/searchable-combobox/searchable-combobox.component';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';
import { DateFormatService } from '../../../services/date-format.service';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
  imports: [FormsModule, AccountRequestTableComponent, SearchableComboboxComponent],
  providers: [FormatDateDetailPipe],
})
export class AdminHomePageComponent implements OnInit {
  private accountService = inject(AccountService);
  private countryService = inject(CountryService);
  private statusMessageService = inject(StatusMessageService);
  private timezoneService = inject(TimezoneService);
  private dateFormatService = inject(DateFormatService);

  readonly countryOptions: ComboboxOption<string>[] = this.countryService.getCountryOptions().map((o) => ({
    value: o.code,
    label: o.name,
  }));

  instructorName = '';
  instructorEmail = '';
  instructorInstitution = '';
  instructorCountry = '';
  isAddingSingleInstructor = false;

  accountReqs: AccountRequestTableRowModel[] = [];

  ngOnInit(): void {
    this.fetchAccountRequests();
  }

  /**
   * Validates and adds the instructor detail filled in the form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution || !this.instructorCountry) {
      this.statusMessageService.showWarningToast('Please fill in all fields: Name, Email, Institution, and Country.');
      return;
    }
    const instructorName: string = this.instructorName;
    const instructorEmail: string = this.instructorEmail;
    const instructorInstitution: string = this.instructorInstitution;
    const instructorCountry: string = this.instructorCountry;
    this.isAddingSingleInstructor = true;
    this.accountService
      .createAccountRequest({
        instructorName,
        instructorEmail,
        instructorInstitution,
        instructorCountry,
      })
      .pipe(
        finalize(() => {
          this.isAddingSingleInstructor = false;
        }),
      )
      .subscribe({
        next: () => {
          this.statusMessageService.showSuccessToast('Account request was successfully created');
          this.fetchAccountRequests();
          this.instructorName = '';
          this.instructorEmail = '';
          this.instructorInstitution = '';
          this.instructorCountry = '';
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
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
