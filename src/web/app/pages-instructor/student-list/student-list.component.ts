import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { SortBy, SortOrder, StudentListSectionData, StudentListStudentData } from './student-list-section-data';

/**
 * A table displaying a list of students from a course, with buttons to view/edit/delete students etc.
 */
@Component({
  selector: 'tm-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss'],
})
export class StudentListComponent implements OnInit {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() listOfStudentsToHide: string[] = [];
  @Input() isHideTableHead: boolean = false;
  @Input() enableRemindButton: boolean = false;

  students: StudentListStudentData[] = [];
  tableSortOrder: SortOrder = SortOrder.ASC;
  tableSortBy: SortBy = SortBy.NONE;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  constructor(private router: Router,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private navigationService: NavigationService,
              private courseService: CourseService,
              private ngbModal: NgbModal) { }

  ngOnInit(): void {
  }

  @Input()
  set sections(sections: StudentListSectionData[]) {
    this.students = this.mapStudentsFromSectionData(sections);
  }

  /**
   * Flatten section data.
   */
  mapStudentsFromSectionData(sections: StudentListSectionData[]): StudentListStudentData[] {
    const students: StudentListStudentData[] = [];
    sections.forEach((section: StudentListSectionData) =>
        section.students.forEach((student: StudentListStudentData) =>
            students.push({
              section,
              name: student.name,
              email: student.email,
              status: student.status,
              team: student.team,
              photoUrl: student.photoUrl,
            }),
        ),
    );
    return students;
  }

  /**
   * Returns whether this course are divided into sections
   */
  hasSection(): boolean {
    return (this.students.some((student: StudentListStudentData) =>
        student.section ? student.section.sectionName !== 'None' : false));
  }

  /**
   * Function to be passed to ngFor, so that students in the list is tracked by email
   */
  trackByFn(_index: number, item: StudentListStudentData): any {
    return item.email;
  }

  /**
   * Load the profile picture of a student
   */
  loadPhoto(student: StudentListStudentData): void {
    student.photoUrl =
        `${environment.backendUrl}/webapi/student/profilePic?courseid=${this.courseId}&studentemail=${student.email}`;
  }

  /**
   * Sets the profile picture of a student as the default image
   */
  setDefaultPic(student: StudentListStudentData): void {
    student.photoUrl = '/assets/images/profile_picture_default.png';
  }

  /**
   * Open the student delete confirmation modal.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

  /**
   * Remind the student from course.
   */
  remindStudentFromCourse(studentEmail: string): void {
    this.courseService.remindStudentForJoin(this.courseId, studentEmail)
      .subscribe((resp: MessageOutput) => {
        this.navigationService.navigateWithSuccessMessagePreservingParams(this.router,
            '/web/instructor/courses/details', resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(studentEmail: string): void {
    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      studentemail: studentEmail,
    };
    this.httpRequestService.delete('/student', paramMap).subscribe(() => {
      this.statusMessageService.showSuccessMessage(`Student is successfully deleted from course "${this.courseId}"`);
      this.sections.forEach(
        (section: StudentListSectionData) => {
          section.students = section.students.filter(
            (student: StudentListStudentData) => student.email !== studentEmail);
        });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Determines which row in the studentTable should be hidden.
   */
  isStudentToHide(studentEmail: string): boolean {
    return this.listOfStudentsToHide.indexOf(studentEmail) > -1;
  }

  /**
   * Sorts the student list
   */
  sortStudentListEvent(by: SortBy): void {
    this.tableSortBy = by;
    this.tableSortOrder =
        this.tableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.students.sort(this.sortBy(by));
  }

  /**
   * Returns a function to determine the order of sort
   */
  sortBy(by: SortBy):
      ((a: StudentListStudentData , b: StudentListStudentData) => number) {
    return (a: StudentListStudentData, b: StudentListStudentData): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.section ? a.section.sectionName : '';
          strB = b.section ? b.section.sectionName : '';
          break;
        case SortBy.STUDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.TEAM_NAME:
          strA = a.team;
          strB = b.team;
          break;
        case SortBy.EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.STATUS:
          strA = a.status;
          strB = b.status;
          break;
        default:
          strA = '';
          strB = '';
      }

      if (this.tableSortOrder === SortOrder.ASC) {
        return strA.localeCompare(strB);
      }
      if (this.tableSortOrder === SortOrder.DESC) {
        return strB.localeCompare(strA);
      }

      return 0;
    };
  }
}
