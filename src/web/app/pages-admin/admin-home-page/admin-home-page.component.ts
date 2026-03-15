import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequests } from '../../../types/api-output';
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
  currentPage: number = 1;
  pageSize: number = 20;
  items$: Observable<any> = of([]);
  localDraftCounter: number = 0;

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
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      this.accountReqs.unshift(this.getLocalDraftRowModel(
          instructorDetailSplit[0],
          instructorDetailSplit[1],
          instructorDetailSplit[2],
      ));
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
    this.accountReqs.unshift(this.getLocalDraftRowModel(
      this.instructorName,
      this.instructorEmail,
      this.instructorInstitution,
    ));
    this.instructorName = '';
    this.instructorEmail = '';
    this.instructorInstitution = '';
  }

  private getLocalDraftRowModel(name: string, email: string, institution: string): AccountRequestTableRowModel {
    this.localDraftCounter += 1;
    return {
      id: '',
      localId: `local-draft-${this.localDraftCounter}`,
      name,
      email,
      status: 'DRAFT',
      instituteAndCountry: institution,
      createdAtText: '',
      registeredAtText: '',
      comments: '',
      registrationLink: '',
      showLinks: false,
      isLocalRow: true,
    };
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
        isLocalRow: false,
      };
    });
  }

  fetchAccountRequests(): void {
    const localDraftRows: AccountRequestTableRowModel[] = this.accountReqs
        .filter((row: AccountRequestTableRowModel) => row.isLocalRow);
    this.accountService.getPendingAccountRequests().subscribe({
      next: (resp: AccountRequests) => {
        this.accountReqs = [...localDraftRows, ...this.formatAccountRequests(resp)];
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }
}
