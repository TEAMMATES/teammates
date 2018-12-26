import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from "../../../services/http-request.service";
import { StatusMessageService } from "../../../services/status-message.service";
import { ErrorMessageOutput } from "../../message-output";

/**
 * Mock courses to test the UI.
 */
const COURSES: StudentCourse[]= [
  { id: 'test.exa-demo', name: 'Sample Course 101', teamLink: '#',
    sessions: [
      { name: 'First team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: false, isOpened: false, isWaitingToOpen: true,
        isSessionVisible: true, isSessionPublished: false,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Second team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: true, isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Third team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: true, isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Fourth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: false, isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: true,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
      { name: 'Fifth team feedback session', deadline: 'Mon, 02 Apr 2012, 11:59 PM SGT',
        isSubmitted: false, isOpened: true, isWaitingToOpen: false,
        isSessionVisible: true, isSessionPublished: false,
        studentFeedbackResultsLink: '#', studentFeedbackResponseEditLink: '#'},
    ]},
  { id: 'CS3244', name: 'Sample Course 103', teamLink: '#',
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

interface StudentCourse {
  id: string;
  name: string;
  teamLink: string;

  sessions: StudentFeedbackSession[];
}

interface StudentCourses {
  courses: StudentCourse[];
}

/**
 * Student home page.
 */
@Component({
  selector: 'tm-student-home-page',
  templateUrl: './student-home-page.component.html',
  styleUrls: ['./student-home-page.component.scss'],
})
export class StudentHomePageComponent {

  user: string = '';

  courses: StudentCourse[] = [];

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
    this.courses = COURSES;
  }

  /**
   * Gets the courses and feedback sessions involving the student.
   */
  getStudentCourses(): void {
    const paramMap: { [key: string]: string } = {
      student: this.user,
    };
    this.httpRequestService.get('/sessions/student', paramMap).subscribe((resp: StudentCourses) => {
      //TODO: Sort courses and sessions by name and creation time respectively
      this.courses = resp.courses;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
