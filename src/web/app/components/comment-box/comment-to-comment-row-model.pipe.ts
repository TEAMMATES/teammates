import { Injectable, Pipe, PipeTransform } from '@angular/core';
import { CommentRowModel } from './comment-row/comment-row.component';
import { FeedbackResponseComment } from '../../../types/api-output';

/**
 * Transforms comment to comment row model.
 */
@Injectable({ providedIn: 'root' })
@Pipe({ name: 'commentToCommentRowModel' })
export class CommentToCommentRowModelPipe implements PipeTransform {
  transform(comment: FeedbackResponseComment | string, timezone?: string): CommentRowModel {
    // Temporarily support string type for comment to avoid breaking existing code.
    // This can be removed once comment related code is updated to properly support both string and FeedbackResponseComment types.
    const commentModel: FeedbackResponseComment =
      typeof comment === 'string'
        ? {
            feedbackResponseCommentId: '',
            commentGiverName: '',
            lastEditorName: '',
            commentText: comment,
            createdAt: 0,
            lastEditedAt: 0,
            isVisibilityFollowingFeedbackQuestion: true,
            showCommentTo: [],
            showGiverNameTo: [],
          }
        : comment;

    return {
      timezone,
      originalComment: commentModel,
      commentEditFormModel: {
        commentText: commentModel.commentText,
        isUsingCustomVisibilities: !commentModel.isVisibilityFollowingFeedbackQuestion,
        showCommentTo: commentModel.showCommentTo,
        showGiverNameTo: commentModel.showGiverNameTo,
      },
      isEditing: false,
    };
  }
}
