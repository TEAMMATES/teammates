import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommentTableModel } from './comment-table.model';
import { FeedbackVisibilityType, ResponseOutput } from '../../../../types/api-output';
import { createNewCommentRowModel } from '../comment-row-model-mapper';
import type { CommentRowModel, InstructorCommentRowModel } from '../comment.model';
import { CommentRowComponent } from '../comment-row/comment-row.component';
import { CommentRowMode } from '../comment-row/comment-row.mode';
import { CommentsToCommentTableModelPipe } from '../comments-to-comment-table-model.pipe';

/**
 * Component for the comments table.
 */
@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss'],
  imports: [CommentRowComponent],
  providers: [CommentsToCommentTableModelPipe],
})
export class CommentTableComponent {
  // enum
  CommentRowMode!: typeof CommentRowMode;

  @Input()
  response?: ResponseOutput;

  @Input()
  questionShowResponsesTo: FeedbackVisibilityType[] = [];

  @Input()
  displayAddCommentButton = false;

  @Input()
  model: CommentTableModel = {
    commentRows: [],
    newCommentRow: createNewCommentRowModel([], true),
    isAddingNewComment: true,
    isReadOnly: false,
  };

  @Input()
  shouldHideClosingButtonForNewComment = false;

  @Output()
  modelChange: EventEmitter<CommentTableModel> = new EventEmitter();

  @Output()
  saveNewCommentEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  updateCommentEvent: EventEmitter<number> = new EventEmitter();

  constructor() {
    this.CommentRowMode = CommentRowMode;
  }

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
    if (data.commentType !== 'instructor') {
      return;
    }

    const newCommentRows: InstructorCommentRowModel[] = structuredClone(this.model.commentRows);
    newCommentRows[index] = data;
    this.triggerModelChange('commentRows', newCommentRows);
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: unknown): void {
    this.modelChange.emit({ ...this.model, [field]: data });
  }

  /**
   * Handles the close editing event.
   */
  handleCloseEditingCommentRowEvent(index: number): void {
    const newRowModel: InstructorCommentRowModel = structuredClone(this.model.commentRows[index]);
    newRowModel.commentEditFormModel = structuredClone(newRowModel.originalCommentFormModel);
    newRowModel.isEditing = false;
    this.triggerCommentRowChange(index, newRowModel);
  }

  /**
   * Handles adding new comment button click event.
   */
  handleAddingNewCommentEvent(): void {
    this.modelChange.emit({
      ...this.model,
      newCommentRow: createNewCommentRowModel(this.questionShowResponsesTo, true),
      isAddingNewComment: true,
    });
  }
}
