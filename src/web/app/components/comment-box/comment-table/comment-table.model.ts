import type { InstructorCommentRowModel, NewCommentRowModel } from '../comment.model';

/**
 * Model for CommentTableComponent.
 */
export interface CommentTableModel {
  currentInstructorId?: string;
  commentRows: InstructorCommentRowModel[];
  newCommentRow: NewCommentRowModel;
  isAddingNewComment: boolean;
  isReadOnly: boolean;
}
