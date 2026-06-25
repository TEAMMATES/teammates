import { ResponseInstructorComment } from '../../types/api-output';
import { CommentTableModel } from '../components/comment-box/comment-table/comment-table.model';
import {
  createNewCommentRowModel,
  instructorCommentToCommentRowModel,
} from '../components/comment-box/comment-row-model-mapper';

export function commentToReadOnlyComment(
  comments: ResponseInstructorComment[],
  isReadOnly: boolean,
  timezone: string,
  currentInstructorId?: string,
): CommentTableModel {
  return {
    currentInstructorId,
    isReadOnly,
    commentRows: comments.map((comment: ResponseInstructorComment) => {
      return instructorCommentToCommentRowModel(comment, timezone, currentInstructorId);
    }),
    newCommentRow: createNewCommentRowModel(),
    isAddingNewComment: false,
  };
}
