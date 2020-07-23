import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
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
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentCourse {
  course: Course;
  feedbackSessions: StudentSession[];
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
  studentFeedbackSessionStatusSubmitted: string = 'You have submitted your feedback for this session.';
  studentFeedbackSessionStatusClosed: string = ' The session is now closed for submissions.';

  courses: StudentCourse[] = [];
  isCoursesLoading: boolean = false;
  hasCoursesLoadingFailed: boolean = false;

  sortBy: SortBy = SortBy.NONE;

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
    this.courseService.getAllCoursesAsStudent().subscribe((resp: Courses) => {
      if (!resp.courses.length) {
        this.isCoursesLoading = false;
      }
      for (const course of resp.courses) {
        this.feedbackSessionsService.getFeedbackSessionsForStudent(course.courseId)
          .pipe(finalize(() => this.isCoursesLoading = false))
          .subscribe((fss: FeedbackSessions) => {
            const sortedFss: FeedbackSession[] = this.sortFeedbackSessions(fss);

            const studentSessions: StudentSession[] = [];
            for (const fs of sortedFss) {
              const isOpened: boolean = fs.submissionStatus === FeedbackSessionSubmissionStatus.OPEN;
              const isWaitingToOpen: boolean =
                fs.submissionStatus === FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
              const isPublished: boolean = fs.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
              this.feedbackSessionsService.hasStudentResponseForFeedbackSession(course.courseId,
                fs.feedbackSessionName)
                .subscribe((hasRes: HasResponses) => {
                  const isSubmitted: boolean = hasRes.hasResponses;
                  studentSessions.push(Object.assign({},
                    { isOpened, isWaitingToOpen, isPublished, isSubmitted, session: fs }));
                }, (error: ErrorMessageOutput) => {
                  this.hasCoursesLoadingFailed = true;
                  this.statusMessageService.showErrorToast(error.error.message);
                });
            }

            this.courses.push(Object.assign({}, { course, feedbackSessions: studentSessions }));
            this.courses.sort((a: StudentCourse, b: StudentCourse) =>
              (a.course.courseId > b.course.courseId) ? 1 : -1);
          }, (error: ErrorMessageOutput) => {
            this.hasCoursesLoadingFailed = true;
            this.statusMessageService.showErrorToast(error.error.message);
          });
      }
    }, (e: ErrorMessageOutput) => {
      this.hasCoursesLoadingFailed = true;
      this.statusMessageService.showErrorToast(e.error.message);
    });
  }

  /**
   * Gets the tooltip message for the submission status.
   */
  getSubmissionStatusTooltip(session: StudentSession): string {
    let msg: string = '';

    if (session.isWaitingToOpen) {
      msg += this.studentFeedbackSessionStatusAwaiting;
    } else if (session.isSubmitted) {
      msg += this.studentFeedbackSessionStatusSubmitted;
    } else {
      msg += this.studentFeedbackSessionStatusPending;
    }
    if (!session.isOpened && !session.isWaitingToOpen) {
      msg += this.studentFeedbackSessionStatusClosed;
    }
    return msg;
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
        .map((fs: FeedbackSession) => Object.assign({}, fs))
        .sort((a: FeedbackSession, b: FeedbackSession) => (a.createdAtTimestamp >
            b.createdAtTimestamp) ? 1 : (a.createdAtTimestamp === b.createdAtTimestamp) ?
            ((a.submissionEndTimestamp > b.submissionEndTimestamp) ? 1 : -1) : -1);
  }

  sortCoursesBy(by: SortBy): void {
    this.sortBy = by;
    this.courses.sort(this.sortPanelsBy(by));
  }

  sortPanelsBy(by: SortBy): ((a: StudentCourse, b: StudentCourse) => number) {
    return ((a: StudentCourse, b: StudentCourse): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          break;
        case SortBy.COURSE_ID:
          strA = a.course.courseId;
          strB = b.course.courseId;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, SortOrder.ASC, strA, strB);
    });
  }
}
