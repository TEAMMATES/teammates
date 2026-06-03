import { Pipe, PipeTransform } from '@angular/core';
import type { GiverCommentRowModel } from './comment.model';
import { giverCommentToCommentRowModel } from './comment-row-model-mapper';
import { FeedbackVisibilityType } from '../../../types/api-output';

/**
 * Transforms a giver comment string to a comment row model.
 */
@Pipe({ name: 'giverCommentToCommentRowModel' })
export class GiverCommentToCommentRowModelPipe implements PipeTransform {
  transform(commentText: string, questionShowResponsesTo: FeedbackVisibilityType[] = []): GiverCommentRowModel {
    return giverCommentToCommentRowModel(commentText, questionShowResponsesTo);
  }
}
