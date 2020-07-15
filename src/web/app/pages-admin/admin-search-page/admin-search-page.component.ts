import { Component } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { EmailGenerationService } from '../../../services/email-generation.service';
import { LoadingBarService } from '../../../services/loading-bar.service';
import {
  AdminSearchResult,
  FeedbackSessionsGroup,
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Email, RegenerateStudentCourseLinks } from '../../../types/api-output';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import { RegenerateLinksConfirmModalComponent } from './regenerate-links-confirm-modal/regenerate-links-confirm-modal.component';
import { ResetGoogleIdConfirmModalComponent } from './reset-google-id-confirm-modal/reset-google-id-confirm-modal.component';

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-search-page',
  templateUrl: './admin-search-page.component.html',
  styleUrls: ['./admin-search-page.component.scss'],
  animations: [collapseAnim],
})
export class AdminSearchPageComponent {

  searchQuery: string = '';
  instructors: InstructorAccountSearchResult[] = [];
  students: StudentAccountSearchResult[] = [];

  constructor(
    private statusMessageService: StatusMessageService,
    private modalService: NgbModal,
    private accountService: AccountService,
    private studentService: StudentService,
    private searchService: SearchService,
    private emailGenerationService: EmailGenerationService,
    private loadingBarService: LoadingBarService,
  ) {}

  /**
   * Searches for students and instructors matching the search query.
   */
  search(): void {
    this.loadingBarService.showLoadingBar();
    this.searchService.searchAdmin(
        this.searchQuery,
    ).pipe(finalize(() => this.loadingBarService.hideLoadingBar())).subscribe((resp: AdminSearchResult) => {
      const hasStudents: boolean = !!(resp.students && resp.students.length);
      const hasInstructors: boolean = !!(resp.instructors && resp.instructors.length);

      if (!hasStudents && !hasInstructors) {
        this.statusMessageService.showWarningToast('No results found.');
        this.instructors = [];
        this.students = [];
      } else {
        this.instructors = resp.instructors;
        this.students = resp.students;
        this.hideAllInstructorsLinks();
        this.hideAllStudentsLinks();
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
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
    const modalRef: NgbModalRef = this.modalService.open(ResetGoogleIdConfirmModalComponent);
    modalRef.componentInstance.name = instructor.name;
    modalRef.componentInstance.course = instructor.courseId;

    modalRef.result.then(() => {
      this.accountService.resetInstructorAccount(instructor.courseId, instructor.email).subscribe(() => {
        this.search();
        this.statusMessageService.showSuccessToast('The instructor\'s Google ID has been reset.');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Resets the student's Google ID.
   */
  resetStudentGoogleId(student: StudentAccountSearchResult, event: any): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    const modalRef: NgbModalRef = this.modalService.open(ResetGoogleIdConfirmModalComponent);
    modalRef.componentInstance.name = student.name;
    modalRef.componentInstance.course = student.courseId;

    modalRef.result.then(() => {
      this.accountService.resetStudentAccount(student.courseId, student.email).subscribe(() => {
        student.googleId = '';
        this.statusMessageService.showSuccessToast('The student\'s Google ID has been reset.');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Regenerates the student's course join and feedback session links.
   */
  regenerateFeedbackSessionLinks(student: StudentAccountSearchResult): void {
    const modalRef: NgbModalRef = this.modalService.open(RegenerateLinksConfirmModalComponent);
    modalRef.componentInstance.studentName = student.name;
    modalRef.componentInstance.regenerateLinksCourseId = student.courseId;

    modalRef.result.then(() => {
      this.studentService.regenerateStudentCourseLinks(student.courseId, student.email)
        .subscribe((resp: RegenerateStudentCourseLinks) => {
          this.statusMessageService.showSuccessToast(resp.message);
          this.updateDisplayedStudentCourseLinks(student, resp.newRegistrationKey);
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(response.error.message);
        });
    }, () => {});
  }

  /**
   * Updates the student's displayed course join and feedback session links with the value of the newKey.
   */
  private updateDisplayedStudentCourseLinks(student: StudentAccountSearchResult, newKey: string): void {
    const updateSessions: Function = (sessions: FeedbackSessionsGroup): void => {
      Object.keys(sessions).forEach((key: string): void => {
        sessions[key].feedbackSessionUrl = this.getUpdatedUrl(sessions[key].feedbackSessionUrl, newKey);
      });
    };

    student.courseJoinLink = this.getUpdatedUrl(student.courseJoinLink, newKey);

    updateSessions(student.openSessions);
    updateSessions(student.notOpenSessions);
    updateSessions(student.publishedSessions);
  }

  /**
   * Returns the URL after replacing the value of the `key` parameter with that of the new key.
   */
  private getUpdatedUrl(link: string, newVal: string): string {
    const param: string = 'key';
    const regex: RegExp = new RegExp(`(${param}=)[^\&]+`);

    return link.replace(regex, `$1${newVal}`);
  }

  /**
   * Open up an email populated with content for course join invitation.
   */
  openCourseJoinEmail(courseId: string, studentemail: string): void {
    this.emailGenerationService.getCourseJoinEmail(courseId, studentemail)
        .subscribe((email: Email) => {
          window.location.href = `mailto:${email.recipient}`
              + `?Subject=${email.subject}`
              + `&body=${email.content}`;
        }, (err: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(err.error.message);
        });
  }

  /**
   * Open up an email populated with content for feedback session reminder.
   */
  openFeedbackSessionReminderEmail(courseId: string, studentemail: string, fsname: string): void {
    this.emailGenerationService.getFeedbackSessionReminderEmail(courseId, studentemail, fsname)
        .subscribe((email: Email) => {
          window.location.href = `mailto:${email.recipient}`
              + `?Subject=${email.subject}`
              + `&body=${email.content}`;
        }, (err: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(err.error.message);
        });
  }

}
