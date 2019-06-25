import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstructorListInfoTableRowModel, StudentListInfoTableRowModel } from './respondent-list-info-table-model';

/**
 * Sort criteria for the respondent list info table.
 */
enum SortBy {

  /**
   * Nothing.
   */
  NONE,

  /**
   * The name of the student's section.
   */
  SECTION_NAME,

  /**
   * The name of the student's team.
   */
  TEAM_NAME,

  /**
   * The name of the respondent.
   */
  RESPONDENT_NAME,

  /**
   * The email of the respondent.
   */
  RESPONDENT_EMAIL,

  /**
   * The status of the respondent's feedback submission.
   */
  HAS_SUBMITTED_SESSION,
}

/**
 * Sort order for the sessions table.
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
 * Student list for users to make selection.
 */
@Component({
  selector: 'tm-respondent-list-info-table',
  templateUrl: './respondent-list-info-table.component.html',
  styleUrls: ['./respondent-list-info-table.component.scss'],
})
export class RespondentListInfoTableComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  shouldDisplayHasSubmittedSessionColumn: boolean = false;

  @Input()
  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];

  @Input()
  instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [];

  @Output()
  studentListInfoTableRowModelsChange: EventEmitter<StudentListInfoTableRowModel[]> = new EventEmitter();

  @Output()
  instructorListInfoTableRowModelsChange: EventEmitter<InstructorListInfoTableRowModel[]> = new EventEmitter();

  studentListInfoTableSortBy: SortBy = SortBy.NONE;
  studentListInfoTableSortOrder: SortOrder = SortOrder.ASC;

  instructorListInfoTableSortBy: SortBy = SortBy.NONE;
  instructorListInfoTableSortOrder: SortOrder = SortOrder.ASC;

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Sorts the students according to selection option.
   */
  sortStudentsTableRows(by: SortBy): void {
    // reverse the sort order
    this.studentListInfoTableSortOrder =
        this.studentListInfoTableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;

    this.studentListInfoTableRowModelsChange.emit(
        this.studentListInfoTableRowModels.map((oldModel: StudentListInfoTableRowModel) => Object.assign({}, oldModel))
            .sort(this.sortRowsBy(by, this.studentListInfoTableSortOrder)),
    );
  }

  /**
   * Sorts the instructors according to selection option.
   */
  sortInstructorsTableRows(by: SortBy): void {
    // reverse the sort order
    this.instructorListInfoTableSortOrder =
        this.instructorListInfoTableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;

    this.instructorListInfoTableRowModelsChange.emit(
        this.instructorListInfoTableRowModels.map(
            (oldModel: InstructorListInfoTableRowModel) => Object.assign({}, oldModel))
            .sort(this.sortRowsBy(by, this.instructorListInfoTableSortOrder)),
    );
  }

  /**
   * Handles the event when a row of the table is selected.
   */
  handleSelectionOfRow(model: any): void {
    if (model.teamName) {
      // model is StudentListInfoTableRowModel
      this.studentListInfoTableRowModelsChange.emit(
          this.studentListInfoTableRowModels.map((oldModel: StudentListInfoTableRowModel) => {
            if (oldModel === model) {
              return Object.assign({}, oldModel, {
                isSelected: !oldModel.isSelected,
              });
            }
            return Object.assign({}, oldModel);
          }),
      );
    } else {
      // model is InstructorListInfoTableRowModel
      this.instructorListInfoTableRowModelsChange.emit(
          this.instructorListInfoTableRowModels.map((oldModel: InstructorListInfoTableRowModel) => {
            if (oldModel === model) {
              return Object.assign({}, oldModel, {
                isSelected: !oldModel.isSelected,
              });
            }
            return Object.assign({}, oldModel);
          }),
      );
    }
  }

  /**
   * Sorts the rows of students in order.
   */
  sortRowsBy(by: SortBy, order: SortOrder):
      ((a: any, b: any) => number) {
    return ((a: any, b: any): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.teamName;
          strB = b.teamName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.HAS_SUBMITTED_SESSION:
          strA = a.hasSubmittedSession.toString();
          strB = b.hasSubmittedSession.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      if (order === SortOrder.ASC) {
        return strA.localeCompare(strB);
      }
      if (order === SortOrder.DESC) {
        return strB.localeCompare(strA);
      }
      return 0;
    });
  }

  /**
   * Checks whether all students are selected.
   */
  get isAllStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Changes selection state for all students.
   */
  changeSelectionStatusForAllStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModelsChange.emit(
        this.studentListInfoTableRowModels.map((model: StudentListInfoTableRowModel) => Object.assign({}, model, {
          isSelected: shouldSelect,
        })),
    );
  }

  /**
   * Checks whether all instructors are selected.
   */
  get isAllInstructorsSelected(): boolean {
    return this.instructorListInfoTableRowModels.every((model: InstructorListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Changes selection state for all instructors.
   */
  changeSelectionStatusForAllInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModelsChange.emit(
        this.instructorListInfoTableRowModels.map((model: InstructorListInfoTableRowModel) => Object.assign({}, model, {
          isSelected: shouldSelect,
        })),
    );
  }

}
