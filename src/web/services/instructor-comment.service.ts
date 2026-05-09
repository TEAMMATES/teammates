import { Injectable } from '@angular/core';
import { FeedbackResponseCommentService } from './feedback-response-comment.service';
import { StatusMessageService } from './status-message.service';
import { TableComparatorService } from './table-comparator.service';
import { CommentRowModel } from '../app/components/comment-box/comment-row/comment-row.component';
import { CommentTableModel } from '../app/components/comment-box/comment-table/comment-table.model';
import { CommentToCommentRowModelPipe } from '../app/components/comment-box/comment-to-comment-row-model.pipe';
import { ErrorMessageOutput } from '../app/error-message-output';
import { FeedbackResponseComment } from '../types/api-output';
import { Intent } from '../types/api-request';
import { SortBy, SortOrder } from '../types/sort-properties';

export interface InstructorCommentEventData {
  responseId: string;
  index: number;
}

export interface InstructorCommentUpdateParams {
  data: InstructorCommentEventData;
  timezone: string;
  instructorCommentTableModel: Record<string, CommentTableModel>;
  currInstructorName?: string;
}

export interface InstructorCommentSaveParams {
  responseId: string;
  timezone: string;
  instructorCommentTableModel: Record<string, CommentTableModel>;
  currInstructorName?: string;
}

export interface InstructorCommentDeleteParams {
  data: InstructorCommentEventData;
  instructorCommentTableModel: Record<string, CommentTableModel>;
}

@Injectable({ providedIn: 'root' })
export class InstructorCommentService {
  constructor(
    private commentToCommentRowModel: CommentToCommentRowModelPipe,
    private commentService: FeedbackResponseCommentService,
    private statusMessageService: StatusMessageService,
    private tableComparatorService: TableComparatorService,
  ) {}

  /**
   * Deletes an instructor comment.
   */
  deleteComment({ data, instructorCommentTableModel }: InstructorCommentDeleteParams): void {
    const commentTableModel: CommentTableModel = instructorCommentTableModel[data.responseId];
    const commentToDelete: FeedbackResponseComment = commentTableModel.commentRows[data.index].originalComment!;

    this.commentService.deleteComment(commentToDelete.feedbackResponseCommentId, Intent.INSTRUCTOR_RESULT).subscribe({
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
  updateComment({
    data,
    timezone,
    instructorCommentTableModel,
    currInstructorName,
  }: InstructorCommentUpdateParams): void {
    const commentTableModel: CommentTableModel = instructorCommentTableModel[data.responseId];
    const commentRowToUpdate: CommentRowModel = commentTableModel.commentRows[data.index];
    const commentToUpdate: FeedbackResponseComment = commentRowToUpdate.originalComment!;

    this.commentService
      .updateComment(
        {
          commentText: commentRowToUpdate.commentEditFormModel.commentText,
          showCommentTo: commentRowToUpdate.commentEditFormModel.showCommentTo,
          showGiverNameTo: commentRowToUpdate.commentEditFormModel.showGiverNameTo,
        },
        commentToUpdate.feedbackResponseCommentId,
        Intent.INSTRUCTOR_RESULT,
      )
      .subscribe({
        next: (commentResponse: FeedbackResponseComment) => {
          // Only override lastEditorName when the caller provided a current instructor name.
          const transformedUpdatedComment = {
            ...commentResponse,
            commentGiverName: commentRowToUpdate.commentGiverName,
            ...(currInstructorName ? { lastEditorName: currInstructorName } : {}),
          };

          commentTableModel.commentRows[data.index] = this.commentToCommentRowModel.transform(
            transformedUpdatedComment,
            timezone,
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
  saveNewComment({
    responseId,
    timezone,
    instructorCommentTableModel,
    currInstructorName,
  }: InstructorCommentSaveParams): void {
    const commentTableModel: CommentTableModel = instructorCommentTableModel[responseId];
    const commentRowToAdd: CommentRowModel = commentTableModel.newCommentRow;

    this.commentService
      .createComment(
        {
          commentText: commentRowToAdd.commentEditFormModel.commentText,
          showCommentTo: commentRowToAdd.commentEditFormModel.showCommentTo,
          showGiverNameTo: commentRowToAdd.commentEditFormModel.showGiverNameTo,
        },
        responseId,
        Intent.INSTRUCTOR_RESULT,
      )
      .subscribe({
        next: (commentResponse: FeedbackResponseComment) => {
          commentTableModel.commentRows.push(
            this.commentToCommentRowModel.transform(
              {
                ...commentResponse,
                // the giver and editor name will be the current login instructor
                ...(currInstructorName
                  ? { commentGiverName: currInstructorName, lastEditorName: currInstructorName }
                  : {}),
              },
              timezone,
            ),
          );
          this.sortComments(commentTableModel);
          instructorCommentTableModel[responseId] = {
            ...commentTableModel,
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
    commentTable.commentRows.sort((a: CommentRowModel, b: CommentRowModel) => {
      return this.tableComparatorService.compare(
        SortBy.COMMENTS_CREATION_DATE,
        SortOrder.ASC,
        String(a.originalComment?.createdAt),
        String(b.originalComment?.createdAt),
      );
    });
  }
}
