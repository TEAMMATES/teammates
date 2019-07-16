import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FeedbackSessionsService} from "../../../services/feedback-sessions.service";

import {HttpRequestService} from '../../../services/http-request.service';
import {StatusMessageService} from '../../../services/status-message.service';
import {
  Courses,
  Course,
  FeedbackSessions,
  FeedbackSession,
  HasResponses, FeedbackSessionPublishStatus,
} from "../../../types/api-output";
import {ErrorMessageOutput} from '../../error-message-output';

interface SessionInfoMap {
  endTime: string;
  isOpened: boolean;
  isWaitingToOpen: boolean;
  isPublished: boolean;
  isSubmitted: boolean;
}

interface StudentCourse {
  course: Course;
  feedbackSessions: FeedbackSession[];
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

  user: string = '';

  recentlyJoinedCourseId?: string = '';
  hasEventualConsistencyMsg: boolean = false;
  courses: StudentCourse[] = [];
  sessionsInfoMap: { [key: string]: SessionInfoMap } = {};

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private feedbackSessionsService: FeedbackSessionsService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      if (queryParams.persistencecourse) {
        this.recentlyJoinedCourseId = queryParams.persistencecourse;
      }
      this.getStudentCourses();
    });
  }

  /**
   * Gets the courses and feedback sessions involving the student.
   */
  getStudentCourses(): void {
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
    };
    this.httpRequestService.get('/courses', paramMap).subscribe((resp: Courses) => {
      for (const course of resp.courses) {
        this.feedbackSessionsService.getFeedbackSessionsForStudent(course.courseId).subscribe((fss: FeedbackSessions) => {

          const sortedFss: FeedbackSession[] = fss.feedbackSessions
              .map((fs: FeedbackSession) => Object.assign({}, fs))
              .sort((a: FeedbackSession, b: FeedbackSession) =>
                  (a.createdAtTimestamp > b.createdAtTimestamp) ? 1 : (a.createdAtTimestamp === b.createdAtTimestamp) ?
                      ((a.submissionEndTimestamp > b.submissionEndTimestamp) ? 1 : -1) : -1 );
          this.courses.push(Object.assign({}, { course, feedbackSessions: sortedFss }));
          this.courses.sort((a: StudentCourse, b: StudentCourse) =>
              (a.course.courseId > b.course.courseId) ? 1 : -1);
        });
      }

      if (this.recentlyJoinedCourseId && this.recentlyJoinedCourseId != '') {
        let isDataConsistent: boolean = false;
        for (const course of resp.courses) {
          if (course.courseId === this.recentlyJoinedCourseId) {
            isDataConsistent = true;
            break;
          }
        }
        if (!isDataConsistent) {
          const params: { [key: string]: string } = {
            entitytype: 'student',
            courseid: this.recentlyJoinedCourseId,
          };
          this.httpRequestService.get('/course', params).subscribe((course: Course) => {
            if (course) {
              this.hasEventualConsistencyMsg = false;
            }
          }, (err: ErrorMessageOutput) => {
            if (err.status === 404) {
              this.hasEventualConsistencyMsg = true;
            }
          });
        }
      }

      for (const course of resp.courses) {
        this.feedbackSessionsService.getFeedbackSessionsForStudent(course.courseId).subscribe((resp: FeedbackSessions) => {
          for (const fs of resp.feedbackSessions) {
            const fid: string = course.courseId + '%' + fs.feedbackSessionName;
            const endTime: string = new Date(fs.submissionEndTimestamp).toISOString();
            const isOpened: boolean = this.isOpened(fs);
            const isWaitingToOpen: boolean = this.isWaitingToOpen(fs);
            const isPublished: boolean = this.isPublished(fs);
            this.feedbackSessionsService.hasStudentResponseForFeedbackSession(course.courseId, fs.feedbackSessionName)
                .subscribe((resp: HasResponses) => {
              const isSubmitted: boolean = resp.hasResponses;
              this.sessionsInfoMap[fid] = { endTime, isOpened, isWaitingToOpen, isPublished, isSubmitted };
            });
          }
        });
      }

      if (this.hasEventualConsistencyMsg) {
        this.statusMessageService.showWarningMessage(
            'You have successfully joined the course ' + `${this.recentlyJoinedCourseId}` + '. '
            + 'Updating of the course data on our servers is currently in progress '
            + 'and will be completed in a few minutes. '
            + 'Please refresh this page in a few minutes to see the course ' + `${this.recentlyJoinedCourseId}`
            + ' in the list below.');
      }
    }, (e: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(e.error.message);
    });
  }

  isOpened(fs: FeedbackSession): boolean {
    const now: number = new Date().getTime();
    return now >= fs.submissionStartTimestamp && now < fs.submissionEndTimestamp;
  }

  isWaitingToOpen(fs: FeedbackSession): boolean {
    const now: number = new Date().getTime();
    return now < fs.submissionStartTimestamp;
  }

  isPublished(fs: FeedbackSession): boolean {
    return fs.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
  }

  /**
   * Gets the tooltip message for the submission status.
   */
  getSubmissionStatusTooltip(sessionInfoMap: SessionInfoMap): string {
    let msg: string = '';

    if (sessionInfoMap.isWaitingToOpen) {
      msg += this.studentFeedbackSessionStatusAwaiting;
    } else if (sessionInfoMap.isSubmitted) {
      msg += this.studentFeedbackSessionStatusSubmitted;
    } else {
      msg += this.studentFeedbackSessionStatusPending;
    }
    if (!sessionInfoMap.isOpened && !sessionInfoMap.isWaitingToOpen) {
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
}
