import { Pipe, PipeTransform } from '@angular/core';
import { GiverCommentRowModel } from './comment-row/comment-row.component';
import { giverCommentToCommentRowModel } from './comment-row-model-mapper';

/**
 * Transforms a giver comment string to a comment row model.
 */
@Pipe({ name: 'giverCommentToCommentRowModel' })
export class GiverCommentToCommentRowModelPipe implements PipeTransform {
  transform(commentText: string): GiverCommentRowModel {
    return giverCommentToCommentRowModel(commentText);
  }
}
