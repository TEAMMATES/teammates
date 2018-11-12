import { Component } from '@angular/core';
import { HttpRequestService } from '../../../services/http-request.service';

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
  instructors: any[] = [];
  students: any[] = [];

  constructor(private httpRequestService: HttpRequestService) {}

  /**
   * Searches for students and instructors matching the search query.
   */
  search(): void {
    const paramMap: { [key: string]: string } = {
      searchkey: this.searchQuery,
    };
    this.httpRequestService.get('/accounts', paramMap).subscribe((resp: any) => {
      this.instructors = resp.instructors;
      for (const instructor of this.instructors) {
        instructor.showLinks = false;
      }

      this.students = resp.students;
      for (const student of this.students) {
        student.showLinks = false;
      }
    }, (resp: any) => {
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
