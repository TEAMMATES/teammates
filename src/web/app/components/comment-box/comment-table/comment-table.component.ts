import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommentVisibilityType } from '../../../../types/api-output';
import { CommentRowMode, CommentRowModel } from '../comment-row/comment-row.component';

/**
 * Component for the comments table.
 */
@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss'],
})
export class CommentTableComponent implements OnInit {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input() commentsTableModel: CommentRowModel[] = [];
  @Input() timezone: string = '';
  @Input() showResponsesToInCommentVisibilityType: CommentVisibilityType[] = [];
  @Input() isNewCommentExpanded: boolean = true;

  @Output() modelChange: EventEmitter<any> = new EventEmitter();
  @Output() saveNewCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() deleteCommentEvent: EventEmitter<number> = new EventEmitter();
  @Output() updateCommentEvent: EventEmitter<number> = new EventEmitter();
  @Output() closeEditingEvent: EventEmitter<number> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Triggers the delete comment event.
   */
  triggerDeleteCommentEvent(commentId: number): void {
    this.deleteCommentEvent.emit(commentId);
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
   * Triggers the change of the model for the form.
   */
  triggerModelChange(index: number, data: CommentRowModel): void {
    this.modelChange.emit({
      index,
      commentRow: data,
    });
  }

  /**
   * Triggers the close editing event.
   */
  triggerCloseEditingEvent(index: number): void {
    this.closeEditingEvent.emit(index);
  }
}
