import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { LoadingBarService } from '../../../services/loading-bar.service';
import { InstructorSearchResult, SearchService } from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { SearchCommentsTable } from './comment-result-table/comment-result-table.component';
import { SearchParams } from './instructor-search-bar/instructor-search-bar.component';
import { SearchStudentsListRowTable } from './student-result-table/student-result-table.component';

/**
 * Instructor search page.
 */
@Component({
  selector: 'tm-instructor-search-page',
  templateUrl: './instructor-search-page.component.html',
  styleUrls: ['./instructor-search-page.component.scss'],
})
export class InstructorSearchPageComponent implements OnInit {

  searchParams: SearchParams = {
    searchKey: '',
    isSearchForStudents: true,
    isSearchForComments: false,
  };
  studentsListRowTables: SearchStudentsListRowTable[] = [];
  commentTables: SearchCommentsTable[] = [];

  constructor(
    private route: ActivatedRoute,
    private statusMessageService: StatusMessageService,
    private loadingBarService: LoadingBarService,
    private searchService: SearchService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.studentSearchkey) {
        this.searchParams.searchKey = queryParams.studentSearchkey;
      }
      if (this.searchParams.searchKey) {
        this.search();
      }
    });
  }

  /**
   * Searches for students and questions/responses/comments matching the search query.
   */
  search(): void {
    if (!(this.searchParams.isSearchForComments || this.searchParams.isSearchForStudents)
        || this.searchParams.searchKey === '') {
      return;
    }
    this.loadingBarService.showLoadingBar();
    forkJoin([
      this.searchParams.isSearchForComments
          ? this.searchService.searchComment(this.searchParams.searchKey)
          : of({}) as Observable<InstructorSearchResult>,
      this.searchParams.isSearchForStudents
          ? this.searchService.searchInstructor(this.searchParams.searchKey)
          : of({}) as Observable<InstructorSearchResult>,
    ]).pipe(
        finalize(() => this.loadingBarService.hideLoadingBar()),
    ).subscribe((resp: InstructorSearchResult[]) => {
      this.commentTables = resp[0].searchCommentsTables;
      const searchStudentsTable: SearchStudentsListRowTable[] = resp[1].searchStudentsTables;
      const hasStudents: boolean = !!(
          searchStudentsTable && searchStudentsTable.length
      );
      const hasComments: boolean = !!(
          this.commentTables && this.commentTables.length
      );

      if (hasStudents) {
        this.studentsListRowTables = searchStudentsTable;
      }
      if (!hasStudents && !hasComments) {
        this.statusMessageService.showWarningToast('No results found.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }
}
