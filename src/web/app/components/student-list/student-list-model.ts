import { ColumnData, SortableTableCellData } from '../sortable-table/sortable-table.component';

export enum StudentListColumns {
  SECTION,
  TEAM,
  STUDENT_NAME,
  STATUS,
  EMAIL,
  ACTIONS,
}

export const StudentListColumnNames = new Map<StudentListColumns, string>([
  [StudentListColumns.SECTION, 'Section'],
  [StudentListColumns.TEAM, 'Team'],
  [StudentListColumns.STUDENT_NAME, 'Student Name'],
  [StudentListColumns.STATUS, 'Status'],
  [StudentListColumns.EMAIL, 'Email'],
  [StudentListColumns.ACTIONS, 'Action(s)'],
]);

export interface StudentListColumnData extends ColumnData {
  columnType?: StudentListColumns;
}

export interface StudentListRowData extends SortableTableCellData {
  columnType?: StudentListColumns;
  section?: string;
  team?: string;
  studentName?: string;
  status?: string;
  email?: string;
  actions?: string;
}
