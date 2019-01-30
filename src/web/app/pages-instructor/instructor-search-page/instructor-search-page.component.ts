import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListSectionData } from '../student-list/student-list-section-data';

interface SearchResult {
  searchFeedbackSessionDataTables: SearchFeedbackSessionDataTable[];
  searchStudentsTables: SearchStudentsTable[];
}

interface SearchFeedbackSessionDataTable {
  something: any;
}

interface SearchStudentsTable {
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

  user: string = '';
  searchQuery: string = '';
  searchStudents: boolean = true;
  searchFeedbackSessionData: boolean = false;
  studentTables: SearchStudentsTable[] = [];
  fbSessionDataTables: SearchFeedbackSessionDataTable[] = [];

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.searchQuery = queryParams.studentSearchkey;
      if (this.searchQuery) {
        this.search();
      }
    });
  }

  /**
   * Searches for students and questions/responses/comments matching the search query.
   */
  search(): void {
    const paramMap: { [key: string]: string } = {
      searchkey: this.searchQuery,
      searchstudents: this.searchStudents.toString(),
      searchfeedbacksessiondata: this.searchFeedbackSessionData.toString(),
    };
    this.httpRequestService.get('/studentsAndSessionData/search', paramMap).subscribe((resp: SearchResult) => {
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
