import {
  FeedbackSession,
  InstructorPermissionSet,
} from '../../../types/api-output';

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
}

/** Map from column to its name. */
export const SessionsTableColumnNames = new Map<SessionsTableColumn, string>([
  [SessionsTableColumn.COURSE_ID, 'Course ID'],
  [SessionsTableColumn.START_DATE, 'Start Date'],
  [SessionsTableColumn.END_DATE, 'End Date'],
  [SessionsTableColumn.RESPONSE_RATE, 'Response Rate'],
]);

/**
 * The color scheme of the header of the table
 */
export enum SessionsTableHeaderColorScheme {
  /**
   * Blue background with white text.
   */
  BLUE,

  /**
   * White background with black text.
   */
  WHITE,
}
