import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { LoadingBarService } from '../../../services/loading-bar.service';
import { InstructorSearchResult, SearchService } from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListSectionData } from '../student-list/student-list-section-data';
import { SearchCommentsTable } from './comment-result-table/comment-result-table.component';
import { SearchParams } from './instructor-search-bar/instructor-search-bar.component';

/**
 * Data object for communication with the child student result component
 */
export interface SearchStudentsTable {
  courseId: string;
  sections: StudentListSectionData[];
}

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
  studentTables: SearchStudentsTable[] = [];
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
    this.loadingBarService.showLoadingBar();
    forkJoin(
        this.searchParams.isSearchForComments
            ? this.searchService.searchComment(this.searchParams.searchKey)
            : of([]),
        this.searchParams.isSearchForStudents
            ? this.searchService.searchInstructor(this.searchParams.searchKey)
            : of([]))
        .pipe(finalize(() => this.loadingBarService.hideLoadingBar()))
        .subscribe((resp: [InstructorSearchResult, InstructorSearchResult]) => {
          this.commentTables = resp[0].searchCommentsTables;
          this.studentTables = resp[1].searchStudentsTables;
          const hasStudents: boolean = !!(
              this.studentTables && this.studentTables.length
          );
          const hasComments: boolean = !!(
              this.commentTables && this.commentTables.length
          );
          if (!hasStudents && !hasComments) {
            this.statusMessageService.showWarningMessage('No results found.');
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }
}
