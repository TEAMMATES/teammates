import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Params } from '@angular/router';
import { forkJoin } from 'rxjs';
import { finalize, switchMap } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  CourseView,
  FeedbackSessionSubmittedGiverSet,
  FeedbackSessionView,
  Instructor,
  Instructors,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';
import { RespondentListInfoTableComponent } from '../../components/sessions-table/respondent-list-info-table/respondent-list-info-table.component';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Send submission reminders to respondents page.
 */
@Component({
  selector: 'tm-instructor-session-send-reminders-page',
  templateUrl: './instructor-session-send-reminders-page.component.html',
  styleUrls: ['./instructor-session-send-reminders-page.component.scss'],
  imports: [
    FormsModule,
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    RespondentListInfoTableComponent,
    FormatDateDetailPipe,
  ],
})
export class InstructorSessionSendRemindersPageComponent implements OnInit {
  private statusMessageService = inject(StatusMessageService);
  private feedbackSessionsService = inject(FeedbackSessionsService);
  private studentService = inject(StudentService);
  private instructorService = inject(InstructorService);
  private courseService = inject(CourseService);
  private navigationService = inject(NavigationService);
  private route = inject(ActivatedRoute);

  feedbackSessionId = '';
  courseId = '';
  courseName = '';
  feedbackSessionName = '';
  feedbackSessionTimeZone = 'UTC';
  feedbackSessionEndingTimestamp = 0;

  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];
  instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [];

  isSendingCopyToInstructor = true;
  preselectNonSubmitters = false;

  isLoading = true;
  hasLoadingFailed = false;
  isSendingReminders = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: Params) => {
      this.feedbackSessionId = queryParams['fsid'];
      this.preselectNonSubmitters = queryParams['preselectnonsubmitters'] === 'true';
      this.loadFeedbackSessionAndRespondents();
    });
  }

  /**
   * Loads the feedback session along with the students and instructors to remind.
   */
  loadFeedbackSessionAndRespondents(): void {
    this.isLoading = true;
    this.hasLoadingFailed = false;
    this.feedbackSessionsService
      .getFeedbackSession({
        feedbackSessionId: this.feedbackSessionId,
        intent: Intent.FULL_DETAIL,
      })
      .pipe(
        switchMap((feedbackSessionView: FeedbackSessionView) => {
          const feedbackSession = feedbackSessionView.feedbackSession;
          this.feedbackSessionName = feedbackSession.feedbackSessionName;
          this.courseId = feedbackSession.courseId;
          this.feedbackSessionTimeZone = feedbackSession.timeZone;
          this.feedbackSessionEndingTimestamp = feedbackSession.submissionEndTimestamp;

          return forkJoin({
            course: this.courseService.getCourseAsInstructor(this.courseId),
            students: this.studentService.getStudentsFromCourse({ courseId: this.courseId }),
            instructors: this.instructorService.loadInstructors({
              courseId: this.courseId,
              intent: Intent.FULL_DETAIL,
            }),
            submittedGiverSet: this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
              feedbackSessionId: this.feedbackSessionId,
            }),
          });
        }),
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: ({
          course,
          students,
          instructors,
          submittedGiverSet,
        }: {
          course: CourseView;
          students: Students;
          instructors: Instructors;
          submittedGiverSet: FeedbackSessionSubmittedGiverSet;
        }) => {
          this.courseName = course.course.courseName;
          const studentNonGiverSet: Set<string> = new Set(submittedGiverSet.studentNonGivers);
          const instructorNonGiverSet: Set<string> = new Set(submittedGiverSet.instructorNonGivers);

          this.studentListInfoTableRowModels = students.students.map(
            (student: Student) =>
              ({
                id: student.userId,
                email: student.email,
                name: student.name,
                teamName: student.teamName,
                sectionName: student.sectionName,

                hasSubmittedSession: !studentNonGiverSet.has(student.userId),

                isSelected: this.preselectNonSubmitters && studentNonGiverSet.has(student.userId),
              }) satisfies StudentListInfoTableRowModel,
          );
          this.instructorListInfoTableRowModels = instructors.instructors.map(
            (instructor: Instructor) =>
              ({
                id: instructor.userId,
                email: instructor.email,
                name: instructor.name,

                hasSubmittedSession: !instructorNonGiverSet.has(instructor.userId),

                isSelected: this.preselectNonSubmitters && instructorNonGiverSet.has(instructor.userId),
              }) satisfies InstructorListInfoTableRowModel,
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Sends reminders to the selected students and instructors.
   */
  sendReminders(): void {
    this.isSendingReminders = true;
    this.feedbackSessionsService
      .remindFeedbackSessionSubmissionForRespondents(this.feedbackSessionId, {
        usersToRemind: this.getSelectedRespondents().map((model) => model.id),
        isSendingCopyToInstructor: this.isSendingCopyToInstructor,
      })
      .pipe(
        finalize(() => {
          this.isSendingReminders = false;
        }),
      )
      .subscribe({
        next: () => {
          this.navigationService.navigateBackWithSuccessMessage(
            'Reminder e-mails have been sent out to those students and instructors. ' +
              'Please allow up to 1 hour for all the notification emails to be sent out.',
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Navigates back to the previous page without sending reminders.
   */
  cancel(): void {
    this.navigationService.navigateBack();
  }

  /**
   * Changes selection state for all students.
   */
  changeSelectionStatusForAllStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModels.forEach((model: StudentListInfoTableRowModel) => {
      model.isSelected = shouldSelect;
    });
  }

  /**
   * Changes selection state for all yet to submit students.
   */
  changeSelectionStatusForAllYetSubmittedStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModels.forEach((model: StudentListInfoTableRowModel) => {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    });
  }

  /**
   * Changes selection state for all instructors.
   */
  changeSelectionStatusForAllInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModels.forEach((model: InstructorListInfoTableRowModel) => {
      model.isSelected = shouldSelect;
    });
  }

  /**
   * Changes selection state for all yet to submit instructors.
   */
  changeSelectionStatusForAllYetSubmittedInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModels.forEach((model: InstructorListInfoTableRowModel) => {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    });
  }

  /**
   * Changes selection state for sending a copy to requesting instructor.
   */
  changeSelectionStatusForSendingCopyToInstructorHandler(shouldSendCopy: boolean): void {
    this.isSendingCopyToInstructor = shouldSendCopy;
  }

  /**
   * Collates the list of selected students and instructors to remind.
   */
  private getSelectedRespondents(): (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] {
    const studentsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] =
      this.studentListInfoTableRowModels.filter((model: StudentListInfoTableRowModel) => model.isSelected);
    const instructorsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] =
      this.instructorListInfoTableRowModels.filter((model: InstructorListInfoTableRowModel) => model.isSelected);
    return studentsToSend.concat(instructorsToSend);
  }

  /**
   * Checks whether at least one respondent is selected.
   */
  get hasSelectedRespondents(): boolean {
    return (
      this.studentListInfoTableRowModels.some((model: StudentListInfoTableRowModel) => model.isSelected) ||
      this.instructorListInfoTableRowModels.some((model: InstructorListInfoTableRowModel) => model.isSelected)
    );
  }

  /**
   * Checks whether there are any respondents to remind.
   */
  get hasRespondents(): boolean {
    return this.studentListInfoTableRowModels.length > 0 || this.instructorListInfoTableRowModels.length > 0;
  }

  /**
   * Checks whether all students are selected.
   */
  get isAllStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all yet to submit students are selected.
   *
   * If all students have submitted it will return false.
   */
  get isAllYetToSubmitStudentsSelected(): boolean {
    const nonSubmitters: StudentListInfoTableRowModel[] = this.studentListInfoTableRowModels.filter(
      (model: StudentListInfoTableRowModel) => !model.hasSubmittedSession,
    );

    return nonSubmitters.length > 0 && nonSubmitters.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all instructors are selected.
   */
  get isAllInstructorsSelected(): boolean {
    return this.instructorListInfoTableRowModels.every((model: InstructorListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all yet to submit instructors are selected.
   *
   * If all instructors have submitted it will return false.
   */
  get isAllYetToSubmitInstructorsSelected(): boolean {
    const nonSubmitters: InstructorListInfoTableRowModel[] = this.instructorListInfoTableRowModels.filter(
      (model: InstructorListInfoTableRowModel) => !model.hasSubmittedSession,
    );

    return (
      nonSubmitters.length > 0 && nonSubmitters.every((model: InstructorListInfoTableRowModel) => model.isSelected)
    );
  }
}
