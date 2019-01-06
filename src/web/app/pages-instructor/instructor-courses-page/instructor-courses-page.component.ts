import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from "../../../services/http-request.service";
import { StatusMessageService } from "../../../services/status-message.service";
import { ErrorMessageOutput } from "../../message-output";

/**
 * Mock courses to test the UI.
 */
const ACTIVE_COURSES: ActiveCourse[]= [
  { id: 'test.exa-demo', name: 'Sample Course 101', teamLink: '#', createdAt: '01 Apr 2012',
    sessions: [
      { name: 'First team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: false, isOpened: false, isWaitingToOpen: true,
        isSessionVisible: true, isSessionPublished: false,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Second team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: true, isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
    ]}
];

const ARCHIVED_COURSES: ArchivedCourse[]= [
  { id: 'CS3244', name: 'Sample Course 103', teamLink: '#', createdAt: '01 Apr 2012',
    sessions: [
      { name: 'Third team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: true, isOpened: false, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Fourth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: false, isOpened: false, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
    ]}
];

const DELETED_COURSES: DeletedCourse[]= [
  { id: 'CS3245', name: 'Sample Course 105', teamLink: '#',
    createdAt: '01 Apr 2012', deletedAt: '01 Apr 2015',
    sessions: [
      { name: 'Sixth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: true, isOpened: false, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Seventh team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: false, isOpened: false, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
    ]}
];

interface StudentFeedbackSession {
  name: string;
  deadline: string;

  isSubmitted: boolean;
  isOpened: boolean;
  isWaitingToOpen: boolean;
  isSessionVisible: boolean;
  isSessionPublished: boolean;

  studentFeedbackResultsLink: string;
  studentFeedbackResponseEditLink: string;
}

interface ActiveCourse {
  id: string;
  name: string;
  teamLink: string;
  createdAt: string;

  sessions: StudentFeedbackSession[];
}

interface ActiveCourses {
  activeCourses: ActiveCourse[];
}

interface ArchivedCourse {
  id: string;
  name: string;
  teamLink: string;
  createdAt: string;

  sessions: StudentFeedbackSession[];
}

interface DeletedCourse {
  id: string;
  name: string;
  teamLink: string;
  createdAt: string;
  deletedAt: string;

  sessions: StudentFeedbackSession[];
}

/**
 * Instructor courses list page.
 */
@Component({
  selector: 'tm-instructor-courses-page',
  templateUrl: './instructor-courses-page.component.html',
  styleUrls: ['./instructor-courses-page.component.scss'],
})
export class InstructorCoursesPageComponent implements OnInit {

  user: string = '';

  activeCourses: ActiveCourse[] = [];
  archivedCourses: ArchivedCourse[] = [];
  deletedCourses: DeletedCourse[] = [];

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });

    this.getMockCourses();

    //this.getStudentCourses();
  }

  getMockCourses(): void {
    this.activeCourses = ACTIVE_COURSES;
    this.archivedCourses = ARCHIVED_COURSES;
    this.deletedCourses = DELETED_COURSES;
  }

  /**
   * Gets the courses and feedback sessions involving the student.
   */
  getStudentCourses(): void {
    const paramMap: { [key: string]: string } = {
      student: this.user,
    };
    this.httpRequestService.get('/sessions/student', paramMap).subscribe((resp: ActiveCourses) => {
      //TODO: Sort courses and sessions by name and creation time respectively
      this.activeCourses = resp.activeCourses;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
