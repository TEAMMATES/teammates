import { Component } from '@angular/core';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AdminSearchResult, InstructorAccountSearchResult,
  StudentAccountSearchResult } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

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
  instructors: InstructorAccountSearchResult[] = [];
  students: StudentAccountSearchResult[] = [];

  constructor(private statusMessageService: StatusMessageService, private accountService: AccountService) {}

  /**
   * Searches for students and instructors matching the search query.
   */
  search(): void {
    this.accountService.searchAccounts(this.searchQuery).subscribe((resp: AdminSearchResult) => {
      const hasStudents: boolean = !!(resp.students && resp.students.length);
      const hasInstructors: boolean = !!(resp.instructors && resp.instructors.length);

      if (!hasStudents && !hasInstructors) {
        this.statusMessageService.showWarningMessage('No results found.');
      } else {
        this.instructors = resp.instructors;
        this.students = resp.students;
        this.hideAllInstructorsLinks();
        this.hideAllStudentsLinks();
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
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

  /**
   * Resets the instructor's Google ID.
   */
  resetInstructorGoogleId(instructor: InstructorAccountSearchResult, event: any): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    this.accountService.resetInstructorAccount(instructor.courseId, instructor.email).subscribe(() => {
      this.search();
      this.statusMessageService.showSuccessMessage('The instructor\'s Google ID has been reset.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Resets the student's Google ID.
   */
  resetStudentGoogleId(student: StudentAccountSearchResult, event: any): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.accountService.resetStudentAccount(student.courseId, student.email).subscribe(() => {
      student.googleId = '';
      this.statusMessageService.showSuccessMessage('The student\'s Google ID has been reset.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
