import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { FeedbackResponseComment } from '../../../../types/api-output';
import { FeedbackVisibilityType } from '../../../../types/api-request';
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
  @Input() timeZone: string = '';

  @Output() commentsChange: EventEmitter<FeedbackResponseCommentModel[]> = new EventEmitter();

  commentTableMode: CommentTableMode = CommentTableMode.INSTRUCTOR_RESULT;

  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;

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
          this.comments.filter((comment: FeedbackResponseCommentModel) => comment.commentId !== commentId);
      this.commentsChange.emit(updatedComments);
    });
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(commentData: any): void {
    // TODO set visibility options from user input
    const showCommentTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];
    const showGiverNameTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];

    this.commentService.updateComment(commentData.commentId, commentData.commentText, Intent.INSTRUCTOR_RESULT,
        showCommentTo, showGiverNameTo)
        .subscribe((commentResponse: FeedbackResponseComment) => {
          const updatedComments: FeedbackResponseCommentModel[] = this.comments.slice();
          const commentToUpdateIndex: number =
              updatedComments.findIndex((comment: FeedbackResponseCommentModel) =>
                  comment.commentId === commentData.commentId);
          updatedComments[commentToUpdateIndex] = {...updatedComments[commentToUpdateIndex],
            editedAt: commentResponse.updatedAt,
            commentText: commentResponse.commentText,
          };
          this.commentsChange.emit(updatedComments);
        });
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(commentText: string): void {
    // TODO set visibility options from user input
    const showCommentTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];
    const showGiverNameTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];

    this.commentService.saveComment(this.response.responseId, commentText,
        Intent.INSTRUCTOR_RESULT, showCommentTo, showGiverNameTo)
        .subscribe((comment: FeedbackResponseComment) => {
          const updatedComments: FeedbackResponseCommentModel[] = this.comments.slice();
          updatedComments.push({
            showCommentTo,
            showGiverNameTo,
            commentId: comment.feedbackResponseCommentId,
            createdAt: comment.createdAt,
            editedAt: comment.updatedAt,
            timeZone: this.timeZone,
            commentGiver: comment.commentGiver,
            commentText: comment.commentText,
          });
          this.commentsChange.emit(updatedComments);
        });
  }
}
