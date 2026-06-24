import { Injectable, inject } from '@angular/core';
import { ResponseInstructorCommentService } from './feedback-response-comment.service';
import { StatusMessageService } from './status-message.service';
import { TableComparatorService } from './table-comparator.service';
import type { InstructorCommentRowModel, NewCommentRowModel } from '../app/components/comment-box/comment.model';
import { CommentTableModel } from '../app/components/comment-box/comment-table/comment-table.model';
import { instructorCommentToCommentRowModel } from '../app/components/comment-box/comment-row-model-mapper';
import { ErrorMessageOutput } from '../app/error-message-output';
import { ResponseInstructorComment } from '../types/api-output';
import { SortBy, SortOrder } from '../types/sort-properties';

export interface InstructorCommentEventData {
  responseId: string;
  index: number;
}

export interface InstructorCommentUpdateParams {
  data: InstructorCommentEventData;
  timezone: string;
  instructorCommentTableModel: Record<string, CommentTableModel>;
}

export interface InstructorCommentSaveParams {
  responseId: string;
  timezone: string;
  instructorCommentTableModel: Record<string, CommentTableModel>;
}

export interface InstructorCommentDeleteParams {
  data: InstructorCommentEventData;
  instructorCommentTableModel: Record<string, CommentTableModel>;
}

@Injectable({ providedIn: 'root' })
export class InstructorCommentService {
  private commentService = inject(ResponseInstructorCommentService);
  private statusMessageService = inject(StatusMessageService);
  private tableComparatorService = inject(TableComparatorService);

  /**
   * Deletes an instructor comment.
   */
  deleteComment({ data, instructorCommentTableModel }: InstructorCommentDeleteParams): void {
    const commentTableModel: CommentTableModel = instructorCommentTableModel[data.responseId];
    const commentId: string = commentTableModel.commentRows[data.index].commentId;

    this.commentService.deleteComment(commentId).subscribe({
      next: () => {
        commentTableModel.commentRows.splice(data.index, 1);
        instructorCommentTableModel[data.responseId] = {
          ...commentTableModel,
        };
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Updates an instructor comment.
   */
  updateComment({ data, timezone, instructorCommentTableModel }: InstructorCommentUpdateParams): void {
    const commentTableModel: CommentTableModel = instructorCommentTableModel[data.responseId];
    const commentRowToUpdate: InstructorCommentRowModel = commentTableModel.commentRows[data.index];
    const commentId: string = commentRowToUpdate.commentId;

    this.commentService
      .updateComment(
        {
          commentText: commentRowToUpdate.commentEditFormModel.commentText,
        },
        commentId,
      )
      .subscribe({
        next: (commentResponse: ResponseInstructorComment) => {
          commentTableModel.commentRows[data.index] = instructorCommentToCommentRowModel(
            commentResponse,
            timezone,
            commentTableModel.currentInstructorId,
          );
          instructorCommentTableModel[data.responseId] = {
            ...commentTableModel,
          };
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Saves an instructor comment.
   */
  saveNewComment({ responseId, timezone, instructorCommentTableModel }: InstructorCommentSaveParams): void {
    const commentTableModel: CommentTableModel = instructorCommentTableModel[responseId];
    const commentRowToAdd: NewCommentRowModel = commentTableModel.newCommentRow;

    this.commentService
      .createComment(
        {
          commentText: commentRowToAdd.commentEditFormModel.commentText,
        },
        responseId,
      )
      .subscribe({
        next: (commentResponse: ResponseInstructorComment) => {
          commentTableModel.commentRows.push(
            instructorCommentToCommentRowModel(commentResponse, timezone, commentTableModel.currentInstructorId),
          );
          this.sortComments(commentTableModel);
          instructorCommentTableModel[responseId] = {
            ...commentTableModel,
            newCommentRow: {
              ...commentRowToAdd,
              commentEditFormModel: {
                ...commentRowToAdd.commentEditFormModel,
                commentText: '',
              },
              isEditing: false,
            },
            isAddingNewComment: false,
          };
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Sorts instructor's comments according to creation date.
   */
  sortComments(commentTable: CommentTableModel): void {
    commentTable.commentRows.sort((a: InstructorCommentRowModel, b: InstructorCommentRowModel) => {
      return this.tableComparatorService.compare(
        SortBy.COMMENTS_CREATION_DATE,
        SortOrder.ASC,
        String(a.createdAt),
        String(b.createdAt),
      );
    });
  }
}
