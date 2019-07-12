import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {
  ConfirmDeleteCommentModalComponent,
} from '../confirm-delete-comment-modal/confirm-delete-comment-modal.component';
import { CommentModelDefaultValues, CommentTableMode, FeedbackResponseCommentModel } from './comment-table-model';

/**
 * Component for the comments table
 */
@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss'],
})
export class CommentTableComponent implements OnInit {

  // enum
  CommentTableMode: typeof CommentTableMode = CommentTableMode;

  @Input() commentTableMode: CommentTableMode = CommentTableMode.SESSION_SUBMISSION;

  @Input() comments: FeedbackResponseCommentModel[] = [];
  @Input() comment: FeedbackResponseCommentModel = {
    commentId: CommentModelDefaultValues.INVALID_VALUE,
    createdAt: CommentModelDefaultValues.INVALID_VALUE,
    commentGiver: '',
    commentText: '',
    showGiverNameTo: [],
    showCommentTo: [],
    timeZone: '',
    isEditable: true,
  };

  @Output() saveNewCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() deleteCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() updateCommentEvent: EventEmitter<any> = new EventEmitter();

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(commentId: number): void {
    const modalRef: NgbModalRef = this.modalService.open(ConfirmDeleteCommentModalComponent);

    modalRef.result.then(() => {
      this.deleteCommentEvent.emit(commentId);
    }, () => {});
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(commentText: any, index?: number): void {
    if (index !== undefined) {
      this.updateCommentEvent.emit({ commentText, commentId: this.comments[index].commentId });
    } else {
      const updatedComment: FeedbackResponseCommentModel = { ...this.comment, commentText };
      this.updateCommentEvent.emit(updatedComment);
    }
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(commentText: any): void {
    this.saveNewCommentEvent.emit(commentText);
  }
}
