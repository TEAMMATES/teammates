import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { LoadingBarService } from '../../../services/loading-bar.service';
import {
  AccountVerificationRequestSearchResult,
  AdminSearchResult,
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ApiConst } from '../../../types/api-const';
import { AdminAccountVerificationRequestSearchTableComponent } from './admin-account-verification-request-search-table/admin-account-verification-request-search-table.component';
import { AdminInstructorSearchTableComponent } from './admin-instructor-search-table/admin-instructor-search-table.component';
import { AdminStudentSearchTableComponent } from './admin-student-search-table/admin-student-search-table.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-search-page',
  templateUrl: './admin-search-page.component.html',
  styleUrls: ['./admin-search-page.component.scss'],
  imports: [
    FormsModule,
    AdminAccountVerificationRequestSearchTableComponent,
    AdminInstructorSearchTableComponent,
    AdminStudentSearchTableComponent,
  ],
})
export class AdminSearchPageComponent {
  private statusMessageService = inject(StatusMessageService);
  private searchService = inject(SearchService);
  private loadingBarService = inject(LoadingBarService);

  searchQuery = '';
  searchString = '';
  instructors: InstructorAccountSearchResult[] = [];
  students: StudentAccountSearchResult[] = [];
  accountVerificationRequests: AccountVerificationRequestSearchResult[] = [];
  characterLimit = 100;

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
          const hasAccountVerificationRequests = !!resp.accountVerificationRequests?.length;

          if (!hasStudents && !hasInstructors && !hasAccountVerificationRequests) {
            this.statusMessageService.showWarningToast('No results found.');
            this.instructors = [];
            this.students = [];
            this.accountVerificationRequests = [];
            return;
          }

          this.instructors = resp.instructors;
          this.students = resp.students;
          this.accountVerificationRequests = resp.accountVerificationRequests;

          const limit: number = ApiConst.SEARCH_QUERY_SIZE_LIMIT;
          const limitsReached: string[] = [];
          if (this.students.length >= limit) {
            limitsReached.push(`${limit} student results`);
          }
          if (this.instructors.length >= limit) {
            limitsReached.push(`${limit} instructor results`);
          }
          if (this.accountVerificationRequests.length >= limit) {
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
}
