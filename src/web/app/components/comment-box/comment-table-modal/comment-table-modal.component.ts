import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { FeedbackResponseComment } from '../../../../types/api-output';
import { Intent } from '../../../Intent';
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
  @Input() comments: FeedbackResponseCommentModel[] = [];

  @Output() commentsChange: EventEmitter<FeedbackResponseCommentModel[]> = new EventEmitter();

  commentTableMode: CommentTableMode = CommentTableMode.INSTRUCTOR_RESULT;

  constructor(private commentService: FeedbackResponseCommentService,
              public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(commentId: number): void {
    this.commentService.deleteComment(commentId).subscribe(() => {
      const updatedComments: FeedbackResponseCommentModel[] =
          this.comments.filter((comment: FeedbackResponseCommentModel) => comment.commentId != commentId);
      this.commentsChange.emit(updatedComments);
    });
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(commentData: any): void {
    this.commentService.updateComment(commentData.commentId, commentData.commentText, Intent.INSTRUCTOR_RESULT)
        .subscribe((comment: FeedbackResponseComment) => {
          const updatedComments: FeedbackResponseCommentModel[] = this.comments.slice();
          const commentToUpdateIndex: number =
              updatedComments.findIndex((comment: FeedbackResponseCommentModel) =>
                  comment.commentId === commentData.commentId);
          updatedComments[commentToUpdateIndex] = {...updatedComments[commentToUpdateIndex],
            editedAt: comment.updatedAt,
            commentText: comment.commentText,
          };
          this.commentsChange.emit(updatedComments);
        });
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(commentText: string): void {
    this.commentService.saveComment(this.response.responseId, commentText, Intent.INSTRUCTOR_RESULT)
        .subscribe((comment: FeedbackResponseComment) => {
          const updatedComments: FeedbackResponseCommentModel[] = this.comments.slice();
          updatedComments.push({
            commentId: comment.feedbackResponseCommentId,
            createdAt: comment.createdAt,
            editedAt: comment.updatedAt,
            //TODO CHANGE THIS!!!
            timeZone: 'Asia/Singapore',
            commentGiver: comment.commentGiver,
            commentText: comment.commentText,
            isEditable: true,
          });
          this.commentsChange.emit(updatedComments);
        });
  }
}
