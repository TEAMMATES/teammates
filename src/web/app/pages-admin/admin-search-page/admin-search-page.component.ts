import { Component } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../services/account.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  AdminSearchResult,
  InstructorAccountSearchResult,
  RegenerateStudentCourseLinks,
  StudentAccountSearchResult,
} from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  RegenerateLinksConfirmModalComponent,
} from './regenerate-links-confirm-modal/regenerate-links-confirm-modal.component';

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

  constructor(private statusMessageService: StatusMessageService,
              private accountService: AccountService,
              private httpRequestService: HttpRequestService,
              private modalService: NgbModal) {}

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

  /**
   * Regenerates the student's course join and feedback session links.
   */
  regenerateFeedbackSessionLinks(student: StudentAccountSearchResult): void {
    const modalRef: NgbModalRef = this.modalService.open(RegenerateLinksConfirmModalComponent);
    modalRef.componentInstance.studentName = student.name;
    modalRef.componentInstance.regenerateLinksCourseId = student.courseId;

    modalRef.result.then(() => {
      const paramsMap: { [key: string]: string } = {
        courseid: student.courseId,
        studentemail: student.email,
      };

      this.httpRequestService.post('/regeneratestudentcourselinks', paramsMap)
          .subscribe((resp: RegenerateStudentCourseLinks) => {
            this.statusMessageService.showSuccessMessage(
                `${resp.message} The student's new key is ${resp.newRegistrationKey}`);
            this.updateDisplayedStudentCourseLinks(student, resp.newRegistrationKey);
          }, (response: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(response.error.message);
          });
    }, () => {});
  }

  /**
   * Updates the student's displayed course join and feedback session links with the value of the newKey.
   */
  private updateDisplayedStudentCourseLinks(student: StudentAccountSearchResult, newKey: string): void {
    student.courseJoinLink = this.getUpdatedUrl(student.courseJoinLink, newKey);

    Object.keys(student.openSessions).forEach((key: string): void => {
      student.openSessions[key] = this.getUpdatedUrl(student.openSessions[key], newKey);
    });

    Object.keys(student.notOpenSessions).forEach((key: string): void => {
      student.notOpenSessions[key] = this.getUpdatedUrl(student.notOpenSessions[key], newKey);
    });

    Object.keys(student.publishedSessions).forEach((key: string): void => {
      student.publishedSessions[key] = this.getUpdatedUrl(student.publishedSessions[key], newKey);
    });
  }

  /**
   * Returns the URL after replacing the value of the `key` parameter with that of the new key.
   */
  private getUpdatedUrl(link: string, newKey: string): string {
    const url: URL = new URL(link);
    const searchParams: URLSearchParams = new URLSearchParams(url.search);
    searchParams.set('key', newKey);
    url.search = searchParams.toString();
    return url.toString();
  }

}
