import { FeedbackSession, InstructorPermissionSet } from '../../../types/api-output';
/* eslint-disable-next-line import/no-cycle */
import { ColumnData, SortableTableCellData } from '../sortable-table/sortable-table.component';

/**
 * The model for a row of the sessions table.
 */
export interface SessionsTableRowModel {
  feedbackSession: FeedbackSession;
  responseRate: string;
  isLoadingResponseRate: boolean;

  instructorPrivilege: InstructorPermissionSet;
}

/**
 * The result of copy session event.
 */
export interface CopySessionResult {
  sessionToCopyRowIndex: number;
  newFeedbackSessionName: string;
  copyToCourseList: string[];
  sessionToCopyCourseId: string;
}

/**
 * The column of the session table
 */
export enum SessionsTableColumn {

  /**
   * Course ID column.
   */
  COURSE_ID,

  /**
   * Start date of the feedback session column.
   */
  START_DATE,

  /**
   * End date of the feedback session column.
   */
  END_DATE,
  /**
   * Response rate of the feedback session column.
   */
  RESPONSE_RATE,
  /**
   * Responses of the feedback session column.
   */
  RESPONSES,
  /**
   * Actions of the feedback session column.
   */
  ACTIONS,
}

/** Map from column to its name. */
export const SessionsTableColumnNames = new Map<SessionsTableColumn, string>([
  [SessionsTableColumn.COURSE_ID, 'Course ID'],
  [SessionsTableColumn.START_DATE, 'Start Date'],
  [SessionsTableColumn.END_DATE, 'End Date'],
  [SessionsTableColumn.RESPONSE_RATE, 'Response Rate'],
  [SessionsTableColumn.RESPONSES, 'Responses'],
  [SessionsTableColumn.ACTIONS, 'Action(s)'],
]);

/** Generate header. */
export interface SessionsTableColumnData extends ColumnData {
  columnType?: SessionsTableColumn;
}

/** Generate Row. */
export interface SessionsTableRowData extends SortableTableCellData {
  columnType?: SessionsTableColumn;
}
