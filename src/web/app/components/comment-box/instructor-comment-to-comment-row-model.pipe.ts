import { Pipe, PipeTransform } from '@angular/core';
import type { InstructorCommentRowModel } from './comment.model';
import { instructorCommentToCommentRowModel } from './comment-row-model-mapper';
import { ResponseInstructorComment } from '../../../types/api-output';

/**
 * Transforms an instructor comment to a comment row model.
 */
@Pipe({ name: 'instructorCommentToCommentRowModel' })
export class InstructorCommentToCommentRowModelPipe implements PipeTransform {
  transform(comment: ResponseInstructorComment, timezone: string): InstructorCommentRowModel {
    return instructorCommentToCommentRowModel(comment, timezone);
  }
}
