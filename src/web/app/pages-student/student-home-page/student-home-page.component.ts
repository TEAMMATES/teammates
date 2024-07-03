import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  Courses,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  HasResponses,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';
import { SubmissionStatusPipe } from '../../pipes/session-submission-status.pipe';

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
  isWaitingToOpen: boolean;
  isPublished: boolean;
  isSubmitted: boolean;
}

/**
 * Student home page.
 */
@Component({
  selector: 'tm-student-home-page',
  templateUrl: './student-home-page.component.html',
  styleUrls: ['./student-home-page.component.scss'],
  animations: [collapseAnim],
})
export class StudentHomePageComponent implements OnInit {
  // enum
  SortBy: typeof SortBy = SortBy;

  // Tooltip messages
  studentFeedbackSessionStatusPublished: string =
    'The responses for the session have been published and can now be viewed.';
  studentFeedbackSessionStatusNotPublished: string =
    'The responses for the session have not yet been published and cannot be viewed.';
  studentFeedbackSessionStatusAwaiting: string =
    'The session is not open for submission at this time. It is expected to open later.';
  studentFeedbackSessionStatusPending: string = 'The feedback session is yet to be completed by you.';
  studentFeedbackSessionStatusExtension: string = ' An instructor has granted you a deadline extension.';
  studentFeedbackSessionStatusSubmitted: string = 'You have submitted your feedback for this session.';
  studentFeedbackSessionStatusClosed: string = ' The session is now closed for submissions.';

  // Error messages
  allStudentFeedbackSessionsNotReturned: string =
      'Something went wrong with fetching responses for all Feedback Sessions.';

  courses: StudentCourse[] = [];
  isCoursesLoading: boolean = false;
  hasCoursesLoadingFailed: boolean = false;

  sortBy: SortBy = SortBy.COURSE_CREATION_DATE;

  sessionSubmissionStatusPipe = new SubmissionStatusPipe();
  formatDateDetailPipe = new FormatDateDetailPipe(this.timezoneService);

  constructor(private route: ActivatedRoute,
    private courseService: CourseService,
    private statusMessageService: StatusMessageService,
    private feedbackSessionsService: FeedbackSessionsService,
    private timezoneService: TimezoneService,
    private tableComparatorService: TableComparatorService) {
    this.timezoneService.getTzVersion();
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(() => {
      this.loadStudentCourses();
    });
  }

  /**
   * Load the courses and feedback sessions involving the student.
   */
  loadStudentCourses(): void {
    this.hasCoursesLoadingFailed = false;
    this.isCoursesLoading = true;
    this.courses = [];
    this.courseService.getAllCoursesAsStudent()
      .pipe(finalize(() => { this.isCoursesLoading = false; }))
      .subscribe({
        next: (resp: Courses) => {
          resp.courses.forEach((course: Course) => {
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
    this.feedbackSessionsService.getFeedbackSessionsForStudent('student', courseId).subscribe({
      next: (fss: FeedbackSessions) => {
        const sortedFss: FeedbackSession[] = this.sortFeedbackSessions(fss);

        this.feedbackSessionsService
          .hasStudentResponseForAllFeedbackSessionsInCourse(courseId)
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
                sortedFss.filter((fs: FeedbackSession) => sessionsReturned.has(fs.feedbackSessionName)).length
                === sortedFss.length;

              if (!isAllSessionsPresent) {
                this.statusMessageService.showErrorToast(this.allStudentFeedbackSessionsNotReturned);
                courseRef.hasFeedbackSessionsLoadingFailed = true;
                return;
              }

              for (const fs of sortedFss) {
                const isOpened: boolean = fs.submissionStatus === FeedbackSessionSubmissionStatus.OPEN;
                const isWaitingToOpen: boolean =
                  fs.submissionStatus === FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
                const isPublished: boolean = fs.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;

                const isSubmitted: boolean = hasRes.hasResponsesBySession[fs.feedbackSessionName];
                courseRef.feedbackSessions.push({
                  isOpened,
                  isWaitingToOpen,
                  isPublished,
                  isSubmitted,
                  session: fs,
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
    let msg: string = '';
    const hasStudentExtension = DeadlineExtensionHelper.hasUserExtension(session.session);
    const hasOngoingStudentExtension = DeadlineExtensionHelper.hasOngoingExtension(session.session);

    if (session.isWaitingToOpen) {
      msg += this.studentFeedbackSessionStatusAwaiting;
    } else if (session.isSubmitted) {
      msg += this.studentFeedbackSessionStatusSubmitted;
    } else {
      msg += this.studentFeedbackSessionStatusPending;
    }

    if (hasStudentExtension && (session.isSubmitted || session.isOpened)) {
      msg += this.studentFeedbackSessionStatusExtension;
    }

    if (!session.isOpened && !session.isWaitingToOpen && !hasOngoingStudentExtension) {
      msg += this.studentFeedbackSessionStatusClosed;
    }
    return msg;
  }

  /**
   * Gets the status for the submission.
   */
  getSubmissionStatus(session: StudentSession): string {
    const hasStudentExtension = this.hasStudentExtension(session.session);
    return this.sessionSubmissionStatusPipe.transform(
      session.isOpened, session.isWaitingToOpen, session.isSubmitted, hasStudentExtension);
  }

  /**
   * Get the formatted date of the student's session end time.
   */
  getSubmissionEndDate({ session }: StudentSession): string {
    const submissionEndDate = DeadlineExtensionHelper.getUserFeedbackSessionEndingTimestamp(session);
    return this.formatDateDetailPipe.transform(submissionEndDate, session.timeZone);
  }

  getSubmissionEndDateTooltip({ session }: StudentSession): string {
    const hasStudentExtension = this.hasStudentExtension(session);
    if (!hasStudentExtension) {
      return '';
    }
    const originalEndTime = this.formatDateDetailPipe.transform(session.submissionEndTimestamp, session.timeZone);
    return `The session's original end date is ${originalEndTime}.`
      + ' An instructor has granted you an extension to this date.';
  }

  hasStudentExtension(session: FeedbackSession): boolean {
    return DeadlineExtensionHelper.hasUserExtension(session);
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
  sortFeedbackSessions(fss: FeedbackSessions): FeedbackSession[] {
    return fss.feedbackSessions
      .map((fs: FeedbackSession) => ({ ...fs }))
      .sort((a: FeedbackSession, b: FeedbackSession) => {
        if (a.createdAtTimestamp > b.createdAtTimestamp) {
          return 1;
        }
        if (a.createdAtTimestamp === b.createdAtTimestamp) {
          return a.submissionEndTimestamp > b.submissionEndTimestamp ? 1 : -1;
        }
        return -1;
      });
  }

  sortCoursesBy(by: SortBy): void {
    this.sortBy = by;

    // make a copy of the courses array and sort it
    const copy: StudentCourse[] = JSON.parse(JSON.stringify(this.courses));
    copy.sort(this.sortPanelsBy(by));
    this.courses = copy;

    // open the first three panels
    this.courses.slice(0, 3).forEach((course: StudentCourse) => {
      course.isTabExpanded = true;
      this.loadFeedbackSessionsForCourse(course.course.courseId);
    });
  }

  sortPanelsBy(by: SortBy): ((a: StudentCourse, b: StudentCourse) => number) {
    return ((a: StudentCourse, b: StudentCourse): number => {
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
    });
  }
}
