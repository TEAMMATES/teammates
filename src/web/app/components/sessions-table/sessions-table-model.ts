import { FeedbackSession, InstructorPrivilege } from '../../../types/api-output';

/**
 * The model for a row of the sessions table.
 */
export interface SessionsTableRowModel {
  feedbackSession: FeedbackSession;
  responseRate: string;
  isLoadingResponseRate: boolean;

  instructorPrivilege: InstructorPrivilege;
}

/**
 * The result of copy session event.
 */
export interface CopySessionResult {
  sessionToCopyRowIndex: number;
  newFeedbackSessionName: string;
  copyToCourseList: string[];
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
}

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
