import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { JoinState, MessageOutput, Student } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

/**
 * Model of row of student data containing details about a student and their section.
 */
export interface StudentListRowModel {
  student: Student;
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
export class StudentListComponent {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() listOfStudentsToHide: string[] = [];
  @Input() isHideTableHead: boolean = false;
  @Input() enableRemindButton: boolean = false;
  @Input() isActionButtonsEnabled: boolean = true;
  @Input() students: StudentListRowModel[] = [];
  @Input() tableSortBy: SortBy = SortBy.NONE;
  @Input() tableSortOrder: SortOrder = SortOrder.ASC;
  @Input() searchString: string = '';

  @Output() removeStudentFromCourseEvent: EventEmitter<string> = new EventEmitter();
  @Output() sortStudentListEvent: EventEmitter<SortBy> = new EventEmitter();

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  JoinState: typeof JoinState = JoinState;

  constructor(private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private simpleModalService: SimpleModalService) {
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
    const modalContent: string = `Usually, there is no need to use this feature because
          TEAMMATES sends an automatic invite to students at the opening time of each session.
          Send a join request to <strong>${studentModel.student.email}</strong> anyway?`;
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
    const modalContent: string = `Are you sure you want to remove <strong>${studentModel.student.name}</strong> `
        + `from the course <strong>${this.courseId}?</strong>`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete student <strong>${studentModel.student.name}</strong>?`, SimpleModalType.DANGER, modalContent);
    modalRef.result.then(() => {
      this.removeStudentFromCourse(studentModel.student.email);
    }, () => {});
  }

  /**
   * Remind the student from course.
   */
  remindStudentFromCourse(studentEmail: string): void {
    this.courseService.remindStudentForJoin(this.courseId, studentEmail)
      .subscribe({
        next: (resp: MessageOutput) => {
          this.statusMessageService.showSuccessToast(resp.message);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
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
  sortStudentList(by: SortBy): void {
    this.sortStudentListEvent.emit(by);
  }

  getAriaSort(by: SortBy): String {
    if (by !== this.tableSortBy) {
      return 'none';
    }
    return this.tableSortOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }
}
