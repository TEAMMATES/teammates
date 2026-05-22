import { Pipe, PipeTransform, inject } from '@angular/core';
import { CommentTableModel } from './comment-table/comment-table.model';
import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';
import { FeedbackResponseComment } from '../../../types/api-output';

/**
 * Transforms comments to readonly comment table model.
 */
@Pipe({ name: 'commentsToCommentTableModel' })
export class CommentsToCommentTableModelPipe implements PipeTransform {
  private commentToCommentRowModel = inject(CommentToCommentRowModelPipe);

  transform(comments: FeedbackResponseComment[], isReadOnly: boolean, timezone?: string): CommentTableModel {
    return {
      isReadOnly,
      commentRows: comments.map((comment: FeedbackResponseComment) => {
        return this.commentToCommentRowModel.transform(comment, timezone);
      }),
      newCommentRow: {
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },
        isEditing: false,
      },
      isAddingNewComment: false,
    };
  }
}
