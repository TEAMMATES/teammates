import { Pipe, PipeTransform } from '@angular/core';
import { CommentTableModel } from './comment-table/comment-table.model';
import { createNewCommentRowModel, instructorCommentToCommentRowModel } from './comment-row-model-mapper';
import { ResponseInstructorComment, FeedbackVisibilityType } from '../../../types/api-output';

/**
 * Transforms comments to readonly comment table model.
 */
@Pipe({ name: 'commentsToCommentTableModel' })
export class CommentsToCommentTableModelPipe implements PipeTransform {
  transform(
    comments: ResponseInstructorComment[],
    isReadOnly: boolean,
    timezone: string,
    questionShowResponsesTo: FeedbackVisibilityType[] = [],
  ): CommentTableModel {
    return {
      isReadOnly,
      commentRows: comments.map((comment: ResponseInstructorComment) => {
        return instructorCommentToCommentRowModel(comment, timezone);
      }),
      newCommentRow: createNewCommentRowModel(questionShowResponsesTo),
      isAddingNewComment: false,
    };
  }
}
