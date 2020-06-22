import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackVisibilityType, ResponseOutput,
} from '../../../../types/api-output';
import { CommentTableModel } from '../comment-table/comment-table.component';

/**
 * Modal for the comments table.
 */
@Component({
  selector: 'tm-comment-table-modal',
  templateUrl: './comment-table-modal.component.html',
  styleUrls: ['./comment-table-modal.component.scss'],
})
export class CommentTableModalComponent implements OnInit, OnChanges {

  @Input()
  response?: ResponseOutput;

  @Input()
  questionShowResponsesTo: FeedbackVisibilityType[] = [];

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
  activeModal?: NgbActiveModal;

  @Output()
  modelChange: EventEmitter<CommentTableModel> = new EventEmitter();

  @Output()
  saveNewCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  updateCommentEvent: EventEmitter<number> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {

  }

  ngOnChanges(): void {
    if (!this.model.isAddingNewComment) {
      // in the model, we should always let the new comment box open regardless of upstream's settings
      this.modelChange.emit(Object.assign({}, this.model, { isAddingNewComment: true }));
    }
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
   * Triggers the change of the model for the form.
   */
  triggerModelChange(model: CommentTableModel): void {
    this.modelChange.emit(model);
  }

}
