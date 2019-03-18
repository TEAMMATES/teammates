import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { ClipboardService } from 'ngx-clipboard';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListSectionData } from '../student-list/student-list-section-data';

interface CourseAttributes {
  id: string;
  name: string;
}

interface CourseStats {
  sectionsTotal: number;
  teamsTotal: number;
  studentsTotal: number;
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
  instructors: InstructorAttributes[] = [];
  sections: StudentListSectionData[] = [];
  courseStudentListAsCsv: string = '';

  loading: boolean = false;
  isAjaxSuccess: boolean = true;

  constructor(private route: ActivatedRoute, private router: Router,
              private clipboardService: ClipboardService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
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
    this.httpRequestService.delete('/students', paramsMap)
      .subscribe((resp: MessageOutput) => {
        this.loadCourseDetails(courseId);
        this.statusMessageService.showSuccessMessage(resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }

  /**
   * Download all the students from a course.
   */
  downloadAllStudentsFromCourse(courseId: string): void {
    const filename: string = `${courseId.concat('_studentList')}.csv`;
    let blob: any;

    // Calling REST API only the first time to laod the downloadable data
    if (this.loading) {
      blob = new Blob([this.courseStudentListAsCsv], { type: 'text/csv' });
      saveAs(blob, filename);
    } else {

      const paramsMap: { [key: string]: string } = {
        user: this.user,
        courseid: courseId,
      };
      this.httpRequestService.get('/students/csv', paramsMap, 'text')
        .subscribe((resp: string) => {
          blob = new Blob([resp], { type: 'text/csv' });
          saveAs(blob, filename);
          this.courseStudentListAsCsv = resp;
          this.loading = false;
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
    }
  }

  /**
   * Load the student list in csv table format
   */
  loadStudentsListCsv(courseId: string): void {
    this.loading = true;

    // Calls the REST API once only when student list is not loaded
    if (this.courseStudentListAsCsv !== '') {
      this.loading = false;
      return;
    }

    const paramsMap: { [key: string]: string } = {
      user: this.user,
      courseid: courseId,
    };
    this.httpRequestService.get('/students/csv', paramsMap, 'text')
      .subscribe((resp: string) => {
        this.courseStudentListAsCsv = resp;
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
        this.isAjaxSuccess = false;
      });
    this.loading = false;
  }

  /**
   * Remind all yet to join students in a course.
   */
  remindAllStudentsFromCourse(courseId: string): void {
    this.courseService.remindUnregisteredStudentsForJoin(courseId).subscribe((resp: MessageOutput) => {
      this.navigationService.navigateWithSuccessMessagePreservingParams(this.router,
        '/web/instructor/courses/details', resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Converts a csv string to a html table string for displaying.
   */
  convertToHtmlTable(str: string): string {
    let result: string = '<table class=\"table table-bordered table-striped table-sm\">';
    let rowData: string[];
    const lines: string[] = str.split(/\r?\n/);

    lines.forEach(
        (line: string) => {
          rowData = this.getTableData(line);

          if (rowData.filter((s: string) => s !== '').length === 0) {
            return;
          }
          result = result.concat('<tr>');
          for (const td of rowData) {
            result = result.concat(`<td>${td}</td>`);
          }
          result = result.concat('</tr>');
        },
    );
    return result.concat('</table>');
  }

  /**
   * Obtain a string without quotations.
   */
  getTableData(line: string): string[] {
    const output: string[] = [];
    let inquote: boolean = false;

    let buffer: string = '';
    const data: string[] = line.split('');

    for (let i: number = 0; i < data.length; i += 1) {
      if (data[i] === '"') {
        if (i + 1 < data.length && data[i + 1] === '"') {
          i += 1;
        } else {
          inquote = !inquote;
          continue;
        }
      }

      if (data[i] === ',') {
        if (inquote) {
          buffer = buffer.concat(data[i]);
        } else {
          output.push(buffer);
          buffer = '';
        }
      } else {
        buffer = buffer.concat(data[i]);
      }
    }
    output.push(buffer.trim());
    return output;
  }
}
