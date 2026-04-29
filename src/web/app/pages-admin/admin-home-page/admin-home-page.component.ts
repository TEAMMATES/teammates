import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequests } from '../../../types/api-output';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import {
  AccountRequestTableComponent,
} from '../../components/account-requests-table/account-request-table.component';
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
  isAddingMultipleInstructors: boolean = false;
  isAddingSingleInstructor: boolean = false;

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
    let invalidLinesCount = 0;
    const validRequests: { instructorDetail: string, instructorDetailSplit: string[] }[] = [];

    for (const instructorDetail of this.instructorDetails.split(/\r?\n/)) {
      if (!instructorDetail.trim()) {
        continue;
      }
      const instructorDetailSplit: string[] = instructorDetail.split(/[|\t]/).map((item: string) => item.trim());
      if (instructorDetailSplit.length < 3) {
        invalidLinesCount += 1;
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        invalidLinesCount += 1;
        continue;
      }
      validRequests.push({ instructorDetail, instructorDetailSplit });
    }

    // Do not proceed with backend calls if there are invalid lines
    if (invalidLinesCount > 0) {
      this.statusMessageService.showWarningToast(
        `${invalidLinesCount} line(s) with missing or invalid fields.`
          + ' Format required: Name | Email | Institution');
      return;
    }

    this.isAddingMultipleInstructors = true;

    forkJoin(
      validRequests.map(({ instructorDetail, instructorDetailSplit }) => {
        const instructorName: string = instructorDetailSplit[0];
        return this.accountService.createAccountRequest({
          instructorName,
          instructorEmail: instructorDetailSplit[1],
          instructorInstitution: instructorDetailSplit[2],
        }).pipe(
          map(() => ({ success: true, instructorDetail })),
          catchError(() => of({ success: false, instructorDetail })),
        );
      }),
    ).pipe(
      finalize(() => { this.isAddingMultipleInstructors = false; }),
    ).subscribe((results: { success: boolean, instructorDetail: string }[]) => {
      const failedLines: string[] = results.filter((r) => !r.success).map((r) => r.instructorDetail);
      const successCount: number = results.length - failedLines.length;
      this.instructorDetails = failedLines.join('\r\n');
      if (successCount > 0) {
        const message: string = failedLines.length > 0
          ? `${successCount} account request(s) created, ${failedLines.length} failed`
          : `${successCount} account request(s) were successfully created`;
        this.statusMessageService.showSuccessToast(message);
        this.fetchAccountRequests();
      } else {
        this.statusMessageService.showErrorToast(
          'Failed to create account requests. Use single add to identify errors.');
      }
    });
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      this.statusMessageService.showWarningToast('Please fill in all fields: Name, Email, and Institution.');
      return;
    }
    const instructorName: string = this.instructorName;
    const instructorEmail: string = this.instructorEmail;
    const instructorInstitution: string = this.instructorInstitution;
    this.isAddingSingleInstructor = true;
    this.accountService.createAccountRequest({
      instructorName,
      instructorEmail,
      instructorInstitution,
    }).pipe(
      finalize(() => { this.isAddingSingleInstructor = false; }),
    ).subscribe({
      next: () => {
        this.statusMessageService.showSuccessToast('Account request was successfully created');
        this.fetchAccountRequests();
        this.instructorName = '';
        this.instructorEmail = '';
        this.instructorInstitution = '';
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
