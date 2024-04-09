import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { InstructorData } from './instructor-data';
import { AccountService } from '../../../services/account.service';
import { LinkService } from '../../../services/link.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequest, AccountRequests } from '../../../types/api-output';
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

  instructorsConsolidated: InstructorData[] = [];
  accountReqs: AccountRequestTableRowModel[] = [];
  activeRequests: number = 0;
  currentPage: number = 1;
  pageSize: number = 20;
  items$: Observable<any> = of([]);

  isAddingInstructors: boolean = false;

  constructor(
    private accountService: AccountService,
    private statusMessageService: StatusMessageService,
    private timezoneService: TimezoneService,
    private linkService: LinkService,
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
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      this.instructorsConsolidated.push({
        name: instructorDetailSplit[0],
        email: instructorDetailSplit[1],
        institution: instructorDetailSplit[2],
        status: 'PENDING',
        isCurrentlyBeingEdited: false,
      });
    }
    this.instructorDetails = invalidLines.join('\r\n');
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      // TODO handle error
      return;
    }
    this.instructorsConsolidated.push({
      name: this.instructorName,
      email: this.instructorEmail,
      institution: this.instructorInstitution,
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
    });
    this.instructorName = '';
    this.instructorEmail = '';
    this.instructorInstitution = '';
  }

  /**
   * Adds the instructor at the i-th index.
   */
  addInstructor(i: number): void {
    const instructor: InstructorData = this.instructorsConsolidated[i];
    if (this.instructorsConsolidated[i].isCurrentlyBeingEdited
      || (instructor.status !== 'PENDING' && instructor.status !== 'FAIL')) {
      return;
    }
    this.activeRequests += 1;
    instructor.status = 'ADDING';

    this.isAddingInstructors = true;
    this.accountService.createAccountRequest({
      instructorEmail: instructor.email,
      instructorName: instructor.name,
      instructorInstitution: instructor.institution,
    })
        .pipe(finalize(() => {
          this.isAddingInstructors = false;
        }))
        .subscribe({
          next: (resp: AccountRequest) => {
            instructor.status = 'SUCCESS';
            instructor.statusCode = 200;
            instructor.joinLink = this.linkService.generateAccountRegistrationLink(resp.registrationKey);
            this.activeRequests -= 1;
          },
          error: (resp: ErrorMessageOutput) => {
            instructor.status = 'FAIL';
            instructor.statusCode = resp.status;
            instructor.message = resp.error.message;
            this.activeRequests -= 1;
          },
        });
  }

  /**
   * Removes the instructor at the i-th index.
   */
  removeInstructor(i: number): void {
    this.instructorsConsolidated.splice(i, 1);
  }

  /**
   * Sets the i-th instructor data row's edit mode status.
   *
   * @param i The index.
   * @param isEnabled Whether the edit mode status is enabled.
   */
  setInstructorRowEditModeEnabled(i: number, isEnabled: boolean): void {
    this.instructorsConsolidated[i].isCurrentlyBeingEdited = isEnabled;
  }

  /**
   * Adds all the pending and failed-to-add instructors.
   */
  addAllInstructors(): void {
    for (let i: number = 0; i < this.instructorsConsolidated.length; i += 1) {
      this.addInstructor(i);
    }
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
