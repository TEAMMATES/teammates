import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CellWithActionsComponent } from './cell-with-actions.component';
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { JoinState, MessageOutput, Student } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';
import { SearchTermsHighlighterPipe } from '../../pipes/search-terms-highlighter.pipe';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import {
    ColumnData,
    SortableEvent,
    SortableTableCellData,
    SortableTableHeaderColorScheme,
} from '../sortable-table/sortable-table.component';

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
export class StudentListComponent implements OnInit {
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
  @Input() headerColorScheme: SortableTableHeaderColorScheme = SortableTableHeaderColorScheme.OTHERS;
  @Input() customHeaderStyle: string = 'bg-light';

  @Input() set studentModels(studentRowModels: StudentListRowModel[]) {
    this.students = studentRowModels;
    this.setRowData();
  }

  @Input() set hiddenStudents(hiddenStudents: string[]) {
    this.listOfStudentsToHide = hiddenStudents;
    this.setRowData();
  }

  @Output() removeStudentFromCourseEvent: EventEmitter<string> = new EventEmitter();
  @Output() sortStudentListEvent: EventEmitter<SortableEvent> = new EventEmitter();

  rowsData: SortableTableCellData[][] = [];
  columnsData: ColumnData[] = [];

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  JoinState: typeof JoinState = JoinState;
  SortableTableHeaderColorScheme: typeof SortableTableHeaderColorScheme = SortableTableHeaderColorScheme;

  constructor(private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private simpleModalService: SimpleModalService,
              private searchTermsHighlighterPipe: SearchTermsHighlighterPipe) {
  }

  /**
   * Returns whether this course are divided into sections
   */
  hasSection(): boolean {
    return (this.students.some((studentModel: StudentListRowModel) =>
        studentModel.student.sectionName !== 'None'));
  }

  ngOnInit(): void {
    this.setRowData();
    this.setColumnData();
    this.setHeaderStyle();
  }

  setHeaderStyle(): void {
    this.customHeaderStyle = this.useGrayHeading ? 'bg-light' : 'bg-info';
  }

  setColumnData(): void {
    this.columnsData = [
        {
            header: 'Section',
            sortBy: SortBy.SECTION_NAME,
            headerClass: 'sort-by-section',
        },
        {
            header: 'Team',
            sortBy: SortBy.TEAM_NAME,
            headerClass: 'sort-by-team',
        },
        {
            header: 'Student Name',
            sortBy: SortBy.RESPONDENT_NAME,
            headerClass: 'sort-by-name',
        },
        {
            header: 'Status',
            sortBy: SortBy.JOIN_STATUS,
            headerClass: 'sort-by-status',
        },
        {
            header: 'Email',
            sortBy: SortBy.RESPONDENT_EMAIL,
            headerClass: 'sort-by-email',

        },
        {
            header: 'Action(s)',
            alignment: 'center',
        },
    ];
  }

  setRowData(): void {
    this.rowsData = this.students
    .filter((studentModel: StudentListRowModel) => !this.isStudentToHide(studentModel.student.email))
    .map((studentModel: StudentListRowModel) => {
      const rowData: SortableTableCellData[] = [
        {
          value: studentModel.student.sectionName,
          displayValue: this.searchTermsHighlighterPipe.transform(studentModel.student.sectionName, this.searchString),
        },
        {
          value: studentModel.student.teamName,
          displayValue: this.searchTermsHighlighterPipe.transform(studentModel.student.teamName, this.searchString),
        },
        {
          value: studentModel.student.name,
          displayValue: this.searchTermsHighlighterPipe.transform(studentModel.student.name, this.searchString),
        },
        {
          value: studentModel.student.joinState === JoinState.JOINED ? 'Joined' : 'Yet to Join',
        },
        {
          value: studentModel.student.email,
          displayValue: this.searchTermsHighlighterPipe.transform(studentModel.student.email, this.searchString),
        },
        this.createActionsCell(studentModel),
      ];

      return rowData;
    });
  }

  createActionsCell(studentModel: StudentListRowModel): SortableTableCellData {
    const actionsCell: SortableTableCellData = {
      customComponent: {
      component: CellWithActionsComponent,
      componentData: (idx: number) => ({
        idx,
        courseId: this.courseId,
        email: studentModel.student.email,
        enableRemindButton: studentModel.student.joinState === JoinState.NOT_JOINED,
        instructorPrivileges: {
            canModifyStudent: studentModel.isAllowedToModifyStudent,
            canViewStudentInSections: studentModel.isAllowedToViewStudentInSection,
        },
        isActionButtonsEnabled: this.isActionButtonsEnabled,
        removeStudentFromCourse: () => this.openDeleteModal(studentModel),
        remindStudentFromCourse: () => this.openRemindModal(studentModel),
      }),
      },
    };

    return actionsCell;
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
      this.students = this.students.filter((student: StudentListRowModel) =>
      student.student.email !== studentModel.student.email);
      this.setRowData();
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
  sortStudentListEventHandler(event: { sortBy: SortBy, sortOrder: SortOrder }): void {
    this.sortStudentListEvent.emit(event);
  }
}
