import { FeedbackResponseCommentService } from '../../services/feedback-response-comment.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TableComparatorService } from '../../services/table-comparator.service';
import { FeedbackResponseComment } from '../../types/api-output';
import { Intent } from '../../types/api-request';
import { SortBy, SortOrder } from '../../types/sort-properties';
import { CommentRowModel } from '../components/comment-box/comment-row/comment-row.component';
import { CommentTableModel } from '../components/comment-box/comment-table/comment-table.component';
import { CommentToCommentRowModelPipe } from '../components/comment-box/comment-to-comment-row-model.pipe';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * Base class for instructor comment CRUD operations.
 */
export abstract class InstructorCommentsComponent {

  currInstructorName?: string;

  // this is a separate model for instructor comments
  // from responseID to comment table model
  instructorCommentTableModel: Record<string, CommentTableModel> = {};

  protected constructor(
        protected commentToCommentRowModel: CommentToCommentRowModelPipe,
        protected commentService: FeedbackResponseCommentService,
        protected statusMessageService: StatusMessageService,
        protected tableComparatorService: TableComparatorService) { }

  /**
   * Deletes an instructor comment.
   */
  deleteComment(data: { responseId: string, index: number}): void {
    const commentTableModel: CommentTableModel = this.instructorCommentTableModel[data.responseId];
    const commentToDelete: FeedbackResponseComment =
            // tslint:disable-next-line:no-non-null-assertion
            this.instructorCommentTableModel[data.responseId].commentRows[data.index].originalComment!;

    this.commentService.deleteComment(commentToDelete.feedbackResponseCommentId, Intent.INSTRUCTOR_RESULT)
        .subscribe(() => {
          commentTableModel.commentRows.splice(data.index, 1);
          this.instructorCommentTableModel[data.responseId] = {
            ...commentTableModel,
          };
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Updates an instructor comment.
   */
  updateComment(data: { responseId: string, index: number}, timezone: string): void {
    const commentTableModel: CommentTableModel = this.instructorCommentTableModel[data.responseId];
    const commentRowToUpdate: CommentRowModel = commentTableModel.commentRows[data.index];
    // tslint:disable-next-line:no-non-null-assertion
    const commentToUpdate: FeedbackResponseComment = commentRowToUpdate.originalComment!;

    this.commentService.updateComment({
      commentText: commentRowToUpdate.commentEditFormModel.commentText,
      showCommentTo: commentRowToUpdate.commentEditFormModel.showCommentTo,
      showGiverNameTo: commentRowToUpdate.commentEditFormModel.showGiverNameTo,
    }, commentToUpdate.feedbackResponseCommentId, Intent.INSTRUCTOR_RESULT)
        .subscribe((commentResponse: FeedbackResponseComment) => {
          commentTableModel.commentRows[data.index] = this.commentToCommentRowModel.transform({
            ...commentResponse,
            commentGiverName: commentRowToUpdate.commentGiverName,
            // the current instructor will become the last editor
            lastEditorName: this.currInstructorName,
          }, timezone);
          this.instructorCommentTableModel[data.responseId] = {
            ...commentTableModel,
          };
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Saves an instructor comment.
   */
  saveNewComment(responseId: string, timezone: string): void {
    const commentTableModel: CommentTableModel = this.instructorCommentTableModel[responseId];
    const commentRowToAdd: CommentRowModel = commentTableModel.newCommentRow;

    this.commentService.createComment({
      commentText: commentRowToAdd.commentEditFormModel.commentText,
      showCommentTo: commentRowToAdd.commentEditFormModel.showCommentTo,
      showGiverNameTo: commentRowToAdd.commentEditFormModel.showGiverNameTo,
    }, responseId, Intent.INSTRUCTOR_RESULT)
        .subscribe((commentResponse: FeedbackResponseComment) => {
          commentTableModel.commentRows.push(this.commentToCommentRowModel.transform({
            ...commentResponse,
            // the giver and editor name will be the current login instructor
            commentGiverName: this.currInstructorName,
            lastEditorName: this.currInstructorName,
          }, timezone));
          this.instructorCommentTableModel[responseId] = {
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
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
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
          String(a.originalComment?.createdAt), String(b.originalComment?.createdAt));
    });
  }
}
