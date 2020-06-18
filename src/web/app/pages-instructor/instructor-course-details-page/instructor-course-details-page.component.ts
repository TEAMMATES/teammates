import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { ClipboardService } from 'ngx-clipboard';
import { CourseService, CourseStatistics } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  Course,
  Instructor,
  InstructorPrivilege,
  Instructors,
  MessageOutput,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';

interface CourseDetailsBundle {
  course: Course;
  stats: CourseStatistics;
}

interface StudentIndexedData {
  [key: string]: Student[];
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

  courseDetails: CourseDetailsBundle = {
    course: {
      courseId: '',
      courseName: '',
      timeZone: '',
      creationTimestamp: 0,
      deletionTimestamp: 0,
    },
    stats: {
      numOfSections: 0,
      numOfTeams: 0,
      numOfStudents: 0,
    },
  };
  instructors: Instructor[] = [];
  students: StudentListRowModel[] = [];
  courseStudentListAsCsv: string = '';

  loading: boolean = false;
  isAjaxSuccess: boolean = true;

  constructor(private route: ActivatedRoute, private router: Router,
              private clipboardService: ClipboardService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private ngbModal: NgbModal, private navigationService: NavigationService,
              private studentService: StudentService,
              private instructorService: InstructorService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadCourseDetails(queryParams.courseid);
    });
  }

  /**
   * Loads the course's details based on the given course ID.
   */
  loadCourseDetails(courseid: string): void {
    this.students = [];
    this.loadCourseName(courseid);
    this.loadInstructors(courseid);
    this.loadStudents(courseid);
  }

  /**
   * Loads the name of the course
   */
  private loadCourseName(courseid: string): void {
    this.courseService.getCourseAsInstructor(courseid).subscribe((course: Course) => {
      this.courseDetails.course = course;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads the instructors in the course
   */
  private loadInstructors(courseid: string): void {
    this.instructorService.loadInstructors({ courseId: courseid, intent: Intent.FULL_DETAIL })
    .subscribe((instructors: Instructors) => {
      this.instructors = instructors.instructors;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads the students in the course
   */
  private loadStudents(courseid: string): void {
    this.studentService.getStudentsFromCourse({ courseId: courseid }).subscribe((students: Students) => {
      const sections: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
        const term: string = x.sectionName;
        (acc[term] = acc[term] || []).push(x);
        return acc;
      }, {});

      Object.keys(sections).forEach((key: string) => {
        const studentsInSection: Student[] = sections[key];

        const data: StudentListRowModel[] = [];
        studentsInSection.forEach((student: Student) => {
          const studentData: StudentListRowModel = {
            name: student.name,
            status: student.joinState,
            email: student.email,
            team: student.teamName,
            sectionName: key,
            isAllowedToViewStudentInSection: false,
            isAllowedToModifyStudent: false,
          };
          data.push(studentData);
        });

        this.loadPrivilege(courseid, key, data);
      });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  private loadPrivilege(courseid: string, sectionName: string, students: StudentListRowModel[]): void {
    this.instructorService.loadInstructorPrivilege({
      sectionName,
      courseId: courseid,
    }).subscribe((instructorPrivilege: InstructorPrivilege) => {
      students.forEach((student: StudentListRowModel) => {
        student.isAllowedToViewStudentInSection = instructorPrivilege.canViewStudentInSections;
        student.isAllowedToModifyStudent = instructorPrivilege.canModifyStudent;
      });

      this.students.push(...students);
      this.courseDetails.stats = this.statisticsCalculatorService.calculateCourseStatistics(this.students);
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
    this.studentService.deleteAllStudentsFromCourse({ courseId })
      .subscribe((resp: MessageOutput) => {
        this.students = [];
        this.courseDetails.stats = this.statisticsCalculatorService.calculateCourseStatistics(this.students);
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

    // Calling REST API only the first time to load the downloadable data
    if (this.loading) {
      blob = new Blob([this.courseStudentListAsCsv], { type: 'text/csv' });
      saveAs(blob, filename);
    } else {
      this.studentService.loadStudentListAsCsv({ courseId })
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

    this.studentService.loadStudentListAsCsv({ courseId })
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

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(studentEmail: string): void {
    this.courseService.removeStudentFromCourse(this.courseDetails.course.courseId, studentEmail).subscribe(() => {
      this.statusMessageService
          .showSuccessMessage(`Student is successfully deleted from course "${this.courseDetails.course.courseId}"`);
      this.students = this.students.filter((student: StudentListRowModel) => student.email !== studentEmail);
      this.courseDetails.stats = this.statisticsCalculatorService.calculateCourseStatistics(this.students);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
