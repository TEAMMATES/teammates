import { NgClass } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  CourseView,
  Courses,
  FeedbackSession,
  FeedbackSessionView,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  HasResponses,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { RouterLink } from '@angular/router';
import { ErrorMessageOutput } from '../../error-message-output';
import { ResponseStatusPipe } from '../../pipes/session-response-status.pipe';
import { DateFormatService } from '../../../services/date-format.service';
import { sessionSubmissionStatusDisplay } from '../../utils/session-submission-status.util';

interface StudentCourse {
  course: Course;
  feedbackSessions: StudentSession[];
  isFeedbackSessionsLoading: boolean;
  hasFeedbackSessionsLoadingFailed: boolean;
  isTabExpanded: boolean;
  hasPopulated: boolean;
}

interface StudentSession {
  session: FeedbackSession;
  isOpened: boolean;
  isPublished: boolean;
  isSubmitted: boolean;
  userDeadlineExtension?: number;
}

/**
 * Student home page.
 */
@Component({
  selector: 'tm-student-home-page',
  templateUrl: './student-home-page.component.html',
  styleUrls: ['./student-home-page.component.scss'],
  imports: [
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    RouterLink,
    PanelChevronComponent,
    NgbTooltip,
    NgClass,
    ResponseStatusPipe,
    NgbCollapse,
  ],
  providers: [FormatDateDetailPipe],
})
export class StudentHomePageComponent implements OnInit {
  private courseService = inject(CourseService);
  private statusMessageService = inject(StatusMessageService);
  private feedbackSessionsService = inject(FeedbackSessionsService);
  private tableComparatorService = inject(TableComparatorService);
  private dateFormatService = inject(DateFormatService);

  private readonly timezoneService = inject(TimezoneService);

  // enum
  SortBy!: typeof SortBy;

  // Tooltip messages
  studentFeedbackSessionStatusPublished = 'The responses for the session have been published and can now be viewed.';
  studentFeedbackSessionStatusNotPublished =
    'The responses for the session have not yet been published and cannot be viewed.';
  studentFeedbackSessionStatusPending = 'The feedback session is yet to be completed by you.';
  studentFeedbackSessionStatusExtension = ' An instructor has granted you a deadline extension.';
  studentFeedbackSessionStatusSubmitted = 'You have submitted your feedback for this session.';
  studentFeedbackSessionStatusClosed = ' The session is now closed for submissions.';

  // Error messages
  allStudentFeedbackSessionsNotReturned = 'Something went wrong with fetching responses for all Feedback Sessions.';

  courses: StudentCourse[] = [];
  isCoursesLoading = false;
  hasCoursesLoadingFailed = false;

  sortBy: SortBy = SortBy.COURSE_CREATION_DATE;

  constructor() {
    this.SortBy = SortBy;
    this.timezoneService.getTzVersion();
  }

  ngOnInit(): void {
    this.loadStudentCourses();
  }

  /**
   * Load the courses and feedback sessions involving the student.
   */
  loadStudentCourses(): void {
    this.hasCoursesLoadingFailed = false;
    this.isCoursesLoading = true;
    this.courses = [];
    this.courseService
      .getAllCoursesAsStudent()
      .pipe(
        finalize(() => {
          this.isCoursesLoading = false;
        }),
      )
      .subscribe({
        next: (resp: Courses) => {
          resp.courses.forEach((courseView: CourseView) => {
            const course = courseView.course;
            this.courses.push({
              course,
              feedbackSessions: [],
              isFeedbackSessionsLoading: false,
              hasFeedbackSessionsLoadingFailed: false,
              isTabExpanded: false,
              hasPopulated: false,
            });
          });

          this.sortCoursesBy(SortBy.COURSE_CREATION_DATE);
        },
        error: (e: ErrorMessageOutput) => {
          this.hasCoursesLoadingFailed = true;
          this.statusMessageService.showErrorToast(e.error.message);
        },
      });
  }

  /**
   * Handles click events on the course tab model.
   */
  handleClick(event: Event, studentCourse: StudentCourse): boolean {
    if (event.target && !(event.target as HTMLElement).className.includes('dropdown-toggle')) {
      return !studentCourse.isTabExpanded;
    }
    return studentCourse.isTabExpanded;
  }

