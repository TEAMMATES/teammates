import { Component, Input, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { finalize, switchMap } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  FeedbackSession,
  FeedbackSessionSubmittedGiverSet,
  FeedbackSessionView,
  Instructor,
  Instructors,
  Student,
  Students,
} from '../../../types/api-output';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { RespondentListInfoTableComponent } from '../../components/sessions-table/respondent-list-info-table/respondent-list-info-table.component';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Instructor send reminders page.
 */
@Component({
  selector: 'tm-instructor-session-send-reminders-page',
  templateUrl: './instructor-session-send-reminders-page.component.html',
  styleUrls: ['./instructor-session-send-reminders-page.component.scss'],
  imports: [
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    FormsModule,
    RespondentListInfoTableComponent,
    FormatDateDetailPipe,
  ],
})
export class InstructorSessionSendRemindersPageComponent implements OnInit {
  private readonly courseService = inject(CourseService);
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly instructorService = inject(InstructorService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly studentService = inject(StudentService);

  courseId = '';
  courseName = '';
  feedbackSessionName = '';
  feedbackSessionEndingTimestamp = 0;
  feedbackSessionTimeZone = 'UTC';

  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];
  instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [];

  isSendingCopyToInstructor = true;
  isLoadingData = true;
  hasLoadingDataFailed = false;
  isSubmittingReminders = false;

  @Input({ required: true }) feedbackSessionId!: string;
  @Input() preselectNonSubmitters = 'false';

  ngOnInit(): void {
    this.preselectNonSubmitters ||= 'false';
    this.loadPageData();
  }

