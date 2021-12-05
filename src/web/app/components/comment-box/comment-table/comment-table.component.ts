import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FeedbackResponseComment, FeedbackVisibilityType, ResponseOutput,
} from '../../../../types/api-output';
import { collapseAnim } from '../../teammates-common/collapse-anim';
import { CommentRowModel } from '../comment-row/comment-row.component';
import { CommentRowMode } from '../comment-row/comment-row.mode';

/**
 * Model for CommentTableComponent.
 */
export interface CommentTableModel {
  commentRows: CommentRowModel[];
  newCommentRow: CommentRowModel;

  isAddingNewComment: boolean;
  isReadOnly: boolean;
}

/**
 * Component for the comments table.
 */
@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss'],
  animations: [collapseAnim],
})
export class CommentTableComponent {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input()
  response?: ResponseOutput;

  @Input()
  questionShowResponsesTo: FeedbackVisibilityType[] = [];

  @Input()
  displayAddCommentButton: boolean = false;

  @Input()
  model: CommentTableModel = {
    commentRows: [],
    newCommentRow: {
      commentEditFormModel: {
        commentText: '',
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: true,
    },
    isAddingNewComment: true,
    isReadOnly: false,
  };

  @Input()
  shouldHideClosingButtonForNewComment: boolean = false;

  @Output()
  modelChange: EventEmitter<CommentTableModel> = new EventEmitter();

  @Output()
  saveNewCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  updateCommentEvent: EventEmitter<number> = new EventEmitter();

  /**
   * Triggers the delete comment event.
   */
  triggerDeleteCommentEvent(index: number): void {
    this.deleteCommentEvent.emit(index);
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(index: number): void {
    this.updateCommentEvent.emit(index);
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(): void {
    this.saveNewCommentEvent.emit();
  }

  /**
   * Triggers the change of comment rows for the form.
   */
  triggerCommentRowChange(index: number, data: CommentRowModel): void {
    const newCommentRows: CommentRowModel[] = JSON.parse(JSON.stringify(this.model.commentRows));
    newCommentRows[index] = data;
    this.triggerModelChange('commentRows', newCommentRows);
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({ ...this.model, [field]: data });
  }

  /**
   * Tracks by index.
   */
  trackByIndex(index: number): string {
    return index.toString();
  }

  /**
   * Handles the close editing event.
   */
  handleCloseEditingCommentRowEvent(index: number): void {
    const newRowModel: CommentRowModel = JSON.parse(JSON.stringify(this.model.commentRows[index]));
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const originalComment: FeedbackResponseComment = newRowModel.originalComment!;
    newRowModel.commentEditFormModel = {
      commentText: originalComment.commentText,

      isUsingCustomVisibilities: false,
      showCommentTo: originalComment.showCommentTo,
      showGiverNameTo: originalComment.showGiverNameTo,
    };
    newRowModel.isEditing = false;
    this.triggerCommentRowChange(index, newRowModel);
  }

  /**
   * Handles adding new comment button click event.
   */
  handleAddingNewCommentEvent(): void {
    this.triggerModelChange('isAddingNewComment', true);
  }
}
