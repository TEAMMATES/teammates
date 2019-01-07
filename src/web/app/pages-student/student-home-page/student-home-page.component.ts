import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';

/**
 * Mock courses to test the UI.
 */
const COURSES: StudentCourse[] = [
  { id: 'test.exa-demo', name: 'Sample Course 101',
    sessions: [
      { name: 'First team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: false, isWaitingToOpen: true,
        isSessionVisible: true, isSessionPublished: false },
      { name: 'Second team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true },
      { name: 'Third team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false },
      { name: 'Fourth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true },
      { name: 'Fifth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false },
    ]},
  { id: 'CS3244', name: 'Sample Course 103',
    sessions: [
      { name: 'Sixth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: false, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true },
      { name: 'Seventh team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isOpened: false, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false }
    ]},
  { id: 'CS3103', name: 'Sample Course 103',
    sessions: []},
];

/**
 * Mock submission statuses for each mock feedback session.
 * @type {Map<any, any>}
 */
const SESSIONSUBMISSIONSTATUSMAP: Map<StudentFeedbackSession, Boolean> = new Map();

SESSIONSUBMISSIONSTATUSMAP.set(
    { name: 'First team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: false, isWaitingToOpen: true,
    isSessionVisible: true, isSessionPublished: false }, false );

SESSIONSUBMISSIONSTATUSMAP.set(
    { name: 'Second team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: true, isWaitingToOpen: false,
    isSessionVisible: true, isSessionPublished: true }, true );

SESSIONSUBMISSIONSTATUSMAP.set(
  { name: 'Third team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: true, isWaitingToOpen: false,
    isSessionVisible: true, isSessionPublished: false }, true);

SESSIONSUBMISSIONSTATUSMAP.set(
  { name: 'Fourth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: true, isWaitingToOpen: false,
    isSessionVisible: true, isSessionPublished: true }, false);

SESSIONSUBMISSIONSTATUSMAP.set(
  { name: 'Fifth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: true, isWaitingToOpen: false,
    isSessionVisible: true, isSessionPublished: false }, false);

SESSIONSUBMISSIONSTATUSMAP.set(
  { name: 'Sixth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: false, isWaitingToOpen: false,
    isSessionVisible: true, isSessionPublished: true }, true);

SESSIONSUBMISSIONSTATUSMAP.set(
  { name: 'Seventh team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
    isOpened: false, isWaitingToOpen: false,
    isSessionVisible: true, isSessionPublished: false }, false);

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
      this.getStudentCourses(queryParams.persistencecourse);
    });

    this.getMockCourses();
  }

  /**
   * Gets mock data (for TEMPORARY use only).
   */
  getMockCourses(): void {
    this.courses = COURSES;
  }

  /**
   * Gets the courses and feedback sessions involving the student.
   */
  getStudentCourses(persistencecourse: string): void {
    const paramMap: { [key: string]: string } = { persistencecourse };
    this.httpRequestService.get('/students/courses', paramMap).subscribe((resp: StudentCourses) => {
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
            + ' in the list below.'
        );
      }

    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
