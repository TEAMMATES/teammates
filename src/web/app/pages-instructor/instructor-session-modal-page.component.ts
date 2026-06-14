import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { InstructorSessionBasePageComponent } from './instructor-session-base-page.component';
import { StudentService } from '../../services/student.service';
import { Instructor, Student } from '../../types/api-output';
import { Intent } from '../../types/api-request';
import { ResendResultsLinkToRespondentModalComponent } from '../components/sessions-table/resend-results-link-to-respondent-modal/resend-results-link-to-respondent-modal.component';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';
import { SessionsTableRowModel } from '../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../error-message-output';
import { inject } from '@angular/core';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionModalPageComponent extends InstructorSessionBasePageComponent {
  protected studentService = inject(StudentService);

  isSendReminderLoading = false;

  /**
   * Sends e-mails to remind respondents on the published results link.
   */
  resendResultsLinkToRespondentsEventHandler(model: SessionsTableRowModel): void {
    this.isSendReminderLoading = true;
    const courseId = model.feedbackSession.courseId;
    const feedbackSessionName = model.feedbackSession.feedbackSessionName;
    const feedbackSessionId = model.feedbackSession.feedbackSessionId;

    forkJoin([
      this.studentService.getStudentsFromCourse({ courseId }),
      this.instructorService.loadInstructors({ courseId, intent: Intent.FULL_DETAIL }),
    ])
      .pipe(
        finalize(() => {
          this.isSendReminderLoading = false;
        }),
      )
      .subscribe({
        next: (result) => {
          const students: Student[] = result[0].students;
          const instructors: Instructor[] = result[1].instructors;

          const modalRef: NgbModalRef = this.ngbModal.open(ResendResultsLinkToRespondentModalComponent);

          modalRef.componentInstance.courseId = courseId;
          modalRef.componentInstance.feedbackSessionName = feedbackSessionName;
          modalRef.componentInstance.studentListInfoTableRowModels = students.map(
            (student: Student) =>
              ({
                id: student.userId,
                email: student.email,
                name: student.name,
                teamName: student.teamName,
                sectionName: student.sectionName,

                hasSubmittedSession: false,

                isSelected: false,
              }) satisfies StudentListInfoTableRowModel,
          );
          modalRef.componentInstance.instructorListInfoTableRowModels = instructors.map(
            (instructor: Instructor) =>
              ({
                id: instructor.userId,
                email: instructor.email,
                name: instructor.name,

                hasSubmittedSession: false,

                isSelected: false,
              }) satisfies InstructorListInfoTableRowModel,
          );

          modalRef.result.then(
            (respondentsToRemind: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[]) => {
              this.isSendReminderLoading = true;
              this.feedbackSessionsService
                .remindResultsLinkToRespondents(feedbackSessionId, {
                  usersToRemind: respondentsToRemind.map((m) => m.id),
                  isSendingCopyToInstructor: true,
                })
                .pipe(
                  finalize(() => {
                    this.isSendReminderLoading = false;
                  }),
                )
                .subscribe({
                  next: () => {
                    this.statusMessageService.showSuccessToast(
                      'Session published notification emails have been resent to those students and instructors. ' +
                        'Please allow up to 1 hour for all the notification emails to be sent out.',
                    );
                  },
                  error: (resp: ErrorMessageOutput) => {
                    this.statusMessageService.showErrorToast(resp.error.message);
                  },
                });
            },
            () => {},
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
