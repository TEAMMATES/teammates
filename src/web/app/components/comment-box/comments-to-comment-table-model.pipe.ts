import { Pipe, PipeTransform } from '@angular/core';
import { CommentTableModel } from './comment-table/comment-table.model';
import { ResponseInstructorComment } from '../../../types/api-output';
import { commentToReadOnlyComment } from '../../utils/comment-to-comment-table.util';

/**
 * Transforms comments to readonly comment table model.
 */
@Pipe({ name: 'commentsToCommentTableModel' })
export class CommentsToCommentTableModelPipe implements PipeTransform {
  transform(
    comments: ResponseInstructorComment[],
    isReadOnly: boolean,
    timezone: string,
  ): CommentTableModel {
    return commentToReadOnlyComment(comments, isReadOnly, timezone);
  }
}
