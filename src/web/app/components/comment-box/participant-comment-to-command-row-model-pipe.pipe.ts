import { Pipe, PipeTransform } from '@angular/core';
import { CommentOutput } from '../../../types/api-output';
import { CommentRowModel } from './comment-row/comment-row.component';

/**
 * Transforms participant comment to comment row model.
 */
@Pipe({
  name: 'participantCommentToCommandRowModelPipe',
})
export class ParticipantCommentToCommandRowModelPipePipe implements PipeTransform {

  transform(participantComment: CommentOutput, timezone?: string): CommentRowModel {
    return {
      timezone,
      originalComment: participantComment,
      commentGiverName: participantComment.commentGiverName,
      lastEditorName: participantComment.lastEditorName,
      commentEditFormModel: {
        commentText: participantComment.commentText,
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: false,
    };
  }

}