  /**
   * Loads feedback session details and respondent selection data.
   */
  loadPageData(): void {
    this.isLoadingData = true;
    this.hasLoadingDataFailed = false;
    this.studentListInfoTableRowModels = [];
    this.instructorListInfoTableRowModels = [];

    this.feedbackSessionsService
      .getFeedbackSession({
        feedbackSessionId: this.feedbackSessionId,
      })
      .pipe(
        switchMap((feedbackSessionView: FeedbackSessionView) => {
          this.setFeedbackSessionDetails(feedbackSessionView.feedbackSession);

          return forkJoin({
            courseView: this.courseService.getCourseAsInstructor(this.courseId),
            students: this.studentService.getStudents({ courseIds: [this.courseId] }),
            instructors: this.instructorService.loadInstructors({ courseId: this.courseId }),
            submittedGiverSet: this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
              feedbackSessionId: this.feedbackSessionId,
            }),
          });
        }),
        finalize(() => {
          this.isLoadingData = false;
        }),
      )
      .subscribe({
        next: ({ courseView, students, instructors, submittedGiverSet }) => {
          this.courseName = courseView.course.courseName;
          this.populateRespondentModels(students, instructors, submittedGiverSet);
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasLoadingDataFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Changes selection state for all students.
   */
  changeSelectionStatusForAllStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModels = this.studentListInfoTableRowModels.map(
      (model: StudentListInfoTableRowModel) => ({
        ...model,
        isSelected: shouldSelect,
      }),
    );
  }

  /**
   * Changes selection state for all yet to submit students.
   */
  changeSelectionStatusForAllYetSubmittedStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModels = this.studentListInfoTableRowModels.map(
      (model: StudentListInfoTableRowModel) => ({
        ...model,
        isSelected: model.hasSubmittedSession ? model.isSelected : shouldSelect,
      }),
    );
  }

  /**
   * Changes selection state for all instructors.
   */
  changeSelectionStatusForAllInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModels = this.instructorListInfoTableRowModels.map(
      (model: InstructorListInfoTableRowModel) => ({
        ...model,
        isSelected: shouldSelect,
      }),
    );
  }

  /**
   * Changes selection state for all yet to submit instructors.
   */
  changeSelectionStatusForAllYetSubmittedInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModels = this.instructorListInfoTableRowModels.map(
      (model: InstructorListInfoTableRowModel) => ({
        ...model,
        isSelected: model.hasSubmittedSession ? model.isSelected : shouldSelect,
      }),
    );
  }

  /**
   * Changes selection state for sending a copy to requesting instructor.
   */
  changeSelectionStatusForSendingCopyToInstructorHandler(shouldSendCopy: boolean): void {
    this.isSendingCopyToInstructor = shouldSendCopy;
  }

  /**
   * Sends reminders to the selected respondents.
   */
  sendReminders(): void {
    const selectedUserIds = this.selectedRespondents.map((model) => model.id);
    if (selectedUserIds.length == 0) {
      this.statusMessageService.showErrorToast('Please select at least one respondent to remind.');
      return;
    }
    this.isSubmittingReminders = true;
    this.feedbackSessionsService
      .remindFeedbackSessionSubmissionForRespondents(this.feedbackSessionId, {
        usersToRemind: selectedUserIds,
        isSendingCopyToInstructor: this.isSendingCopyToInstructor,
      })
      .pipe(
        finalize(() => {
          this.isSubmittingReminders = false;
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
  }

  get hasRespondents(): boolean {
    return this.studentListInfoTableRowModels.length > 0 || this.instructorListInfoTableRowModels.length > 0;
  }

  get isAllStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  get isAllYetToSubmitStudentsSelected(): boolean {
    const nonSubmitters: StudentListInfoTableRowModel[] = this.studentListInfoTableRowModels.filter(
      (model: StudentListInfoTableRowModel) => !model.hasSubmittedSession,
    );

    return nonSubmitters.length > 0 && nonSubmitters.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  get isAllInstructorsSelected(): boolean {
    return this.instructorListInfoTableRowModels.every((model: InstructorListInfoTableRowModel) => model.isSelected);
  }

  get isAllYetToSubmitInstructorsSelected(): boolean {
    const nonSubmitters: InstructorListInfoTableRowModel[] = this.instructorListInfoTableRowModels.filter(
      (model: InstructorListInfoTableRowModel) => !model.hasSubmittedSession,
    );

    return (
      nonSubmitters.length > 0 && nonSubmitters.every((model: InstructorListInfoTableRowModel) => model.isSelected)
    );
  }

  private setFeedbackSessionDetails(feedbackSession: FeedbackSession): void {
    this.feedbackSessionName = feedbackSession.feedbackSessionName;
    this.courseId = feedbackSession.courseId;
    this.feedbackSessionEndingTimestamp = feedbackSession.submissionEndTimestamp;
    this.feedbackSessionTimeZone = feedbackSession.timeZone;
  }

  private populateRespondentModels(
    students: Students,
    instructors: Instructors,
    submittedGiverSet: FeedbackSessionSubmittedGiverSet,
  ): void {
    const shouldPreselectNonSubmitters = this.preselectNonSubmitters === 'true';
    const studentNonGiverSet = new Set(submittedGiverSet.studentNonGivers);
    const instructorNonGiverSet = new Set(submittedGiverSet.instructorNonGivers);

    this.studentListInfoTableRowModels = students.students.map(
      (student: Student) =>
        ({
          id: student.userId,
          email: student.email,
          name: student.name,
          teamName: student.teamName,
          sectionName: student.sectionName,
          hasSubmittedSession: !studentNonGiverSet.has(student.userId),
          isSelected: shouldPreselectNonSubmitters && studentNonGiverSet.has(student.userId),
        }) satisfies StudentListInfoTableRowModel,
    );

    this.instructorListInfoTableRowModels = instructors.instructors.map(
      (instructor: Instructor) =>
        ({
          id: instructor.userId,
          email: instructor.email,
          name: instructor.name,
          hasSubmittedSession: !instructorNonGiverSet.has(instructor.userId),
          isSelected: shouldPreselectNonSubmitters && instructorNonGiverSet.has(instructor.userId),
        }) satisfies InstructorListInfoTableRowModel,
    );
  }

  private get selectedRespondents(): (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] {
    return [...this.studentListInfoTableRowModels, ...this.instructorListInfoTableRowModels].filter((model) => {
      return model.isSelected;
    });
  }
}
