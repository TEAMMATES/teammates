import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { InstructorSessionBasePageComponent } from './instructor-session-base-page.component';
import { FeedbackQuestionsService } from '../../services/feedback-questions.service';
import { FeedbackSessionActionsService } from '../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../services/feedback-sessions.service';
import { InstructorService } from '../../services/instructor.service';
import { NavigationService } from '../../services/navigation.service';
import { ProgressBarService } from '../../services/progress-bar.service';
import { SimpleModalService } from '../../services/simple-modal.service';
import { StatusMessageService } from '../../services/status-message.service';
import { StudentService } from '../../services/student.service';
import { TableComparatorService } from '../../services/table-comparator.service';
import { TimezoneService } from '../../services/timezone.service';
import { FeedbackSessionSubmittedGiverSet, Instructor, Instructors, Student, Students } from '../../types/api-output';
import { Intent } from '../../types/api-request';
import { ResendResultsLinkToRespondentModalComponent } from '../components/sessions-table/resend-results-link-to-respondent-modal/resend-results-link-to-respondent-modal.component';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';
import { SendRemindersToRespondentsModalComponent } from '../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-modal.component';
import { ReminderResponseModel } from '../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-model';
import { SessionsTableRowModel } from '../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionModalPageComponent extends InstructorSessionBasePageComponent {
  isSendReminderLoading = false;

  protected constructor(
    instructorService: InstructorService,
    statusMessageService: StatusMessageService,
    navigationService: NavigationService,
    feedbackSessionsService: FeedbackSessionsService,
    feedbackQuestionsService: FeedbackQuestionsService,
    tableComparatorService: TableComparatorService,
    ngbModal: NgbModal,
    simpleModalService: SimpleModalService,
    progressBarService: ProgressBarService,
    feedbackSessionActionsService: FeedbackSessionActionsService,
    timezoneService: TimezoneService,
    protected studentService: StudentService,
  ) {
    super(
      instructorService,
      statusMessageService,
      navigationService,
      feedbackSessionsService,
      feedbackQuestionsService,
      tableComparatorService,
      ngbModal,
      simpleModalService,
      progressBarService,
      feedbackSessionActionsService,
      timezoneService,
    );
  }

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
        next: (result: any[]) => {
          const students: Student[] = (result[0] as Students).students;
          const instructors: Instructor[] = (result[1] as Instructors).instructors;

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

  /**
   * Sends e-mails to remind respondents who have not submitted their feedback.
   */
  sendRemindersToRespondentsEventHandler(model: SessionsTableRowModel, selectAllRespondents: boolean): void {
    this.isSendReminderLoading = true;
    const courseId = model.feedbackSession.courseId;
    const feedbackSessionName = model.feedbackSession.feedbackSessionName;
    const feedbackSessionId = model.feedbackSession.feedbackSessionId;

    forkJoin([
      this.studentService.getStudentsFromCourse({ courseId }),
      this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({ feedbackSessionId }),
      this.instructorService.loadInstructors({ courseId, intent: Intent.FULL_DETAIL }),
    ])
      .pipe(
        finalize(() => {
          this.isSendReminderLoading = false;
        }),
      )
      .subscribe({
        next: (result: any[]) => {
          const students: Student[] = (result[0] as Students).students;
          const giverSet: Set<string> = new Set((result[1] as FeedbackSessionSubmittedGiverSet).giverIdentifiers);
          const instructors: Instructor[] = (result[2] as Instructors).instructors;

          const modalRef: NgbModalRef = this.ngbModal.open(SendRemindersToRespondentsModalComponent);

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

                hasSubmittedSession: giverSet.has(student.email),

                isSelected: selectAllRespondents && !giverSet.has(student.email),
              }) satisfies StudentListInfoTableRowModel,
          );
          modalRef.componentInstance.instructorListInfoTableRowModels = instructors.map(
            (instructor: Instructor) =>
              ({
                id: instructor.userId,
                email: instructor.email,
                name: instructor.name,

                hasSubmittedSession: giverSet.has(instructor.email),

                isSelected: selectAllRespondents && !giverSet.has(instructor.email),
              }) satisfies InstructorListInfoTableRowModel,
          );

          modalRef.result.then(
            (reminderResponse: ReminderResponseModel) => {
              this.isSendReminderLoading = true;
              this.feedbackSessionsService
                .remindFeedbackSessionSubmissionForRespondents(feedbackSessionId, {
                  usersToRemind: reminderResponse.respondentsToSend.map((m) => m.id),
                  isSendingCopyToInstructor: reminderResponse.isSendingCopyToInstructor,
                })
                .pipe(
                  finalize(() => {
                    this.isSendReminderLoading = false;
                  }),
                )
                .subscribe({
                  next: () => {
                    this.statusMessageService.showSuccessToast(
                      'Reminder e-mails have been sent out to those students and instructors. ' +
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
