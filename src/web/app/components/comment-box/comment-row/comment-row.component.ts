import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackResponseComment } from '../../../../types/api-output';
import { CommentEditFormModel } from '../comment-edit-form/comment-edit-form.component';
import {
  ConfirmDeleteCommentModalComponent,
} from '../confirm-delete-comment-modal/confirm-delete-comment-modal.component';

/**
 * Model for a comment row.
 */
export interface CommentRowModel {
  originalComment?: FeedbackResponseComment;
  /**
   * Timezone of the original comment.
   */
  timezone?: string;
  // timezone and originalComment are optional under ADD mode.

  commentEditFormModel: CommentEditFormModel;
  isEditing: boolean;
}

/**
 * Mode of current comment row.
 */
export enum CommentRowMode {
  /**
   * Add new comment.
   */
  ADD,

  /**
   * Edit existing comment.
   */
  EDIT,
}

/**
 * Comment row component to be used in a comment table
 */
@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss'],
})
export class CommentRowComponent implements OnInit {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input()
  mode: CommentRowMode = CommentRowMode.EDIT;

  @Input()
  isVisibilityOptionEnabled: boolean = true;

  @Input()
  isDisabled: boolean = false;

  @Input()
  shouldHideSavingButton: boolean = false;

  @Input()
  model: CommentRowModel = {
    commentEditFormModel: {
      commentText: '',
    },

    isEditing: false,
  };
  @Output()
  modelChange: EventEmitter<CommentRowModel> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<void> = new EventEmitter();
  @Output()
  deleteCommentEvent: EventEmitter<void> = new EventEmitter();
  @Output()
  closeEditingEvent: EventEmitter<void> = new EventEmitter();

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Closes editing of current comment and restore back the original comment.
   */
  triggerCloseEditing(): void {
    this.closeEditingEvent.emit();
  }

  /**
   * Triggers update comment event.
   */
  triggerSaveCommentEvent(): void {
    this.saveCommentEvent.emit();
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(): void {
    const modalRef: NgbModalRef = this.modalService.open(ConfirmDeleteCommentModalComponent);

    modalRef.result.then(() => {
      this.deleteCommentEvent.emit();
    }, () => {});
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit(Object.assign({}, this.model, { [field]: data }));
  }
}
