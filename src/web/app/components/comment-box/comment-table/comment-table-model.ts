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
  isEditable: boolean;
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
   * Session result mode.
   */
  SESSION_RESULT,
}
