import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';

/**
 * Mock feedback sessions to test the UI.
 */
const sessionA: StudentFeedbackSession = {
  name: 'A - awaiting, not published, not submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: false, isWaitingToOpen: true, isSessionVisible: true, isSessionPublished: false };

const sessionB: StudentFeedbackSession = {
  name: 'B - open, published, submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: true, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: true };

const sessionC: StudentFeedbackSession = {
  name: 'C - open, published, not submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: true, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: true };

const sessionD: StudentFeedbackSession = {
  name: 'D - open, not published, submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: true, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: false };

const sessionE: StudentFeedbackSession = {
  name: 'E - open, not published, not submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: true, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: false };

const sessionF: StudentFeedbackSession = {
  name: 'F - closed, published, submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: false, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: true };

const sessionG: StudentFeedbackSession = {
  name: 'G - closed, published, not submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: false, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: true };

const sessionH: StudentFeedbackSession = {
  name: 'H - closed, not published, submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: false, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: false };

const sessionI: StudentFeedbackSession = {
  name: 'I - closed, not published, not submitted', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
  isOpened: false, isWaitingToOpen: false, isSessionVisible: true, isSessionPublished: false };

/**
 * Mock courses to test the UI.
 */
const COURSES: StudentCourse[] = [
  { id: 'test.exa-demo', name: 'Course without sessions', sessions: [] },
  { id: 'test.exa-demo1', name: 'Sample Course 101',
    sessions: [sessionA, sessionB, sessionC, sessionD, sessionE] },
  { id: 'test.exa-demo2', name: 'Sample Course 101',
    sessions: [sessionF, sessionG, sessionH, sessionI] },
];

/**
 * Mock submission statuses for each mock feedback session.
 */
const SESSION_SUBMISSION_STATUS_MAP: Map<StudentFeedbackSession, Boolean> = new Map();
SESSION_SUBMISSION_STATUS_MAP.set(sessionA, false);
SESSION_SUBMISSION_STATUS_MAP.set(sessionB, true);
SESSION_SUBMISSION_STATUS_MAP.set(sessionC, false);
SESSION_SUBMISSION_STATUS_MAP.set(sessionD, true);
SESSION_SUBMISSION_STATUS_MAP.set(sessionE, false);
SESSION_SUBMISSION_STATUS_MAP.set(sessionF, true);
SESSION_SUBMISSION_STATUS_MAP.set(sessionG, false);
SESSION_SUBMISSION_STATUS_MAP.set(sessionH, true);
SESSION_SUBMISSION_STATUS_MAP.set(sessionI, false);

interface StudentFeedbackSession {
  name: string;
  deadline: string;

  isOpened: boolean;
  isWaitingToOpen: boolean;
  isSessionVisible: boolean;
  isSessionPublished: boolean;
}

interface StudentCourse {
  id: string;
  name: string;

  sessions: StudentFeedbackSession[];
}

interface StudentCourses {
  recentlyJoinedCourseId: string;
  hasEventualConsistencyMsg: boolean;
  courses: StudentCourse[];
  sessionSubmissionStatusMap: Map<StudentFeedbackSession, Boolean>;
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
  studentCourseDetails: string = 'View and edit information regarding your team';

  studentFeedbackSessionStatusPublished: string =
      'The responses for the session have been published and can now be viewed.';
  studentFeedbackSessionStatusNotPublished: string =
      'The responses for the session have not yet been published and cannot be viewed.';
  studentFeedbackSessionStatusAwaiting: string =
      'The session is not open for submission at this time. It is expected to open later.';
  studentFeedbackSessionStatusPending: string = 'The feedback session is yet to be completed by you.';
  studentFeedbackSessionStatusSubmitted: string = 'You have submitted your feedback for this session.';
  studentFeedbackSessionStatusClosed: string = ' The session is now closed for submissions.';

  feedbackSessionEditSubmittedResponse: string = 'Edit submitted feedback';
  feedbackSessionViewSubmittedResponse: string = 'View submitted feedback';
  feedbackSessionSubmit: string = 'Start submitting feedback';
  feedbackSessionResults: string = 'View the submitted responses for this feedback session';

  user: string = '';

  recentlyJoinedCourseId: string = '';
  hasEventualConsistencyMsg: boolean = false;
  courses: StudentCourse[] = [];
  sessionSubmissionStatusMap: Map<StudentFeedbackSession, Boolean> = new Map();

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      // this.getStudentCourses(queryParams.persistencecourse);
    });

    this.getMockCourses();
  }

  /**
   * Gets mock data (for TEMPORARY use only).
   */
  getMockCourses(): void {
    this.courses = COURSES;
    this.sessionSubmissionStatusMap = SESSION_SUBMISSION_STATUS_MAP;
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
      this.sessionSubmissionStatusMap = resp.sessionSubmissionStatusMap;

      if (this.hasEventualConsistencyMsg) {
        this.statusMessageService.showWarningMessage(
            'You have successfully joined the course ' + `${this.recentlyJoinedCourseId}` + '. '
            + '<br>Updating of the course data on our servers is currently in progress '
            + 'and will be completed in a few minutes. '
            + '<br>Please refresh this page in a few minutes to see the course ' + `${this.recentlyJoinedCourseId}`
            + ' in the list below.');
      }

    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Gets the tooltip message for the submission status.
   */
  getSubmissionStatusTooltip(isOpened: boolean, isWaitingToOpen: boolean, hasSubmitted: boolean): string {
    let msg: string = '';

    if (isWaitingToOpen) {
      msg += this.studentFeedbackSessionStatusAwaiting;
    } else if (hasSubmitted) {
      msg += this.studentFeedbackSessionStatusSubmitted;
    } else {
      msg += this.studentFeedbackSessionStatusPending;
    }
    if (!isOpened && !isWaitingToOpen) {
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
