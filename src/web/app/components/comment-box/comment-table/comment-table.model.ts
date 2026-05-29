import { InstructorCommentRowModel, NewCommentRowModel } from '../comment-row/comment-row.component';

/**
 * Model for CommentTableComponent.
 */
export interface CommentTableModel {
  commentRows: InstructorCommentRowModel[];
  newCommentRow: NewCommentRowModel;
  isAddingNewComment: boolean;
  isReadOnly: boolean;
}