  /**
   * Load feedback sessions for a single course.
   * The course should have been pushed to the this.courses array before this.
   */
  loadFeedbackSessionsForCourse(courseId: string): void {
    // reference to the course within the this.courses array
    const courseRef = this.courses.find((c) => c.course.courseId === courseId)!;
    if (courseRef.hasPopulated) {
      return;
    }
    courseRef.isFeedbackSessionsLoading = true;
    courseRef.hasFeedbackSessionsLoadingFailed = false;
    courseRef.feedbackSessions = [];
    this.feedbackSessionsService.getFeedbackSessionsForStudent(courseId).subscribe({
      next: (fss: FeedbackSessions) => {
        const sortedFss: FeedbackSessionView[] = this.sortFeedbackSessions(fss);

        this.feedbackSessionsService
          .hasResponsesForAllFeedbackSessionsInCourse(courseId, 'student')
          .pipe(
            finalize(() => {
              courseRef.isFeedbackSessionsLoading = false;
            }),
          )
          .subscribe({
            next: (hasRes: HasResponses) => {
              if (!hasRes.hasResponsesBySession) {
                this.statusMessageService.showErrorToast(this.allStudentFeedbackSessionsNotReturned);
                courseRef.hasFeedbackSessionsLoadingFailed = true;
                return;
              }

              const sessionsReturned: Set<string> = new Set(Object.keys(hasRes.hasResponsesBySession));
              const isAllSessionsPresent: boolean =
                sortedFss.filter((fsView: FeedbackSessionView) =>
                  sessionsReturned.has(fsView.feedbackSession.feedbackSessionName),
                ).length === sortedFss.length;

              if (!isAllSessionsPresent) {
                this.statusMessageService.showErrorToast(this.allStudentFeedbackSessionsNotReturned);
                courseRef.hasFeedbackSessionsLoadingFailed = true;
                return;
              }

              for (const fsView of sortedFss) {
                const fs = fsView.feedbackSession;
                const userDeadlineExtension = fsView.userDeadlineExtension;
                const hasActiveExtension = DeadlineExtensionHelper.hasUserOngoingExtension(fs, userDeadlineExtension);
                const rawStatus = fs.submissionStatus;
                const isOpened: boolean =
                  rawStatus === FeedbackSessionSubmissionStatus.OPEN ||
                  rawStatus === FeedbackSessionSubmissionStatus.GRACE_PERIOD ||
                  (rawStatus === FeedbackSessionSubmissionStatus.CLOSED && hasActiveExtension);
                const isPublished: boolean = fs.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;

                const isSubmitted: boolean = hasRes.hasResponsesBySession[fs.feedbackSessionName];
                courseRef.feedbackSessions.push({
                  isOpened,
                  isPublished,
                  isSubmitted,
                  session: fs,
                  userDeadlineExtension,
                });
              }

              // only set true if all feedback sessions are loaded
              courseRef.hasPopulated = true;
            },
            error: (error: ErrorMessageOutput) => {
              courseRef.hasFeedbackSessionsLoadingFailed = true;
              this.statusMessageService.showErrorToast(error.error.message);
            },
          });
      },
      error: (error: ErrorMessageOutput) => {
        courseRef.isFeedbackSessionsLoading = false;
        courseRef.hasFeedbackSessionsLoadingFailed = true;
        this.statusMessageService.showErrorToast(error.error.message);
      },
    });
  }

  /**
   * Gets the tooltip message for the submission status.
   */
  getSubmissionStatusTooltip(session: StudentSession): string {
    let msg = '';
    const hasStudentExtension = DeadlineExtensionHelper.hasUserExtension(
      session.session,
      session.userDeadlineExtension,
    );
    const hasOngoingStudentExtension = DeadlineExtensionHelper.hasUserOngoingExtension(
      session.session,
      session.userDeadlineExtension,
    );

    if (session.isSubmitted) {
      msg += this.studentFeedbackSessionStatusSubmitted;
    } else {
      msg += this.studentFeedbackSessionStatusPending;
    }

    if (hasStudentExtension && (session.isSubmitted || session.isOpened)) {
      msg += this.studentFeedbackSessionStatusExtension;
    }

    if (!session.isOpened && !hasOngoingStudentExtension) {
      msg += this.studentFeedbackSessionStatusClosed;
    }
    return msg;
  }

