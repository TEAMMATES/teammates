import { Injectable, Pipe, PipeTransform } from '@angular/core';
import { CommentEditFormModel, GiverCommentRowModel, InstructorCommentRowModel } from './comment.model';
import { FeedbackResponseComment } from '../../../types/api-output';

/**
 * Transforms comment to comment row model.
 */
@Injectable({ providedIn: 'root' })
@Pipe({ name: 'commentToCommentRowModel' })
export class CommentToCommentRowModelPipe implements PipeTransform {
  transform(comment: FeedbackResponseComment, timezone: string): InstructorCommentRowModel;
  transform(comment: string, timezone?: string): GiverCommentRowModel;
  transform(
    comment: FeedbackResponseComment | string,
    timezone = '',
  ): InstructorCommentRowModel | GiverCommentRowModel {
    const originalCommentFormModel: CommentEditFormModel =
      typeof comment === 'string'
        ? {
            commentText: comment,
            isUsingCustomVisibilities: false,
            showCommentTo: [],
            showGiverNameTo: [],
          }
        : {
            commentText: comment.commentText,
            isUsingCustomVisibilities: !comment.isVisibilityFollowingFeedbackQuestion,
            showCommentTo: comment.showCommentTo,
            showGiverNameTo: comment.showGiverNameTo,
          };

    return {
      ...(typeof comment === 'string'
        ? {
            commentType: 'giver' as const,
          }
        : {
            commentType: 'instructor' as const,
            commentId: comment.feedbackResponseCommentId,
            commentGiverName: comment.commentGiverName,
            lastEditorName: comment.lastEditorName,
            createdAt: comment.createdAt,
            lastEditedAt: comment.lastEditedAt,
            timezone,
          }),
      originalCommentFormModel,
      commentEditFormModel: structuredClone(originalCommentFormModel),
      isEditing: false,
    };
  }
}
