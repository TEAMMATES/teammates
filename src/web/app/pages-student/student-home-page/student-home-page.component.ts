import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';

interface SessionInfoMap {
  endTime: string;
  isOpened: boolean;
  isWaitingToOpen: boolean;
  isPublished: boolean;
  isSubmitted: boolean;
}

interface FeedbackSessionAttributes {
  feedbackSessionName: string;
  courseId: string;
}

interface FeedbackSessionDetailsBundle {
  feedbackSession: FeedbackSessionAttributes;
}

interface StudentCourseAttributes {
  id: string;
  name: string;
}

interface StudentCourse {
  course: StudentCourseAttributes;
  feedbackSessions: FeedbackSessionDetailsBundle[];
}

interface StudentCourses {
  recentlyJoinedCourseId: string;
  hasEventualConsistencyMsg: boolean;
  courses: StudentCourse[];
  sessionsInfoMap: { [key: string]: SessionInfoMap };
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
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getStudentCourses(queryParams.persistencecourse);
    });
  }

  /**
   * Gets the courses and feedback sessions involving the student.
   */
  getStudentCourses(persistencecourse: string): void {
    const paramMap: { [key: string]: string } = { persistencecourse };
    this.httpRequestService.get('/student/courses', paramMap).subscribe((resp: StudentCourses) => {
      this.recentlyJoinedCourseId = resp.recentlyJoinedCourseId;
      this.hasEventualConsistencyMsg = resp.hasEventualConsistencyMsg;
      this.courses = resp.courses;
      this.sessionsInfoMap = resp.sessionsInfoMap;

      if (this.hasEventualConsistencyMsg) {
        this.statusMessageService.showWarningMessage(
            'You have successfully joined the course ' + `${this.recentlyJoinedCourseId}` + '. '
            + 'Updating of the course data on our servers is currently in progress '
            + 'and will be completed in a few minutes. '
            + 'Please refresh this page in a few minutes to see the course ' + `${this.recentlyJoinedCourseId}`
            + ' in the list below.');
      }

    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
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
