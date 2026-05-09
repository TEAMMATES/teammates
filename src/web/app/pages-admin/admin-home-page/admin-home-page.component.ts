import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequests } from '../../../types/api-output';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { AccountRequestTableComponent } from '../../components/account-requests-table/account-request-table.component';
import { AdminAddInstructorModalComponent } from '../components/admin-add-instructor-modal/admin-add-instructor-modal.component';
import { AdminAddInstructorModalComponentResult } from '../components/admin-add-instructor-modal/admin-add-instructor-modal-model';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
  imports: [FormsModule, AccountRequestTableComponent],
  providers: [FormatDateDetailPipe],
})
export class AdminHomePageComponent implements OnInit {
  showAllRequests = false;
  accountReqs: AccountRequestTableRowModel[] = [];

  constructor(
    private accountService: AccountService,
    private statusMessageService: StatusMessageService,
    private timezoneService: TimezoneService,
    private formatDateDetailPipe: FormatDateDetailPipe,
    private ngbModal: NgbModal,
  ) {}

  ngOnInit(): void {
    this.fetchAccountRequests();
  }

  /**
   * Opens the modal to add a new instructor.
   */
  openAddInstructorModal(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(AdminAddInstructorModalComponent);
    modalRef.result.then(
      (result: AdminAddInstructorModalComponentResult) => {
        this.accountService
          .createAccountRequest({
            instructorName: result.instructorName,
            instructorEmail: result.instructorEmail,
            instructorInstitution: result.instructorInstitution,
          })
          .subscribe({
            next: () => {
              this.statusMessageService.showSuccessToast('Account request was successfully created');
              this.fetchAccountRequests();
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
      },
      () => {}, // modal dismissed
    );
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
          ? this.formatDateDetailPipe.transform(request.registeredAt, timezone)
          : '',
        comments: request.comments || '',
        registrationLink: '',
        showLinks: false,
        isDuplicateEmail: (request as any).isDuplicateEmail || false,
        hasExistingInstructor: (request as any).hasExistingInstructor || false,
        sameInstituteCount: (request as any).sameInstituteCount || 0,
        instituteDomain: request.email ? request.email.split('@')[1] : '',
      };
    });
  }

  fetchAccountRequests(): void {
    const status = this.showAllRequests ? 'ALL' : 'PENDING';
    this.accountService.getAccountRequests(status).subscribe({
      next: (resp: AccountRequests) => {
        this.accountReqs = this.formatAccountRequests(resp);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }
}
