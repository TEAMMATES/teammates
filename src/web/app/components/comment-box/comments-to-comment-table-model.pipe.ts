import { Pipe, PipeTransform } from '@angular/core';
import { CommentTableModel } from './comment-table/comment-table.model';
import { createNewCommentRowModel, instructorCommentToCommentRowModel } from './comment-row-model-mapper';
import { FeedbackResponseComment } from '../../../types/api-output';

/**
 * Transforms comments to readonly comment table model.
 */
@Pipe({ name: 'commentsToCommentTableModel' })
export class CommentsToCommentTableModelPipe implements PipeTransform {
  transform(comments: FeedbackResponseComment[], isReadOnly: boolean, timezone: string): CommentTableModel {
    return {
      isReadOnly,
      commentRows: comments.map((comment: FeedbackResponseComment) => {
        return instructorCommentToCommentRowModel(comment, timezone);
      }),
      newCommentRow: createNewCommentRowModel(),
      isAddingNewComment: false,
    };
  }
}
