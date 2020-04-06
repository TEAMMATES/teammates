import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { CommentVisibilityType, FeedbackResponseComment } from '../../../../types/api-output';
import { FeedbackVisibilityType, Intent } from '../../../../types/api-request';
import { CommentVisibilityControl } from '../../../../types/visibility-control';
import { CommentEditFormModel } from '../comment-edit-form/comment-edit-form.component';
import { CommentRowModel } from '../comment-row/comment-row.component';

/**
 * Modal for the comments table.
 */
@Component({
  selector: 'tm-comment-table-modal',
  templateUrl: './comment-table-modal.component.html',
  styleUrls: ['./comment-table-modal.component.scss'],
})
export class CommentTableModalComponent implements OnInit {

  @Input() response: any = '';
  @Input() questionDetails: any = '';
  @Input() comments: FeedbackResponseComment[] = [];
  @Input() timeZone: string = '';
  @Input() showResponsesTo: FeedbackVisibilityType[] = [];

  @Output() commentsChange: EventEmitter<FeedbackResponseComment[]> = new EventEmitter();

  commentsTableModel: CommentRowModel[] = [];
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;
  showResponsesToInCommentVisibilityType: CommentVisibilityType[] = [];

  constructor(private commentService: FeedbackResponseCommentService,
              public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    this.showResponsesToInCommentVisibilityType =
      this.showResponsesTo.map((visibilityType: FeedbackVisibilityType) => {
        switch (visibilityType) {
          case FeedbackVisibilityType.INSTRUCTORS:
            return CommentVisibilityType.INSTRUCTORS;
            break;
          case FeedbackVisibilityType.STUDENTS:
            return CommentVisibilityType.STUDENTS;
            break;
          case FeedbackVisibilityType.RECIPIENT:
            return CommentVisibilityType.RECIPIENT;
            break;
          case FeedbackVisibilityType.GIVER_TEAM_MEMBERS:
            return CommentVisibilityType.GIVER_TEAM_MEMBERS;
            break;
          case FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS:
            return CommentVisibilityType.RECIPIENT_TEAM_MEMBERS;
            break;
          default:
            return CommentVisibilityType.GIVER;
        }
      });
    this.showResponsesToInCommentVisibilityType.push(CommentVisibilityType.GIVER);

    // push the new comment edit form
    this.commentsTableModel.push({
      commentEditFormModel: {
        commentText: '',
        commentVisibility:
            this.getCommentVisibilityMap(this.showResponsesToInCommentVisibilityType,
                this.showResponsesToInCommentVisibilityType),
      },
      timezone: this.timeZone,
      isEditing: true,
    });

    this.comments.forEach((comment: FeedbackResponseComment) => {
      this.commentsTableModel.push({
        originalComment: comment,
        timezone: this.timeZone,
        commentEditFormModel: {
          commentText: comment.commentText,
          commentVisibility: this.getCommentVisibilityMap(comment.showCommentTo, comment.showGiverNameTo),
        },
        isEditing: false,
      });
    });
  }

