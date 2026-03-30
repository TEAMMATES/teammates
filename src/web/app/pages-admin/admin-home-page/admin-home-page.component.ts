import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequest, AccountRequests } from '../../../types/api-output';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { AccountRequestTableComponent } from '../../components/account-requests-table/account-request-table.component';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
  imports: [
    FormsModule,
    AccountRequestTableComponent,
],
  providers: [FormatDateDetailPipe],
})
export class AdminHomePageComponent implements OnInit {

  instructorDetails: string = '';
  instructorName: string = '';
  instructorEmail: string = '';
  instructorInstitution: string = '';

  accountReqs: AccountRequestTableRowModel[] = [];

  constructor(
    private accountService: AccountService,
    private statusMessageService: StatusMessageService,
    private timezoneService: TimezoneService,
    private formatDateDetailPipe: FormatDateDetailPipe,
  ) {}

  ngOnInit(): void {
    this.fetchAccountRequests();
  }

  /**
   * Validates and adds the instructor details filled with first form.
   */
  validateAndAddInstructorDetails(): void {
    const invalidLines: string[] = [];
    for (const instructorDetail of this.instructorDetails.split(/\r?\n/)) {
      const instructorDetailSplit: string[] = instructorDetail.split(/[|\t]/).map((item: string) => item.trim());
      if (instructorDetailSplit.length < 3) {
        invalidLines.push(instructorDetail);
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        invalidLines.push(instructorDetail);
        continue;
      }
      this.accountService.createAccountRequest({
        instructorName: instructorDetailSplit[0],
        instructorEmail: instructorDetailSplit[1],
        instructorInstitution: instructorDetailSplit[2],
      }).subscribe({
        next: (resp: AccountRequest) => {
          this.statusMessageService.showSuccessToast(
              `Instructor "${instructorDetailSplit[0]}" has been successfully created`);
          this.fetchAccountRequests();
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }
    this.instructorDetails = invalidLines.join('\r\n');
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      return;
    }
    const name: string = this.instructorName;
    this.accountService.createAccountRequest({
      instructorName: this.instructorName,
      instructorEmail: this.instructorEmail,
      instructorInstitution: this.instructorInstitution,
    }).subscribe({
      next: (resp: AccountRequest) => {
        this.statusMessageService.showSuccessToast(`Instructor "${name}" has been successfully created`);
        this.fetchAccountRequests();
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
    this.instructorName = '';
    this.instructorEmail = '';
    this.instructorInstitution = '';
  }

  private formatAccountRequests(requests: AccountRequests): AccountRequestTableRowModel[] {
    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    return requests.accountRequests.map((request) => {
      return {
        id: request.id,
        name: request.name,
        email: request.email,
        status: request.status,
        instituteAndCountry: request.institute,
        createdAtText: this.formatDateDetailPipe.transform(request.createdAt, timezone),
        registeredAtText: request.registeredAt
        ? this.formatDateDetailPipe.transform(request.registeredAt, timezone) : '',
        comments: request.comments || '',
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
