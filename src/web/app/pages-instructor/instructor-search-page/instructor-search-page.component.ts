import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { LoadingBarService } from '../../../services/loading-bar.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListSectionData } from '../student-list/student-list-section-data';

/**
 * Search result object from student search query.
 */
export interface SearchResult {
  searchFeedbackSessionDataTables: SearchFeedbackSessionDataTable[];
  searchStudentsTables: SearchStudentsTable[];
}

interface SearchFeedbackSessionDataTable {
  something: any;
}

/**
 * Data object for communication with the child student result component
 */
export interface SearchStudentsTable {
  courseId: string;
  sections: StudentListSectionData[];
}

/**
 * Data object for communciation with the child search bar component
 */
export interface SearchQuery {
  searchKey: string;
  searchStudents: boolean;
  searchFeedbackSessionData: boolean;
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

  searchKey: string = '';
  studentTables: SearchStudentsTable[] = [];
  fbSessionDataTables: SearchFeedbackSessionDataTable[] = [];

  constructor(private route: ActivatedRoute,
              private studentService: StudentService,
              private statusMessageService: StatusMessageService,
              private loadingBarService: LoadingBarService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.studentSearchkey) {
        this.searchKey = queryParams.studentSearchkey;
      }
      if (this.searchKey) {
        this.search({
          searchKey: this.searchKey,
          searchStudents: true,
          searchFeedbackSessionData: false,
        });
      }
    });
  }

  /**
   * Searches for students and questions/responses/comments matching the search query.
   */
  search(searchQuery: SearchQuery): void {
    this.loadingBarService.showLoadingBar();
    this.studentService.searchForStudents({
      searchKey: searchQuery.searchKey,
      searchStudents: searchQuery.searchStudents.toString(),
      searchFeedbackSessionData: searchQuery.searchFeedbackSessionData.toString(),
    })
        .pipe(finalize(() => this.loadingBarService.hideLoadingBar()))
        .subscribe((resp: SearchResult) => {
          this.studentTables = resp.searchStudentsTables;
          this.fbSessionDataTables = resp.searchFeedbackSessionDataTables;
          const hasStudents: boolean = !!(this.studentTables && this.studentTables.length);
          const hasFbSessionData: boolean = !!(this.fbSessionDataTables && this.fbSessionDataTables.length);
          if (!hasStudents && !hasFbSessionData) {
            this.statusMessageService.showWarningMessage('No results found.');
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

}
