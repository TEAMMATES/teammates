import { Injectable, Pipe, PipeTransform } from '@angular/core';
import { CommentRowModel } from './comment-row/comment-row.component';
import { FeedbackResponseComment } from '../../../types/api-output';

/**
 * Transforms comment to comment row model.
 */
@Injectable({ providedIn: 'root' })
@Pipe({ name: 'commentToCommentRowModel' })
export class CommentToCommentRowModelPipe implements PipeTransform {
  transform(comment: FeedbackResponseComment, timezone?: string): CommentRowModel {
    return {
      timezone,
      originalComment: comment,
      commentEditFormModel: {
        commentText: comment.commentText,
        isUsingCustomVisibilities: !comment.isVisibilityFollowingFeedbackQuestion,
        showCommentTo: comment.showCommentTo,
        showGiverNameTo: comment.showGiverNameTo,
      },
      isEditing: false,
    };
  }
}
