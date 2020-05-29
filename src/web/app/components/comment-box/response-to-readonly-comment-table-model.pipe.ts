import { Pipe, PipeTransform } from '@angular/core';
import { CommentOutput, ResponseOutput } from '../../../types/api-output';
import { CommentTableModel } from './comment-table/comment-table.component';
import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';

/**
 * Transforms response to readonly comment table model.
 */
@Pipe({
  name: 'responseToReadonlyCommentTableModel',
})
export class ResponseToReadonlyCommentTableModelPipe implements PipeTransform {
  constructor(private commentToCommentRowModel: CommentToCommentRowModelPipe) {
  }
  transform(response: ResponseOutput, timezone?: string): CommentTableModel {
    return {
      commentRows: response.instructorComments.map((comment: CommentOutput) => {
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
      isReadonly: true,
    };
  }

}