  /**
   * Gets the status for the submission.
   */
  getSubmissionStatus(session: StudentSession): string {
    const hasStudentExtension = this.hasStudentExtension(session.session, session.userDeadlineExtension);
    return sessionSubmissionStatusDisplay(session.isOpened, session.isSubmitted, hasStudentExtension);
  }

  /**
   * Get the formatted date of the student's session end time.
   */
  getSubmissionEndDate({ session, userDeadlineExtension }: StudentSession): string {
    const submissionEndDate = DeadlineExtensionHelper.getUserFeedbackSessionEndingTimestamp(
      session,
      userDeadlineExtension,
    );
    return this.dateFormatService.formatDateDetailed(submissionEndDate, session.timeZone);
  }

  getSubmissionEndDateTooltip({ session, userDeadlineExtension }: StudentSession): string {
    const hasStudentExtension = this.hasStudentExtension(session, userDeadlineExtension);
    if (!hasStudentExtension) {
      return '';
    }
    const originalEndTime = this.dateFormatService.formatDateDetailed(session.submissionEndTimestamp, session.timeZone);
    return (
      `The session's original end date is ${originalEndTime}.` +
      ' An instructor has granted you an extension to this date.'
    );
  }

  hasStudentExtension(session: FeedbackSession, userDeadlineExtension?: number): boolean {
    return DeadlineExtensionHelper.hasUserExtension(session, userDeadlineExtension);
  }

  /**
   * Gets the tooltip message for the response status.
   */
  getResponseStatusTooltip(isPublished: boolean): string {
    if (isPublished) {
      return this.studentFeedbackSessionStatusPublished;
    }
    return this.studentFeedbackSessionStatusNotPublished;
  }

  /**
   * Sorts the feedback sessions based on creation and end timestamp.
   */
  sortFeedbackSessions(fss: FeedbackSessions): FeedbackSessionView[] {
    return fss.feedbackSessions.slice().sort((a: FeedbackSessionView, b: FeedbackSessionView) => {
      const fsA = a.feedbackSession;
      const fsB = b.feedbackSession;
      if (fsA.createdAtTimestamp > fsB.createdAtTimestamp) {
        return 1;
      }
      if (fsA.createdAtTimestamp === fsB.createdAtTimestamp) {
        return fsA.submissionEndTimestamp > fsB.submissionEndTimestamp ? 1 : -1;
      }
      return -1;
    });
  }

  sortCoursesBy(by: SortBy): void {
    this.sortBy = by;

    // make a copy of the courses array and sort it
    const copy: StudentCourse[] = structuredClone(this.courses);
    copy.sort(this.sortPanelsBy(by));
    this.courses = copy;

    // open the first three panels
    this.courses.slice(0, 3).forEach((course: StudentCourse) => {
      course.isTabExpanded = true;
      this.loadFeedbackSessionsForCourse(course.course.courseId);
    });
  }

  sortPanelsBy(by: SortBy): (a: StudentCourse, b: StudentCourse) => number {
    return (a: StudentCourse, b: StudentCourse): number => {
      let strA: string;
      let strB: string;
      let sortOrder: SortOrder;
      switch (by) {
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          sortOrder = SortOrder.ASC;
          break;
        case SortBy.COURSE_ID:
          strA = a.course.courseId;
          strB = b.course.courseId;
          sortOrder = SortOrder.ASC;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = a.course.creationTimestamp.toString();
          strB = b.course.creationTimestamp.toString();
          sortOrder = SortOrder.DESC;
          break;
        default:
          strA = '';
          strB = '';
          sortOrder = SortOrder.ASC;
      }
      return this.tableComparatorService.compare(by, sortOrder, strA, strB);
    };
  }
}
