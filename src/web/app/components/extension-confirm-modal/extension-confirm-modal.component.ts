import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  StudentExtensionTableColumnModel,
  InstructorExtensionTableColumnModel,
} from '../../pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import {
     ColumnData,
     SortableEvent,
     SortableTableCellData,
     SortableTableHeaderColorScheme,
 } from '../sortable-table/sortable-table.component';
import { FormatDateDetailPipe } from '../teammates-common/format-date-detail.pipe';
import { InstructorRoleNamePipe } from '../teammates-common/instructor-role-name.pipe';

export enum ExtensionModalType {
  EXTEND,
  DELETE,
  SESSION_DELETE,
}

@Component({
  selector: 'tm-extension-confirm-modal',
  templateUrl: './extension-confirm-modal.component.html',
  styleUrls: ['./extension-confirm-modal.component.scss'],
})
export class ExtensionConfirmModalComponent implements OnInit {
  @Input()
  modalType: ExtensionModalType = ExtensionModalType.EXTEND;

  @Input()
  selectedStudents: StudentExtensionTableColumnModel[] = [];

  @Input()
  selectedInstructors: InstructorExtensionTableColumnModel[] = [];

  @Input()
  extensionTimestamp: number = 0;

  @Input()
  feedbackSessionTimeZone: string = '';

  @Input()
  headerColorScheme: SortableTableHeaderColorScheme = SortableTableHeaderColorScheme.WHITE;

  @Input() set studentData(studentData: StudentExtensionTableColumnModel[]) {
    this.selectedStudents = studentData;
    if (this.selectedStudents.length > 0) {
      this.setStudentTableData();
    }
  }

  @Input() set instructorData(instructorData: InstructorExtensionTableColumnModel[]) {
      this.selectedInstructors = instructorData;
      if (this.selectedInstructors.length > 0) {
        this.setInstructorTableData();
      }
  }

  @Output()
  confirmExtensionCallbackEvent: EventEmitter<boolean> = new EventEmitter();

  @Output()
  sortStudentListEvent: EventEmitter<SortableEvent> = new EventEmitter();

  @Output()
  sortInstructorListEvent: EventEmitter<SortableEvent> = new EventEmitter();

  studentColumnsData : ColumnData[] = [];
  studentRowsData : SortableTableCellData[][] = [];
  instructorColumnsData : ColumnData[] = [];
  instructorRowsData : SortableTableCellData[][] = [];
  dateDetailPipe = new FormatDateDetailPipe(this.timeZoneService);
  instructorRoleNamePipe = new InstructorRoleNamePipe();

  constructor(public activeModal: NgbActiveModal, private tableComparatorService: TableComparatorService,
  private timeZoneService: TimezoneService) {}

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortStudentsBy: SortBy = SortBy.SESSION_END_DATE;
  sortStudentOrder: SortOrder = SortOrder.DESC;
  sortInstructorsBy: SortBy = SortBy.SESSION_END_DATE;
  sortInstructorOrder: SortOrder = SortOrder.DESC;

  isSubmitting: boolean = false;
  isNotifyDeadlines: boolean = false;

  ngOnInit(): void {
    if (this.selectedStudents.length > 0) {
      this.setStudentTableData();
    }

    if (this.selectedInstructors.length > 0) {
      this.setInstructorTableData();
    }
  }

  setStudentTableData(): void {
    this.setStudentColumnData();
    this.setStudentRowData();
  }

  setInstructorTableData(): void {
    this.setInstructorColumnData();
    this.setInstructorRowData();
  }

  setStudentColumnData(): void {
     this.studentColumnsData = [
        {
            header: 'Section',
            sortBy: SortBy.SECTION_NAME,
            headerClass: 'student-sort-by-section',
        },
        {
            header: 'Team',
            sortBy: SortBy.TEAM_NAME,
            headerClass: 'student-sort-by-team',
        },
        {
            header: 'Name',
            sortBy: SortBy.RESPONDENT_NAME,
            headerClass: 'student-sort-by-name',
        },
        {
            header: 'Email',
            sortBy: SortBy.RESPONDENT_EMAIL,
            headerClass: 'student-sort-by-email',
        },
        {
            header: this.isDeleteModal() || this.isSessionDeleteModal() ? 'Current Deadline' : 'Original Deadline',
            sortBy: SortBy.SESSION_END_DATE,
            headerClass: 'student-sort-by-deadline',
        },
       ];
     }

