import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { Router } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../../../services/course.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { JoinState, MessageOutput, Student } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { JoinStatePipe } from './join-state.pipe';

/**
 * Model of row of student data containing details about a student and their section.
 */
export interface StudentListRowModel {
  student: Student;
  photoUrl?: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
}

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
  @Input() isActionButtonsEnabled: boolean = true;
  @Input() students: StudentListRowModel[] = [];

  @Output() removeStudentFromCourseEvent: EventEmitter<string> = new EventEmitter();

  tableSortOrder: SortOrder = SortOrder.ASC;
  tableSortBy: SortBy = SortBy.NONE;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  JoinState: typeof JoinState =  JoinState;

  constructor(private router: Router,
              private statusMessageService: StatusMessageService,
              private navigationService: NavigationService,
              private courseService: CourseService,
              private tableComparatorService: TableComparatorService,
              private simpleModalService: SimpleModalService) {
  }

  ngOnInit(): void {
  }

  /**
   * Returns whether this course are divided into sections
   */
  hasSection(): boolean {
    return (this.students.some((studentModel: StudentListRowModel) =>
        studentModel.student.sectionName !== 'None'));
  }

  /**
   * Function to be passed to ngFor, so that students in the list is tracked by email
   */
  trackByFn(_index: number, item: StudentListRowModel): any {
    return item.student.email;
  }

  /**
   * Open the student email reminder modal.
   */
  openRemindModal(studentModel: StudentListRowModel): void {
    const modalContent: string = `Usually, there is no need to use this feature because TEAMMATES sends an automatic invite to students
          at the opening time of each session. Send a join request to <strong>${ studentModel.student.email }</strong> anyway?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Send join request?', SimpleModalType.INFO, modalContent);
    modalRef.result.then(() => {
      this.remindStudentFromCourse(studentModel.student.email);
    }, () => {});
  }

  /**
   * Open the delete student confirmation modal.
   */
  openDeleteModal(studentModel: StudentListRowModel): void {
    const modalContent: string = `Are you sure you want to remove <strong>${ studentModel.student.name }</strong> from the course <strong>${ this.courseId }?</strong>`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete student <strong>${ studentModel.student.name }</strong>?`, SimpleModalType.DANGER, modalContent);
    modalRef.result.then(() => {
      this.removeStudentFromCourse(studentModel.student.email);
    }, () => {});
  }

  /**
   * Remind the student from course.
   */
  remindStudentFromCourse(studentEmail: string): void {
    this.courseService.remindStudentForJoin(this.courseId, studentEmail)
      .subscribe((resp: MessageOutput) => {
        this.navigationService.navigateWithSuccessMessage(this.router,
            `/web/instructor/courses/details?courseid=${this.courseId}`, resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(studentEmail: string): void {
    this.removeStudentFromCourseEvent.emit(studentEmail);
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
      ((a: StudentListRowModel , b: StudentListRowModel) => number) {
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();

    return (a: StudentListRowModel, b: StudentListRowModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.student.sectionName;
          strB = b.student.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.student.name;
          strB = b.student.name;
          break;
        case SortBy.TEAM_NAME:
          strA = a.student.teamName;
          strB = b.student.teamName;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.student.email;
          strB = b.student.email;
          break;
        case SortBy.JOIN_STATUS:
          strA = joinStatePipe.transform(a.student.joinState);
          strB = joinStatePipe.transform(b.student.joinState);
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(by, this.tableSortOrder, strA, strB);
    };
  }
}
