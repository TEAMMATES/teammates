import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackResponseCommentModel } from './comment-table-model';

/**
 * Comment row component to be used in a comment table
 */
@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss'],
})
export class CommentRowComponent implements OnInit {
  @Input() isInEditModeInitialValue: boolean = false;
  @Input() isVisibilityOptionEnabled: boolean = true;
  @Input() isDiscardButtonEnabled: boolean = true;
  @Input() isSaveButtonEnabled: boolean = true;

  @Input()
  commentModel: FeedbackResponseCommentModel = {
    commentId: -1,
    commentText: '',
    commentGiver: '',
    createdAt: '',
    editedAt: '',
    isEditable: false,
  };

  @Output() editCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() deleteCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() commentFormChangeEvent: EventEmitter<any> = new EventEmitter();

  isInEditMode: boolean = false;

  constructor() { }

  ngOnInit(): void {
    this.isInEditMode = this.isInEditModeInitialValue;
  }

  /**
   * Triggers comment form change.
   */
  triggerCommentFormChange(commentText: any): void {
    this.commentFormChangeEvent.emit(commentText);
  }

  /**
   * Disable edit mode.
   */
  triggerCloseCommentEditForm(): void {
    this.isInEditMode = false;
  }

  /**
   * Change to edit mode.
   */
  triggerEditCommentEvent(): void {
    this.isInEditMode = true;
  }

  /**
   * Triggers the save comment event.
   */
  triggerSaveCommentEvent(data: any): void {
    this.saveCommentEvent.emit(data);
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(): void {
    this.deleteCommentEvent.emit();
  }
}
