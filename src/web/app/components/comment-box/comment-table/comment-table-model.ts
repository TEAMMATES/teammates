import { FeedbackVisibilityType } from '../../../../types/api-request';

/**
 * Model for a comment to be displayed in the comment table
 */
export interface FeedbackResponseCommentModel {
  commentId: number;
  createdAt: number;
  editedAt?: number;
  timeZone: string;
  commentGiver: string;
  commentText: string;
  showCommentTo: FeedbackVisibilityType[];
  showGiverNameTo: FeedbackVisibilityType[];
}

/**
 * The display mode of the comments table
 */
export enum CommentTableMode {
  /**
   * Session submission mode.
   */
  SESSION_SUBMISSION,
  /**
   * Instructor result mode.
   */
  INSTRUCTOR_RESULT,
  /**
   * Student result mode.
   */
  STUDENT_RESULT,
}

/**
 * Contains default values to be used for {@link FeedbackResponseCommentModel}.
 */
export enum CommentModelDefaultValues {
  /**
   * Represents an invalid comment ID.
   */
  INVALID_VALUE = -1,
}
