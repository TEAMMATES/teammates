import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { FeedbackQuestionsService } from '../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../services/feedback-sessions.service';
import { InstructorService } from '../../services/instructor.service';
import { NavigationService } from '../../services/navigation.service';
import { StatusMessageService } from '../../services/status-message.service';
import { StudentService } from '../../services/student.service';
import { TableComparatorService } from '../../services/table-comparator.service';
import {
  FeedbackSessionSubmittedGiverSet, Instructor, Instructors,
  Student, Students,
} from '../../types/api-output';
import { Intent } from '../../types/api-request';
import {
  ResendResultsLinkToRespondentModalComponent,
} from "../components/sessions-table/resend-results-link-to-respondent-modal/resend-results-link-to-respondent-modal.component"; // tslint:disable-line
import {
    InstructorListInfoTableRowModel,
    StudentListInfoTableRowModel,
} from '../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';
import {
  SendRemindersToRespondentsModalComponent,
} from '../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-modal.component';
import {
  SessionsTableRowModel,
} from '../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../error-message-output';
import {
  InstructorSessionBasePageComponent,
} from './instructor-session-base-page.component';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionModalPageComponent extends InstructorSessionBasePageComponent {

  isSendReminderLoading: boolean = false;

  protected constructor(router: Router,
                        instructorService: InstructorService,
                        statusMessageService: StatusMessageService,
                        navigationService: NavigationService,
                        feedbackSessionsService: FeedbackSessionsService,
                        feedbackQuestionsService: FeedbackQuestionsService,
                        tableComparatorService: TableComparatorService,
                        ngbModal: NgbModal,
                        protected studentService: StudentService) {
    super(router, instructorService, statusMessageService, navigationService,
        feedbackSessionsService, feedbackQuestionsService, tableComparatorService, ngbModal);
  }

  /**
   * Sends e-mails to remind respondents on the published results link.
   */
  resendResultsLinkToRespondentsEventHandler(model: SessionsTableRowModel): void {
    this.isSendReminderLoading = true;
    const courseId: string = model.feedbackSession.courseId;
    const feedbackSessionName: string = model.feedbackSession.feedbackSessionName;

    forkJoin([
      this.studentService.getStudentsFromCourse({ courseId }),
      this.instructorService.loadInstructors({ courseId, intent: Intent.FULL_DETAIL }),
    ]).pipe(finalize(() => this.isSendReminderLoading = false))
      .subscribe((result: any[]) => {
        const students: Student[] = (result[0] as Students).students;
        const instructors: Instructor[] = (result[1] as Instructors).instructors;

        const modalRef: NgbModalRef = this.ngbModal.open(ResendResultsLinkToRespondentModalComponent);

        modalRef.componentInstance.courseId = courseId;
        modalRef.componentInstance.feedbackSessionName = feedbackSessionName;
        modalRef.componentInstance.studentListInfoTableRowModels = students.map((student: Student) => ({
          email: student.email,
          name: student.name,
          teamName: student.teamName,
          sectionName: student.sectionName,

          hasSubmittedSession: false,

          isSelected: false,
        } as StudentListInfoTableRowModel));
        modalRef.componentInstance.instructorListInfoTableRowModels =
            instructors.map((instructor: Instructor) => ({
              email: instructor.email,
              name: instructor.name,

              hasSubmittedSession: false,

              isSelected: false,
            } as InstructorListInfoTableRowModel));

        modalRef.result.then((respondentsToRemind: any[]) => {
          this.isSendReminderLoading = true;
          this.feedbackSessionsService.remindResultsLinkToRespondents(courseId, feedbackSessionName, {
            usersToRemind: respondentsToRemind.map((m: any) => m.email),
          }).pipe(finalize(() => this.isSendReminderLoading = false))
            .subscribe(() => {
              this.statusMessageService.showSuccessToast(
                  'Session published notification emails have been resent to those students and instructors. '
                  + 'Please allow up to 1 hour for all the notification emails to be sent out.');
            }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
        }, () => {});
      }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }

  /**
   * Sends e-mails to remind respondents who have not submitted their feedback.
   */
  sendRemindersToRespondentsEventHandler(model: SessionsTableRowModel): void {
    this.isSendReminderLoading = true;
    const courseId: string = model.feedbackSession.courseId;
    const feedbackSessionName: string = model.feedbackSession.feedbackSessionName;

    forkJoin([
      this.studentService.getStudentsFromCourse({ courseId }),
      this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({ courseId, feedbackSessionName }),
      this.instructorService.loadInstructors({ courseId, intent: Intent.FULL_DETAIL }),
    ]).pipe(finalize(() => this.isSendReminderLoading = false))
      .subscribe((result: any[]) => {
        const students: Student[] = (result[0] as Students).students;
        const giverSet: Set<string> = new Set((result[1] as FeedbackSessionSubmittedGiverSet).giverIdentifiers);
        const instructors: Instructor[] = (result[2] as Instructors).instructors;

        const modalRef: NgbModalRef = this.ngbModal.open(SendRemindersToRespondentsModalComponent);

        modalRef.componentInstance.courseId = courseId;
        modalRef.componentInstance.feedbackSessionName = feedbackSessionName;
        modalRef.componentInstance.studentListInfoTableRowModels = students.map((student: Student) => ({
          email: student.email,
          name: student.name,
          teamName: student.teamName,
          sectionName: student.sectionName,

          hasSubmittedSession: giverSet.has(student.email),

          isSelected: false,
        } as StudentListInfoTableRowModel));
        modalRef.componentInstance.instructorListInfoTableRowModels = instructors.map(
            (instructor: Instructor) => ({
              email: instructor.email,
              name: instructor.name,

              hasSubmittedSession: giverSet.has(instructor.email),

              isSelected: false,
            } as InstructorListInfoTableRowModel));

        modalRef.result.then((respondentsToRemind: any[]) => {
          this.isSendReminderLoading = true;
          this.feedbackSessionsService.remindFeedbackSessionSubmissionForRespondents(courseId, feedbackSessionName, {
            usersToRemind: respondentsToRemind.map((m: any) => m.email),
          }).pipe(finalize(() => this.isSendReminderLoading = false))
            .subscribe(() => {
              this.statusMessageService.showSuccessToast(
                  'Reminder e-mails have been sent out to those students and instructors. '
                  + 'Please allow up to 1 hour for all the notification emails to be sent out.');
            }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
        }, () => {});
      }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }
}
