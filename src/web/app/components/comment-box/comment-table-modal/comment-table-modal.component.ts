import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommentTableMode, FeedbackResponseCommentModel } from '../comment-table/comment-table-model';

/**
 * Modal for the comments table
 */
@Component({
  selector: 'tm-comment-table-modal',
  templateUrl: './comment-table-modal.component.html',
  styleUrls: ['./comment-table-modal.component.scss'],
})
export class CommentTableModalComponent implements OnInit {

  @Input() response: any = '';
  @Input() questionDetails: any = '';

  comments: FeedbackResponseCommentModel[] = [];
  commentTableMode: CommentTableMode = CommentTableMode.INSTRUCTOR_RESULT;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }
  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(): void {
    // TODO
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(): void {
    // TODO
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(): void {
    // TODO
  }
}
