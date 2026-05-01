import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { LoadingBarService } from '../../../services/loading-bar.service';
import {
  AccountRequestSearchResult,
  AdminSearchResult,
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ApiConst } from '../../../types/api-const';
import { AdminAccountSearchTableComponent } from './admin-account-search-table/admin-account-search-table.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { AdminInstructorSearchTableComponent } from './admin-instructor-search-table/admin-instructor-search-table.component';
import { AdminStudentSearchTableComponent } from './admin-student-search-table/admin-student-search-table.component';

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-search-page',
  templateUrl: './admin-search-page.component.html',
  styleUrls: ['./admin-search-page.component.scss'],
  imports: [
    FormsModule,
    AdminAccountSearchTableComponent,
    AdminInstructorSearchTableComponent,
    AdminStudentSearchTableComponent,
  ],
})
export class AdminSearchPageComponent {
  searchQuery = '';
  searchString = '';
  instructors: InstructorAccountSearchResult[] = [];
  students: StudentAccountSearchResult[] = [];
  accountRequests: AccountRequestSearchResult[] = [];
  characterLimit = 100;

  isRegeneratingInstructorKeys: boolean[] = [];
  isRegeneratingStudentKeys: boolean[] = [];

  constructor(
    private statusMessageService: StatusMessageService,
    private searchService: SearchService,
    private loadingBarService: LoadingBarService,
  ) {}

  /**
   * Searches for students and instructors matching the search query.
   */
  search(): void {
    this.loadingBarService.showLoadingBar();
    this.searchService
      .searchAdmin(this.searchQuery)
      .pipe(
        finalize(() => {
          this.loadingBarService.hideLoadingBar();
        }),
      )
      .subscribe({
        next: (resp: AdminSearchResult) => {
          const hasStudents = !!resp.students?.length;
          const hasInstructors = !!resp.instructors?.length;
          const hasAccountRequests = !!resp.accountRequests?.length;

          if (!hasStudents && !hasInstructors && !hasAccountRequests) {
            this.statusMessageService.showWarningToast('No results found.');
            this.instructors = [];
            this.students = [];
            this.accountRequests = [];
            return;
          }

          this.instructors = resp.instructors;
          this.instructors.forEach((i: InstructorAccountSearchResult) => {
            i.showLinks = false;
          });
          this.students = resp.students;
          this.students.forEach((s: StudentAccountSearchResult) => {
            s.showLinks = false;
          });
          this.accountRequests = this.formatAccountRequests(resp.accountRequests);

          this.isRegeneratingInstructorKeys = new Array(this.instructors.length).fill(false);
          this.isRegeneratingStudentKeys = new Array(this.students.length).fill(false);
          const limit: number = ApiConst.SEARCH_QUERY_SIZE_LIMIT;
          const limitsReached: string[] = [];
          if (this.students.length >= limit) {
            limitsReached.push(`${limit} student results`);
          }
          if (this.instructors.length >= limit) {
            limitsReached.push(`${limit} instructor results`);
          }
          if (this.accountRequests.length >= limit) {
            limitsReached.push(`${limit} account request results`);
          }
          if (limitsReached.length) {
            this.statusMessageService.showWarningToast(`${limitsReached.join(' and ')} have been shown on this page
            but there may be more results not shown. Consider searching with more specific terms.`);
          }

          this.searchString = this.searchQuery;
        },
        error: (resp: ErrorMessageOutput) => {
          this.instructors = [];
          this.students = [];
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  private formatAccountRequests(accountRequests: AccountRequestSearchResult[]): AccountRequestSearchResult[] {
    return accountRequests.map((accountRequest: AccountRequestSearchResult): AccountRequestSearchResult => {
      return {
        id: accountRequest.id,
        name: accountRequest.name,
        email: accountRequest.email,
        status: accountRequest.status,
        institute: accountRequest.institute,
        createdAtText: accountRequest.createdAtText,
        registeredAtText: accountRequest.registeredAtText || '',
        comments: accountRequest.comments,
        registrationLink: accountRequest.registrationLink,
        showLinks: accountRequest.showLinks,
      };
    });
  }
}
