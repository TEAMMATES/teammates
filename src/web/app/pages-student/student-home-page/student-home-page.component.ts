import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';

import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
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
import { ErrorMessageOutput } from '../../error-message-output';

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
              private feedbackSessionsService: FeedbackSessionsService,
              private timezoneService: TimezoneService) {
    this.timezoneService.getTzVersion();
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
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
        this.feedbackSessionsService.getFeedbackSessionsForStudent(course.courseId)
            .subscribe((fss: FeedbackSessions) => {
              const sortedFss: FeedbackSession[] = fss.feedbackSessions
                  .map((fs: FeedbackSession) => Object.assign({}, fs))
                  .sort((a: FeedbackSession, b: FeedbackSession) => (a.createdAtTimestamp >
                      b.createdAtTimestamp) ? 1 : (a.createdAtTimestamp === b.createdAtTimestamp) ?
                      ((a.submissionEndTimestamp > b.submissionEndTimestamp) ? 1 : -1) : -1);
              this.courses.push(Object.assign({}, { course, feedbackSessions: sortedFss }));
              this.courses.sort((a: StudentCourse, b: StudentCourse) =>
                  (a.course.courseId > b.course.courseId) ? 1 : -1);

              for (const fs of fss.feedbackSessions) {
                const fid: string = course.courseId.concat('%').concat(fs.feedbackSessionName);
                const endTime: string = moment(fs.submissionEndTimestamp).tz(fs.timeZone)
                    .format('ddd, DD MMM, YYYY, hh:mm A zz');
                const isOpened: boolean = this.isOpened(fs);
                const isWaitingToOpen: boolean = this.isWaitingToOpen(fs);
                const isPublished: boolean = this.isPublished(fs);
                this.feedbackSessionsService.hasStudentResponseForFeedbackSession(course.courseId,
                    fs.feedbackSessionName)
                    .subscribe((hasRes: HasResponses) => {
                      const isSubmitted: boolean = hasRes.hasResponses;
                      this.sessionsInfoMap[fid] = { endTime, isOpened, isWaitingToOpen, isPublished, isSubmitted };
                    });
              }
            });
      }
    }, (e: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(e.error.message);
    });
  }

  /**
   * Checks if feedback session is opened.
   */
  isOpened(fs: FeedbackSession): boolean {
    return fs.submissionStatus === FeedbackSessionSubmissionStatus.OPEN;
  }

  /**
   * Checks if feedback session is waiting to open.
   */
  isWaitingToOpen(fs: FeedbackSession): boolean {
    return fs.submissionStatus === FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
  }

  /**
   * Checks if feedback session is published.
   */
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
