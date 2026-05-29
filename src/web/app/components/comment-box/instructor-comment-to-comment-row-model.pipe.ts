import { Pipe, PipeTransform } from '@angular/core';
import { InstructorCommentRowModel } from './comment-row/comment-row.component';
import { instructorCommentToCommentRowModel } from './comment-row-model-mapper';
import { FeedbackResponseComment } from '../../../types/api-output';

/**
 * Transforms an instructor comment to a comment row model.
 */
@Pipe({ name: 'instructorCommentToCommentRowModel' })
export class InstructorCommentToCommentRowModelPipe implements PipeTransform {
  transform(comment: FeedbackResponseComment, timezone: string): InstructorCommentRowModel {
    return instructorCommentToCommentRowModel(comment, timezone);
  }
}
