import { Pipe, PipeTransform } from '@angular/core';
import { CommentOutput, ResponseOutput } from '../../../types/api-output';
import { CommentTableModel } from './comment-table/comment-table.component';
import { ParticipantCommentToCommentRowModelPipe } from './participant-comment-to-comment-row-model.pipe';

@Pipe({
  name: 'responseOutputToReadonlyCommentTableModel',
})
export class ResponseOutputToReadonlyCommentTableModelPipe implements PipeTransform {
  constructor(private participantCommentToCommentRowModel: ParticipantCommentToCommentRowModelPipe) {
  }
  transform(response: ResponseOutput, timezone?: string): CommentTableModel {
    return {
      commentRows: response.instructorComments.map((comment: CommentOutput) => {
        return this.participantCommentToCommentRowModel.transform(comment, timezone);
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
      isAddingNewComment: true, // What is this field doing?
    };
  }

}
