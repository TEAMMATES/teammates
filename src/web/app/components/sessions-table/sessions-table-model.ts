import { FeedbackSession, FeedbackSessionStudentResponse, InstructorPrivilege } from '../../../types/api-output';

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
 * The model for a row of the student status table.
 */
export interface StudentStatusTableRowModel {
  feedbackSessionStudentResponse: FeedbackSessionStudentResponse;
  isChecked: boolean;
}

/**
 * The result of copy session event.
 */
export interface CopySessionResult {
  sessionToCopyRowIndex: number;
  newFeedbackSessionName: string;
  copyToCourseId: string;
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

/**
 * Sort criteria for the sessions table.
 */
export enum SortBy {
  /**
   * Nothing.
   */
  NONE,

  /**
   * Course ID.
   */
  COURSE_ID,

  /**
   * Course ID.
   */
  COURSE_NAME,

  /**
   * The creation time of the course.
   */
  COURSE_CREATION_DATE,

  /**
   * Feedback session name.
   */
  FEEDBACK_SESSION_NAME,

  /**
   * Start time of the feedback session.
   */
  START_DATE,

  /**
   * End time of the feedback session.
   */
  END_DATE,

  /**
   * The creation time of the feedback session.
   */
  SESSION_CREATION_DATE,

  /**
   * The time when the feedback session is moved to recycle bin.
   */
  DELETION_DATE,

  /**
   * The name of the student's section.
   */
  SECTION_NAME,

  /**
   * The name of the student's team.
   */
  TEAM_NAME,

  /**
   * The name of the student.
   */
  STUDENT_NAME,

  /**
   * The email of the student.
   */
  STUDENT_EMAIL,

  /**
   * The status of the student's feedback submission.
   */
  SUBMIT_STATUS,
}

/**
 * Sort order for the sessions table.
 */
export enum SortOrder {
  /**
   * Descending sort order.
   */
  DESC,

  /**
   * Ascending sort order
   */
  ASC,
}
