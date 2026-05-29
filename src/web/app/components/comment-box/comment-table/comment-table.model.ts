import type { InstructorCommentRowModel, NewCommentRowModel } from '../comment.model';

/**
 * Model for CommentTableComponent.
 */
export interface CommentTableModel {
  commentRows: InstructorCommentRowModel[];
  newCommentRow: NewCommentRowModel;
  isAddingNewComment: boolean;
  isReadOnly: boolean;
}
