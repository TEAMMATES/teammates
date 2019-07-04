import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { FeedbackQuestionsService } from '../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../services/feedback-sessions.service';
import { HttpRequestService } from '../../services/http-request.service';
import { NavigationService } from '../../services/navigation.service';
import { StatusMessageService } from '../../services/status-message.service';
import { StudentService } from '../../services/student.service';
import {
  FeedbackSessionSubmittedGiverSet,
  Student, Students,
} from '../../types/api-output';
import {
  ResendResultsLinkToStudentModalComponent,
} from "../components/sessions-table/resend-results-link-to-student-modal/resend-results-link-to-student-modal.component"; // tslint:disable-line
import {
  SendRemindersToStudentModalComponent,
} from '../components/sessions-table/send-reminders-to-student-modal/send-reminders-to-student-modal.component';
import {
  SessionsTableRowModel,
} from '../components/sessions-table/sessions-table-model';
import {
  StudentListInfoTableRowModel,
} from '../components/sessions-table/student-list-info-table/student-list-info-table-model';
import { ErrorMessageOutput } from '../error-message-output';
import {
  InstructorSessionBasePageComponent,
} from './instructor-session-base-page.component';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionModalPageComponent extends InstructorSessionBasePageComponent {

  protected constructor(router: Router,
                        httpRequestService: HttpRequestService,
                        statusMessageService: StatusMessageService,
                        navigationService: NavigationService,
                        feedbackSessionsService: FeedbackSessionsService,
                        feedbackQuestionsService: FeedbackQuestionsService,
                        protected modalService: NgbModal,
                        protected studentService: StudentService) {
    super(router, httpRequestService, statusMessageService, navigationService,
        feedbackSessionsService, feedbackQuestionsService);
  }

  /**
   * Sends e-mails to remind students on the published results link.
   */
  resendResultsLinkToStudentsEventHandler(model: SessionsTableRowModel): void {
    const courseId: string = model.feedbackSession.courseId;
    const feedbackSessionName: string = model.feedbackSession.feedbackSessionName;

    this.studentService.getStudentsFromCourse(courseId).subscribe((students: Students) => {
      const modalRef: NgbModalRef = this.modalService.open(ResendResultsLinkToStudentModalComponent);

      modalRef.componentInstance.courseId = courseId;
      modalRef.componentInstance.feedbackSessionName = feedbackSessionName;
      modalRef.componentInstance.studentListInfoTableRowModels = students.students.map((student: Student) => ({
        email: student.email,
        name: student.name,
        teamName: student.teamName,
        sectionName: student.sectionName,

        hasSubmittedSession: false,

        isSelected: false,
      } as StudentListInfoTableRowModel));

      modalRef.result.then((studentsToRemind: StudentListInfoTableRowModel[]) => {
        this.feedbackSessionsService.remindResultsLinkToStudents(courseId, feedbackSessionName, {
          usersToRemind: studentsToRemind.map((m: StudentListInfoTableRowModel) => m.email),
        }).subscribe(() => {
          this.statusMessageService.showSuccessMessage(
              'Session published notification emails have been resent to those students and instructors. '
              + 'Please allow up to 1 hour for all the notification emails to be sent out.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
      }, () => {});
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Sends e-mails to remind students who have not submitted their feedback.
   */
  sendRemindersToStudentsEventHandler(model: SessionsTableRowModel): void {
    const courseId: string = model.feedbackSession.courseId;
    const feedbackSessionName: string = model.feedbackSession.feedbackSessionName;

    forkJoin(
        this.studentService.getStudentsFromCourse(courseId),
        this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet(courseId, feedbackSessionName))
        .subscribe(
            (result: any[]) => {
              const students: Student[] = (result[0] as Students).students;
              const giverSet: Set<string> = new Set((result[1] as FeedbackSessionSubmittedGiverSet).giverIdentifiers);

              const modalRef: NgbModalRef = this.modalService.open(SendRemindersToStudentModalComponent);

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

              modalRef.result.then((studentsToRemind: StudentListInfoTableRowModel[]) => {
                this.feedbackSessionsService.remindFeedbackSessionSubmissionForStudent(courseId, feedbackSessionName, {
                  usersToRemind: studentsToRemind.map((m: StudentListInfoTableRowModel) => m.email),
                }).subscribe(() => {
                  this.statusMessageService.showSuccessMessage(
                      'Reminder e-mails have been sent out to those students and instructors. '
                      + 'Please allow up to 1 hour for all the notification emails to be sent out.');
                }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
              }, () => {});

            }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }
}