  setStudentRowData(): void {
    this.studentRowsData = this.selectedStudents
        .map((studentData: StudentExtensionTableColumnModel) => {
            const rowData: SortableTableCellData[] = [
              {
                value: studentData.sectionName,
              },
              {
                value: studentData.teamName,
              },
              {
                value: studentData.name,
              },
              {
                value: studentData.email,
              },
              {
                value: studentData.extensionDeadline,
                displayValue: this.dateDetailPipe.transform(
                studentData.extensionDeadline,
                this.feedbackSessionTimeZone),
              },
            ];
            return rowData;
          });
    }

  setInstructorColumnData(): void {
    this.instructorColumnsData = [
        {
            header: 'Name',
            sortBy: SortBy.RESPONDENT_NAME,
            headerClass: 'instructor-sort-by-name',
        },
        {
            header: 'Email',
            sortBy: SortBy.RESPONDENT_EMAIL,
            headerClass: 'instructor-sort-by-email',
        },
        {
            header: 'Role',
            sortBy: SortBy.INSTRUCTOR_PERMISSION_ROLE,
            headerClass: 'instructor-sort-by-role',
        },
        {
            header: this.isDeleteModal() || this.isSessionDeleteModal() ? 'Current Deadline' : 'Original Deadline',
            sortBy: SortBy.SESSION_END_DATE,
            headerClass: 'instructor-sort-by-deadline',
        },
        ];
    }

  setInstructorRowData(): void {
    this.instructorRowsData = this.selectedInstructors
        .map((instructorData: InstructorExtensionTableColumnModel) => {
            const rowData: SortableTableCellData[] = [
                {
                    value: instructorData.name,
                },
                {
                    value: instructorData.email,
                },
                {
                    value: instructorData.role,
                    displayValue: instructorData.role
                    ? this.instructorRoleNamePipe.transform(instructorData.role)
                    : instructorData.role,
                },
                {
                    value: instructorData.extensionDeadline,
                    displayValue: this.dateDetailPipe.transform(
                    instructorData.extensionDeadline,
                    this.feedbackSessionTimeZone),
                },
            ];
            return rowData;
        });
    }

  onConfirm(): void {
    this.isSubmitting = true;
    this.confirmExtensionCallbackEvent.emit(this.isNotifyDeadlines);
  }

  isDeleteModal(): boolean {
    return this.modalType === ExtensionModalType.DELETE;
  }

  isExtendModal(): boolean {
    return this.modalType === ExtensionModalType.EXTEND;
  }

  isSessionDeleteModal(): boolean {
    return this.modalType === ExtensionModalType.SESSION_DELETE;
  }

  sortStudentColumnsByEventHandler(event: { sortBy: SortBy, sortOrder: SortOrder }): void {
    this.sortStudentListEvent.emit(event);
  }

  getAriaSortStudent(by: SortBy): string {
    if (by !== this.sortStudentsBy) {
      return 'none';
    }
    return this.sortStudentOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  sortStudentPanelsBy(
    by: SortBy,
  ): (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel) => number {
    return (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortStudentOrder, strA, strB);
    };
  }

  sortInstructorsColumnsByEventHandler(event: { sortBy: SortBy, sortOrder: SortOrder }): void {
    this.sortInstructorListEvent.emit(event);
  }

  getAriaSortInstructor(by: SortBy): string {
    if (by !== this.sortInstructorsBy) {
      return 'none';
    }
    return this.sortInstructorOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  sortInstructorPanelsBy(
    by: SortBy,
  ): (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel) => number {
    return (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.INSTRUCTOR_PERMISSION_ROLE:
          strA = a.role || '';
          strB = b.role || '';
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortInstructorOrder, strA, strB);
    };
  }
}
