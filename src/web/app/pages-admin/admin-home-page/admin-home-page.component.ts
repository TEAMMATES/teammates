import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequests } from '../../../types/api-output';
import { AccountCreateRequest } from '../../../types/api-request';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
})
export class AdminHomePageComponent implements OnInit {

  instructorDetails: string = '';
  instructorName: string = '';
  instructorEmail: string = '';
  instructorInstitution: string = '';

  accountReqs: AccountRequestTableRowModel[] = [];
  activeRequests: number = 0;
  currentPage: number = 1;
  pageSize: number = 20;
  items$: Observable<any> = of([]);

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
  validateAndAddInstructorDetails(): Promise<void> {
    const lines: string[] = this.instructorDetails.split(/\r?\n/);
    const invalidLines: string[] = [];
    const accountRequests: Promise<void>[] = [];
    for (const line of lines) {
      const instructorDetailsSplit: string[] = line.split(/[|\t]/).map((item: string) => item.trim());

      const requestData: AccountCreateRequest = {
        instructorEmail: instructorDetailsSplit[1],
        instructorName: instructorDetailsSplit[0],
        instructorInstitution: instructorDetailsSplit[2],
      };

      const newRequest: Promise<void> = new Promise((resolve, reject) => {
        this.accountService.createAccountRequest(requestData)
        .subscribe({
          next: () => {
            resolve();
          },
          error: (resp: ErrorMessageOutput) => {
            invalidLines.push(line);
            this.statusMessageService.showErrorToast(resp.error.message);
            reject();
          },
        });
      });

      accountRequests.push(newRequest);
    }

    return Promise.allSettled(accountRequests).then(() => {
      this.instructorDetails = invalidLines.join('\r\n');
      this.fetchAccountRequests();
    });
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      // TODO handle error
      return;
    }

    const requestData: AccountCreateRequest = {
      instructorEmail: this.instructorEmail,
      instructorName: this.instructorName,
      instructorInstitution: this.instructorInstitution,
    };

    this.accountService.createAccountRequest(requestData)
    .subscribe({
      next: () => {
        this.instructorName = '';
        this.instructorEmail = '';
        this.instructorInstitution = '';

        this.fetchAccountRequests();
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
