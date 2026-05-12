import { KeyValuePipe, NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { NgbCollapse, NgbModalRef, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../../services/account.service';
import { EmailGenerationService } from '../../../../services/email-generation.service';
import { FeedbackSessionsGroup, StudentAccountSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { StudentService } from '../../../../services/student.service';
import { Email, RegenerateKey } from '../../../../types/api-output';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../../error-message-output';
import { SearchTermsHighlighterPipe } from '../../../pipes/search-terms-highlighter.pipe';

@Component({
  selector: 'tm-admin-student-search-table',
  templateUrl: './admin-student-search-table.component.html',
  imports: [NgClass, NgbTooltip, AjaxLoadingComponent, KeyValuePipe, SearchTermsHighlighterPipe, NgbCollapse],
})
export class AdminStudentSearchTableComponent implements OnChanges {
  private statusMessageService = inject(StatusMessageService);
  private simpleModalService = inject(SimpleModalService);
  private accountService = inject(AccountService);
  private studentService = inject(StudentService);
  private emailGenerationService = inject(EmailGenerationService);

  @Input()
  students: StudentAccountSearchResult[] = [];

  @Input()
  searchString = '';

  @Output()
  studentReset = new EventEmitter<void>();

  isRegeneratingStudentKeys: boolean[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['students']) {
      this.isRegeneratingStudentKeys = new Array(this.students.length).fill(false);
    }
  }

  showAllStudentsLinks(): void {
    for (const student of this.students) {
      student.showLinks = true;
    }
  }

  hideAllStudentsLinks(): void {
    for (const student of this.students) {
      student.showLinks = false;
    }
  }

  resetStudentGoogleId(student: StudentAccountSearchResult, event: any): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    const modalContent = `Are you sure you want to reset the Google account ID currently associated for
        <strong>${student.name}</strong> in the course <strong>${student.courseId}</strong>?
        The user will need to re-associate their account with a new Google ID.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Reset <strong>${student.name}</strong>'s Google ID?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.accountService.resetStudentAccount(student.courseId, student.email).subscribe({
          next: () => {
            student.googleId = '';
            this.studentReset.emit();
            this.statusMessageService.showSuccessToast("The student's Google ID has been reset.");
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  regenerateStudentKey(student: StudentAccountSearchResult, index: number): void {
    this.isRegeneratingStudentKeys[index] = true;
    const modalContent = `Are you sure you want to regenerate the registration key for
        <strong>${student.name}</strong> for the course <strong>${student.courseId}</strong>?
        An email will be sent to the student with all the new course registration and feedback session links.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Regenerate <strong>${student.name}</strong>'s course links?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.dismissed.subscribe(() => {
      this.isRegeneratingStudentKeys[index] = false;
    });

    modalRef.result.then(
      () => {
        this.studentService.regenerateStudentKey(student.courseId, student.email).subscribe({
          next: (resp: RegenerateKey) => {
            this.statusMessageService.showSuccessToast(resp.message);
            this.updateDisplayedStudentCourseLinks(student, resp.newRegistrationKey);
            this.isRegeneratingStudentKeys[index] = false;
          },
          error: (response: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(response.error.message);
            this.isRegeneratingStudentKeys[index] = false;
          },
        });
      },
      () => {},
    );
  }

  private updateDisplayedStudentCourseLinks(student: StudentAccountSearchResult, newKey: string): void {
    const updateSessions = (sessions: FeedbackSessionsGroup): void => {
      Object.keys(sessions).forEach((key: string): void => {
        sessions[key].feedbackSessionUrl = this.getUpdatedUrl(sessions[key].feedbackSessionUrl, newKey);
      });
    };

    student.courseJoinLink = this.getUpdatedUrl(student.courseJoinLink, newKey);
    updateSessions(student.awaitingSessions);
    updateSessions(student.openSessions);
    updateSessions(student.notOpenSessions);
    updateSessions(student.publishedSessions);
  }

  private getUpdatedUrl(link: string, newVal: string): string {
    const param = 'key';
    const regex = new RegExp(`(${param}=)[^&]+`);

    return link.replace(regex, `$1${newVal}`);
  }

  openCourseJoinEmail(studentId: string): void {
    this.emailGenerationService.getCourseJoinEmail(studentId).subscribe({
      next: (email: Email) => {
        window.location.href = `mailto:${email.recipient}` + `?Subject=${email.subject}` + `&body=${email.content}`;
      },
      error: (err: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(err.error.message);
      },
    });
  }

  openFeedbackSessionReminderEmail(studentId: string, fsId: string): void {
    this.emailGenerationService.getFeedbackSessionReminderEmail(studentId, fsId).subscribe({
      next: (email: Email) => {
        window.location.href = `mailto:${email.recipient}` + `?Subject=${email.subject}` + `&body=${email.content}`;
      },
      error: (err: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(err.error.message);
      },
    });
  }
}