  /**
   * Triggers the delete comment event.
   */
  triggerDeleteCommentEvent(commentId: number): void {
    this.commentService.deleteComment(commentId, Intent.INSTRUCTOR_RESULT).subscribe(() => {
      this.commentsTableModel = this.commentsTableModel.filter((commentRow: CommentRowModel) =>
          !commentRow.originalComment || commentRow.originalComment.feedbackResponseCommentId !== commentId);
      const updatedComments: FeedbackResponseComment[] =
          this.comments.filter((comment: FeedbackResponseComment) => comment.feedbackResponseCommentId !== commentId);
      this.commentsChange.emit(updatedComments);
    });
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(index: number): void {
    const commentData: CommentRowModel = this.commentsTableModel[index];

    this.commentService.updateComment({
      commentText: commentData.commentEditFormModel.commentText,
      showCommentTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          commentData.commentEditFormModel.commentVisibility, CommentVisibilityControl.SHOW_COMMENT),
      showGiverNameTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          commentData.commentEditFormModel.commentVisibility, CommentVisibilityControl.SHOW_GIVER_NAME),
      // tslint:disable-next-line:no-non-null-assertion
    }, commentData.originalComment!.feedbackResponseCommentId, Intent.INSTRUCTOR_RESULT)
    .subscribe((commentResponse: FeedbackResponseComment) => {
      this.commentsTableModel[index] = {
        originalComment: commentResponse,
        commentEditFormModel: {
          commentText: commentResponse.commentText,
          commentVisibility: this.getCommentVisibilityMap(commentResponse.showCommentTo,
              commentResponse.showGiverNameTo),
        },
        timezone: this.timeZone,
        isEditing: false,
      };

      const updatedComments: FeedbackResponseComment[] = this.comments.slice();
      const commentToUpdateIndex: number =
          updatedComments.findIndex((comment: FeedbackResponseComment) =>
            // tslint:disable-next-line:no-non-null-assertion
            comment.feedbackResponseCommentId === commentData.originalComment!.feedbackResponseCommentId);
      updatedComments[commentToUpdateIndex] = {...updatedComments[commentToUpdateIndex],
        lastEditedAt: commentResponse.lastEditedAt,
        commentText: commentResponse.commentText,
        showCommentTo: commentResponse.showCommentTo,
        showGiverNameTo: commentResponse.showGiverNameTo,
      };
      this.commentsChange.emit(updatedComments);
    });
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(): void {
    const newCommentEditForm: CommentEditFormModel = this.commentsTableModel[0].commentEditFormModel;
    this.commentService.createComment({
      commentText: newCommentEditForm.commentText,
      showCommentTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          newCommentEditForm.commentVisibility, CommentVisibilityControl.SHOW_COMMENT),
      showGiverNameTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          newCommentEditForm.commentVisibility, CommentVisibilityControl.SHOW_GIVER_NAME),
    }, this.response.responseId, Intent.INSTRUCTOR_RESULT)
    .subscribe((commentResponse: FeedbackResponseComment) => {
      this.commentsTableModel.push({
        originalComment: commentResponse,
        timezone: this.timeZone,
        commentEditFormModel: {
          commentText: commentResponse.commentText,
          commentVisibility: this.getCommentVisibilityMap(commentResponse.showCommentTo,
              commentResponse.showGiverNameTo),
        },
        isEditing: false,
      });

      this.commentsTableModel[0] = {
        commentEditFormModel: {
          commentText: '',
          commentVisibility: this.getCommentVisibilityMap(this.showResponsesToInCommentVisibilityType,
              this.showResponsesToInCommentVisibilityType),
        },
        timezone: this.timeZone,
        isEditing: true,
      };

      const updatedComments: FeedbackResponseComment[] = this.comments.slice();
      updatedComments.push(commentResponse);
      this.commentsChange.emit(updatedComments);
    });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(data: any): void {
    this.commentsTableModel[data.index] = data.commentRow;
  }

  /**
   * Triggers the close editing event.
   */
  triggerCloseEditingEvent(index: number): void {
    // tslint:disable-next-line:no-non-null-assertion
    const originalComment: FeedbackResponseComment = this.commentsTableModel[index].originalComment!;
    if (originalComment) {
      this.commentsTableModel[index].commentEditFormModel = {
        commentText: originalComment.commentText,
        commentVisibility: this.getCommentVisibilityMap(originalComment.showCommentTo, originalComment.showGiverNameTo),
      };
    } else {
      this.commentsTableModel[index].commentEditFormModel = {
        commentText: '',
        commentVisibility: this.getCommentVisibilityMap(this.showResponsesToInCommentVisibilityType,
            this.showResponsesToInCommentVisibilityType),
      };
    }
    this.commentsTableModel[index].isEditing = false;
  }

  /**
   * Gets the CommentVisibilityType arrays under certain visibilityControl.
   */
  getCommentVisibilityTypesUnderVisibilityControl(commentVisibility: Map<CommentVisibilityControl,
      Set<CommentVisibilityType>>, commentVisibilityControl: CommentVisibilityControl): CommentVisibilityType[] {
    const visibilityTypes: CommentVisibilityType[] = [];
    // tslint:disable-next-line:no-non-null-assertion
    commentVisibility.get(commentVisibilityControl)!.forEach((visibilityType: CommentVisibilityType) => {
      visibilityTypes.push(visibilityType);
    });
    return visibilityTypes;
  }

  /**
   * Gets the CommentVisibilityType map combined from showCommentTo and showGiverNameTo.
   */

  getCommentVisibilityMap(showCommentTo: CommentVisibilityType[], showGiverNameTo: CommentVisibilityType[]):
      Map<CommentVisibilityControl, Set<CommentVisibilityType>> {
    const commentVisibility: Map<CommentVisibilityControl, Set<CommentVisibilityType>> = new Map();
    commentVisibility.set(CommentVisibilityControl.SHOW_COMMENT, new Set(showCommentTo));
    commentVisibility.set(CommentVisibilityControl.SHOW_GIVER_NAME, new Set(showGiverNameTo));
    return commentVisibility;
  }
}
