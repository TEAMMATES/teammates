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
  FeedbackQuestion, FeedbackQuestions,
  FeedbackSessionSubmittedGiverSet, Instructor, Instructors,
  Student, Students,
} from '../../types/api-output';
import {
  ResendResultsLinkToStudentModalComponent,
} from "../components/sessions-table/resend-results-link-to-student-modal/resend-results-link-to-student-modal.component"; // tslint:disable-line
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../components/sessions-table/respondent-list-info-table/student-list-info-table-model';
import {
  SendRemindersToStudentModalComponent,
} from '../components/sessions-table/send-reminders-to-student-modal/send-reminders-to-student-modal.component';
import {
  SessionsTableRowModel,
} from '../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../error-message-output';
import { Intent } from '../Intent';
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
    const paramsMapInstructors: { [key: string]: string } = {
      courseid: courseId,
      intent: Intent.FULL_DETAIL,
    };
    const paramsMapQuestions: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
      intent: Intent.FULL_DETAIL,
    };

    forkJoin(
        this.studentService.getStudentsFromCourse(courseId),
        this.httpRequestService.get('/instructors', paramsMapInstructors),
        this.httpRequestService.get('/questions', paramsMapQuestions))
        .subscribe(
            (result: any[]) => {
              let students: Student[] = [];
              let instructors: Instructor[] = [];
              const questions: FeedbackQuestion[] = (result[2] as FeedbackQuestions).questions;

              // check whether there are questions for students
              if (this.feedbackQuestionsService.hasQuestionsForStudent(questions)) {
                students = (result[0] as Students).students;
              }

              // check whether there are questions for instructors
              if (this.feedbackQuestionsService.hasQuestionsForInstructor(questions)) {
                instructors = (result[1] as Instructors).instructors;
              }

              const modalRef: NgbModalRef = this.modalService.open(ResendResultsLinkToStudentModalComponent);

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
                this.feedbackSessionsService.remindResultsLinkToRespondents(courseId, feedbackSessionName, {
                  usersToRemind: respondentsToRemind.map((m: any) => m.email),
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
    const paramsMapInstructors: { [key: string]: string } = {
      courseid: courseId,
      intent: Intent.FULL_DETAIL,
    };
    const paramsMapQuestions: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
      intent: Intent.FULL_DETAIL,
    };

    forkJoin(
        this.studentService.getStudentsFromCourse(courseId),
        this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet(courseId, feedbackSessionName),
        this.httpRequestService.get('/instructors', paramsMapInstructors),
        this.httpRequestService.get('/questions', paramsMapQuestions))
        .subscribe(
            (result: any[]) => {
              let students: Student[] = [];
              const giverSet: Set<string> = new Set((result[1] as FeedbackSessionSubmittedGiverSet).giverIdentifiers);
              let instructors: Instructor[] = [];
              const questions: FeedbackQuestion[] = (result[3] as FeedbackQuestions).questions;

              // check whether there are questions for students
              if (this.feedbackQuestionsService.hasQuestionsForStudent(questions)) {
                students = (result[0] as Students).students;
              }

              // check whether there are questions for instructors
              if (this.feedbackQuestionsService.hasQuestionsForInstructor(questions)) {
                instructors = (result[2] as Instructors).instructors;
              }

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
              modalRef.componentInstance.instructorListInfoTableRowModels = instructors.map(
                  (instructor: Instructor) => ({
                    email: instructor.email,
                    name: instructor.name,

                    hasSubmittedSession: giverSet.has(instructor.email),

                    isSelected: false,
                  } as InstructorListInfoTableRowModel));

              modalRef.result.then((respondentsToRemind: any[]) => {
                this.feedbackSessionsService.remindFeedbackSessionSubmissionForRespondents(courseId,
                    feedbackSessionName,
                  { usersToRemind: respondentsToRemind.map((m: any) => m.email),
                  }).subscribe(() => {
                    this.statusMessageService.showSuccessMessage(
                      'Reminder e-mails have been sent out to those students and instructors. '
                      + 'Please allow up to 1 hour for all the notification emails to be sent out.');
                  }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
              }, () => {});

            }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }
}
