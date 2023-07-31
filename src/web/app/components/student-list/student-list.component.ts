import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { JoinState, MessageOutput, Student } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import {
    ColumnData,
    SortableEvent,
    SortableTableCellData,
    SortableTableHeaderColorScheme,
} from '../sortable-table/sortable-table.component';
import { StudentListActionsComponent } from './student-list-action-cell.component';
import {
    StudentListColumnData,
    StudentListRowData,
    StudentListColumns,
} from './student-list-model';

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

  @Output() removeStudentFromCourseEvent: EventEmitter<string> = new EventEmitter();
  @Output() sortStudentListEvent: EventEmitter<SortableEvent> = new EventEmitter();

  rowData: SortableTableCellData[][] = [];
  columnData: ColumnData[] = [];

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  JoinState: typeof JoinState = JoinState;
  StudentListColumns: typeof StudentListColumns = StudentListColumns;
  SortableTableHeaderColorScheme: typeof SortableTableHeaderColorScheme = SortableTableHeaderColorScheme;

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

  ngOnInit(): void {
    this.setColumnData();
    this.setRowData();
    this.setHeaderStyle();
  }

  setHeaderStyle(): void {
    this.customHeaderStyle = this.useGrayHeading ? 'bg-light' : 'bg-info';
  }

  createColumnData(config: StudentListColumnData): ColumnData[] {

    const columnData: ColumnData = {
      header: config.header,
      ...(config.sortBy && { sortBy: config.sortBy }),
      ...(config.headerToolTip && { headerToolTip: config.headerToolTip }),
      ...(config.alignment && { alignment: config.alignment }),
      ...(config.headerClass && { headerClass: config.headerClass }),
    };

    return [columnData];
  }

  setColumnData(): void {
    this.columnData = [
        ...this.createColumnData({
            columnType: StudentListColumns.SECTION,
            header: 'Section',
            sortBy: SortBy.SECTION_NAME,
        }),
        ...this.createColumnData({
            columnType: StudentListColumns.TEAM,
            header: 'Team',
            sortBy: SortBy.TEAM_NAME,
        }),
        ...this.createColumnData({
            columnType: StudentListColumns.STUDENT_NAME,
            header: 'Student Name',
            sortBy: SortBy.RESPONDENT_NAME,
        }),
        ...this.createColumnData({
            columnType: StudentListColumns.STATUS,
            header: 'Status',
            sortBy: SortBy.JOIN_STATUS,
        }),
        ...this.createColumnData({
            columnType: StudentListColumns.EMAIL,
            header: 'Email',
            sortBy: SortBy.RESPONDENT_EMAIL,
        }),
        ...this.createColumnData({ columnType: StudentListColumns.ACTIONS, header: 'Action(s)', alignment: 'center' }),
    ];
  }

  createRowData(config: StudentListRowData): SortableTableCellData[] {
  
    const rowData: SortableTableCellData = {
      ...(config.value && { value: config.value }),
      ...(config.displayValue && { displayValue: config.displayValue }),
      ...(config.customComponent && { customComponent: config.customComponent }),
      ...(config.style && { style: config.style }),
    };

    return [rowData];
  }

  setRowData(): void {
    this.rowData = this.students.map((studentModel: StudentListRowModel) => {
      const rowData: SortableTableCellData[] = [
        ...this.createRowData({
          columnType: StudentListColumns.SECTION,
          value: studentModel.student.sectionName,
        }),
        ...this.createRowData({
          columnType: StudentListColumns.TEAM,
          value: studentModel.student.teamName,
        }),
        ...this.createRowData({
          columnType: StudentListColumns.STUDENT_NAME,
          value: studentModel.student.name,
        }),
        ...this.createRowData({
          columnType: StudentListColumns.STATUS,
          value: studentModel.student.joinState === JoinState.JOINED ? 'Joined' : 'Yet to Join',
        }),
        ...this.createRowData({
          columnType: StudentListColumns.EMAIL,
          value: studentModel.student.email,
        }),
        ...this.createRowData(this.createActionsCell(studentModel)),
      ];

      return rowData;
    });
  }

  createActionsCell(studentModel: StudentListRowModel): SortableTableCellData {
    const actionsCell: StudentListRowData = {
      columnType: StudentListColumns.ACTIONS,
      customComponent: {
      component: StudentListActionsComponent,
      componentData: (idx: number) => ({
        idx,
        courseId: this.courseId,
        email: studentModel.student.email,
        hasJoined: studentModel.student.joinState === JoinState.JOINED,
        removeStudentFromCourse: () => this.openDeleteModal(studentModel),
        remindStudentFromCourse: () => this.openRemindModal(studentModel),
        instructorPrivileges: {
            canModifyStudent: studentModel.isAllowedToModifyStudent,
            canViewStudentInSections: studentModel.isAllowedToViewStudentInSection,
        },
        enableRemindButton: studentModel.student.joinState === JoinState.NOT_JOINED,
        isActionButtonsEnabled: this.isActionButtonsEnabled,
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

  getAriaSort(by: SortBy): String {
    if (by !== this.tableSortBy) {
      return 'none';
    }
    return this.tableSortOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }
}
