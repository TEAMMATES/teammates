import { Component } from '@angular/core';
import { HttpRequestService } from '../../../services/http-request.service';
import { MessageOutput } from '../../message-output';

interface CommonBundle {
  name: string;
  email: string;
  googleId: string;
  courseId: string;
  courseName: string;
  institute: string;

  courseJoinLink: string;
  homePageLink: string;
  manageAccountLink: string;

  showLinks: boolean;
}

interface StudentBundle extends CommonBundle {
  section: string;
  team: string;
  comments: string;
  recordsPageLink: string;

  openSessions: { [key: string]: string };
  notOpenSessions: { [key: string]: string };
  publishedSessions: { [key: string]: string };
}

// tslint:disable-next-line:no-empty-interface
interface InstructorBundle extends CommonBundle {}

interface AdminAccountSearchResult {
  students: StudentBundle[];
  instructors: InstructorBundle[];
}

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-search-page',
  templateUrl: './admin-search-page.component.html',
  styleUrls: ['./admin-search-page.component.scss'],
})
export class AdminSearchPageComponent {

  searchQuery: string = '';
  instructors: InstructorBundle[] = [];
  students: StudentBundle[] = [];

  constructor(private httpRequestService: HttpRequestService) {}

  /**
   * Searches for students and instructors matching the search query.
   */
  search(): void {
    const paramMap: { [key: string]: string } = {
      searchkey: this.searchQuery,
    };
    this.httpRequestService.get('/accounts', paramMap).subscribe((resp: AdminAccountSearchResult) => {
      this.instructors = resp.instructors;
      for (const instructor of this.instructors) {
        instructor.showLinks = false;
      }

      this.students = resp.students;
      for (const student of this.students) {
        student.showLinks = false;
      }
    }, (resp: MessageOutput) => {
      // TODO handle error
      console.error(resp);
    });
  }

  /**
   * Shows all instructors' links in the page.
   */
  showAllInstructorsLinks(): void {
    for (const instructor of this.instructors) {
      instructor.showLinks = true;
    }
  }

  /**
   * Hides all instructors' links in the page.
   */
  hideAllInstructorsLinks(): void {
    for (const instructor of this.instructors) {
      instructor.showLinks = false;
    }
  }

  /**
   * Shows all students' links in the page.
   */
  showAllStudentsLinks(): void {
    for (const student of this.students) {
      student.showLinks = true;
    }
  }

  /**
   * Hides all students' links in the page.
   */
  hideAllStudentsLinks(): void {
    for (const student of this.students) {
      student.showLinks = false;
    }
  }

}
