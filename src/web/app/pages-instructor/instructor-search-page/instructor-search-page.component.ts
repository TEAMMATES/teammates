import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';

interface SearchResult {
  studentResults: StudentAttributes[][];
  feedbackSessionDataResults: FeedbackSessionDataResultRow[];
}

interface StudentAttributes {
  email: string;
  course: string;
  name: string;
  lastName: string;
  comments: string;
  team: string;
  section: string;
}

interface FeedbackSessionDataResultRow {

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
  students: StudentAttributes[][] = [];
  fbSessionData: FeedbackSessionDataResultRow[] = [];

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

  search(): void {
    const paramMap: { [key: string]: string } = {
      searchkey: this.searchQuery,
      searchstudents: this.searchStudents.toString(),
      searchfeedbacksessiondata: this.searchFeedbackSessionData.toString(),
    };
    this.httpRequestService.get('/studentsAndSessionData/search', paramMap).subscribe((resp: SearchResult) => {
      this.students = resp.studentResults;
      this.fbSessionData = resp.feedbackSessionDataResults;
      let hasStudents = this.students && this.students.length;
      let hasFbSessionData = this.fbSessionData && this.fbSessionData.length;
      if (!hasStudents && !hasFbSessionData) {
        this.statusMessageService.showWarningMessage('No results found.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
