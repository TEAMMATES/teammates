import { CommentRowModel } from '../comment-row/comment-row.component';

/**
 * Model for CommentTableComponent.
 */
export interface CommentTableModel {
  commentRows: CommentRowModel[];
  newCommentRow: CommentRowModel;
  isAddingNewComment: boolean;
  isReadOnly: boolean;
}
