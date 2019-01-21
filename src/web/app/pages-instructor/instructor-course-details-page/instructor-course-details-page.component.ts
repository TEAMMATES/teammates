import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { ClipboardService } from 'ngx-clipboard';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput, MessageOutput } from '../../message-output';
import { StudentListSectionData } from '../student-list/student-list-section-data';

interface CourseAttributes {
  id: string;
  name: string;
}

interface CourseStats {
  sectionsTotal: string;
  teamsTotal: string;
  studentsTotal: string;
}

interface CourseDetailsBundle {
  course: CourseAttributes;
  stats: CourseStats;
}

interface InstructorAttributes {
  googleId: string;
  name: string;
  email: string;
  key: string;
  role: string;
  displayedName: string;
  isArchived: boolean;
  isDisplayedToStudents: boolean;
}

interface CourseInfo {
  courseDetails: CourseDetailsBundle;
  currentInstructor: InstructorAttributes;
  instructors: InstructorAttributes[];
  sections: StudentListSectionData[];
  hasSection: boolean;
  studentListHtmlTableAsString: string;
}

interface RedirectInfo {
  redirectUrl: string;
  statusMessage: string;
}

/**
 * Instructor course details page.
 */
@Component({
  selector: 'tm-instructor-course-details-page',
  templateUrl: './instructor-course-details-page.component.html',
  styleUrls: ['./instructor-course-details-page.component.scss'],
})
export class InstructorCourseDetailsPageComponent implements OnInit {

  user: string = '';
  courseDetails?: CourseDetailsBundle;
  currentInstructor?: InstructorAttributes;
  instructors?: InstructorAttributes[] = [];
  sections?: StudentListSectionData[] = [];
  studentListHtmlTableAsString?: String = '';

  constructor(private route: ActivatedRoute, private router: Router,
              private clipboardService: ClipboardService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private ngbModal: NgbModal, private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadCourseDetails(queryParams.courseid);
    });
  }

  /**
   * Loads the course's details based on the given course ID and email.
   */
  loadCourseDetails(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/courses/details', paramMap).subscribe((resp: CourseInfo) => {
      this.courseDetails = resp.courseDetails;
      this.currentInstructor = resp.currentInstructor;
      this.instructors = resp.instructors;
      this.sections = resp.sections;
      this.studentListHtmlTableAsString = resp.studentListHtmlTableAsString;

      if (!this.courseDetails) {
        this.statusMessageService.showErrorMessage('Error retrieving course details');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Automatically copy the text content provided.
   */
  copyContent(text: string): void {
    this.clipboardService.copyFromContent(text);
  }

  /**
   * Open the modal for different buttons and link.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

  /**
   * Delete all the students in a course.
   */
  deleteAllStudentsFromCourse(courseId: string): void {
    const paramsMap: { [key: string]: string } = {
      user: this.user,
      courseid: courseId,
    };

    this.httpRequestService.delete('/courses/details/deleteAllStudents', paramsMap)
      .subscribe((resp: MessageOutput) => {
        this.navigationService.navigateWithSuccessMessage(this.router,
            `/web/instructor/courses/details?courseid=${courseId}`,
            resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }

  /**
   * Download all the students from a course.
   */
  downloadAllStudentsFromCourse(courseId: string): void {
    const paramsMap: { [key: string]: string } = { courseid: courseId };
    this.httpRequestService.get('/courses/details/downloadAllStudents', paramsMap, 'text')
      .subscribe((resp: string) => {
        const filename: string = `${courseId.concat('_studentList')}.csv`;
        const blob: any = new Blob([resp], { type: 'text/csv' });
        saveAs(blob, filename);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }

  /**
   * Remind all unjoined students in a course.
   */
  remindAllStudentsFromCourse(courseId: string): void {
    const paramsMap: { [key: string]: string } = {
      user: this.user,
      courseid: courseId,
    };

    this.httpRequestService.post('/courses/details/remindAllStudents', paramsMap)
      .subscribe((resp: RedirectInfo) => {
        this.navigationService.navigateWithSuccessMessage(this.router, resp.redirectUrl, resp.statusMessage);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }
}
