import { Component, DoCheck, Input, IterableDiffer, IterableDiffers, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { JoinState, MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { JoinStatePipe } from './join-state.pipe';
import {
  StudentListSectionData,
  StudentListStudentData,
} from './student-list-section-data';

/**
 * Flattened data which contains details about a student and their section.
 * The data is flattened to allow sorting of the table.
 */
interface FlatStudentListData {
  name: string;
  email: string;
  status: JoinState;
  team: string;
  photoUrl?: string;
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
}

/**
 * Sort criteria for the students table.
 */
enum SortBy {
  /**
   * Nothing.
   */
  NONE,

  /**
   * Section Name.
   */
  SECTION_NAME,

  /**
   * Team name.
   */
  TEAM_NAME,

  /**
   * Student Name.
   */
  STUDENT_NAME,

  /**
   * Status.
   */
  STATUS,

  /**
   * Email.
   */
  EMAIL,
}

/**
 * Sort order for the students table.
 */
enum SortOrder {
  /**
   * Descending sort order.
   */
  DESC,

  /**
   * Ascending sort order
   */
  ASC,
}

/**
 * A table displaying a list of students from a course, with buttons to view/edit/delete students etc.
 */
@Component({
  selector: 'tm-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss'],
})
export class StudentListComponent implements OnInit, DoCheck {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() listOfStudentsToHide: string[] = [];
  @Input() isHideTableHead: boolean = false;
  @Input() enableRemindButton: boolean = false;

  // The input sections data from parent.
  @Input() sections: StudentListSectionData[] = [];

  // The flattened students list derived from the sections list.
  // The sections data is flattened to allow sorting of the list.
  students: FlatStudentListData[] = [];
  tableSortOrder: SortOrder = SortOrder.ASC;
  tableSortBy: SortBy = SortBy.NONE;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  JoinState: typeof JoinState =  JoinState;

  private readonly _differ: IterableDiffer<any>;

  constructor(private router: Router,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private navigationService: NavigationService,
              private courseService: CourseService,
              private ngbModal: NgbModal,
              private differs: IterableDiffers) {
    this._differ = this.differs.find(this.sections).create();
  }

  ngOnInit(): void {
  }

  ngDoCheck(): void {
    if (this._differ) {
      const changes: any = this._differ.diff(this.sections);
      if (changes) {
        this.students = this.mapStudentsFromSectionData(this.sections);
      }
    }
  }

  /**
   * Flatten section data.
   */
  mapStudentsFromSectionData(sections: StudentListSectionData[]): FlatStudentListData[] {
    const students: FlatStudentListData[] = [];
    sections.forEach((section: StudentListSectionData) =>
        section.students.forEach((student: StudentListStudentData) =>
            students.push({
              name: student.name,
              email: student.email,
              status: student.status,
              team: student.team,
              photoUrl: student.photoUrl,
              sectionName: section.sectionName,
              isAllowedToModifyStudent: section.isAllowedToModifyStudent,
              isAllowedToViewStudentInSection: section.isAllowedToViewStudentInSection,
            }),
        ),
    );
    return students;
  }

  /**
   * Returns whether this course are divided into sections
   */
  hasSection(): boolean {
    return (this.students.some((student: FlatStudentListData) =>
        student.sectionName !== 'None'));
  }

  /**
   * Function to be passed to ngFor, so that students in the list is tracked by email
   */
  trackByFn(_index: number, item: FlatStudentListData): any {
    return item.email;
  }

  /**
   * Load the profile picture of a student
   */
  loadPhoto(student: FlatStudentListData): void {
    student.photoUrl =
        `${environment.backendUrl}/webapi/student/profilePic?courseid=${this.courseId}&studentemail=${student.email}`;
  }

  /**
   * Sets the profile picture of a student as the default image
   */
  setDefaultPic(student: FlatStudentListData): void {
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
      ((a: FlatStudentListData , b: FlatStudentListData) => number) {
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();

    return (a: FlatStudentListData, b: FlatStudentListData): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
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
          strA = joinStatePipe.transform(a.status);
          strB = joinStatePipe.transform(b.status);
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
