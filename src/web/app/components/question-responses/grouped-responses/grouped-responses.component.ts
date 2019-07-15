import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { FeedbackResponseComment } from '../../../../types/api-output';
import { FeedbackVisibilityType } from '../../../../types/api-request';
import { Intent } from '../../../Intent';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { CommentTableMode, FeedbackResponseCommentModel } from '../../comment-box/comment-table/comment-table-model';

/**
 * A list of responses grouped in GRQ/RGQ mode.
 */
@Component({
  selector: 'tm-grouped-responses',
  templateUrl: './grouped-responses.component.html',
  styleUrls: ['./grouped-responses.component.scss'],
})
export class GroupedResponsesComponent implements OnInit, OnChanges {

  @Input() responses: any = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() timeZone: string = '';
  @Input() isGrq: boolean = true;
  @Input() header: string = '';

  @Output() commentsChangeInResponseEvent: EventEmitter<any> = new EventEmitter();

  // enum
  CommentTableMode: typeof CommentTableMode = CommentTableMode;

  constructor(private commentService: FeedbackResponseCommentService) { }

  ngOnInit(): void {
    this.mapComments();
  }

  ngOnChanges(): void {
    this.mapComments();
  }

  /**
   * Maps comments from {@link ResponseCommentOutput} to {@link FeedbackResponseCommentModel}
   */
  mapComments(): void {
    this.responses.forEach((question: any, questionIndex: number) => {
      question.allResponses.forEach((response: any, responseIndex: number) => {
        this.responses[questionIndex].allResponses[responseIndex].mappedComments =
            response.allComments.map((comment: any) => {
              return {
                commentId: comment.commentId,
                createdAt: comment.createdAt,
                editedAt: comment.updatedAt,
                timeZone: comment.timezone,
                commentGiver: comment.commentGiver,
                commentText: comment.commentText,
              };
            });
      });
    });
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(commentId: number, response: any): void {
    this.commentService.deleteComment(commentId).subscribe(() => {
      const updatedResponse: any = { ...response.allResponses[0], allComments:
            response.allResponses[0].allComments.filter((comment: FeedbackResponseCommentModel) =>
                comment.commentId !== commentId),
      };
      this.commentsChangeInResponseEvent.emit(updatedResponse);
    });
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(commentData: any, response: any): void {
    // TODO set visibility options from user input
    const showCommentTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];
    const showGiverNameTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];

    this.commentService.updateComment(commentData.commentId, commentData.commentText, Intent.INSTRUCTOR_RESULT,
        showCommentTo, showGiverNameTo)
        .subscribe((commentResponse: FeedbackResponseComment) => {
          const updatedComments: any[] = response.allResponses[0].allComments.slice();
          const commentToUpdateIndex: number =
              updatedComments.findIndex((comment: any) =>
                  comment.commentId === commentData.commentId);
          updatedComments[commentToUpdateIndex] = {...updatedComments[commentToUpdateIndex],
            editedAt: commentResponse.updatedAt,
            commentText: commentResponse.commentText,
          };
          const updatedResponse: any = { ...response.allResponses[0], allComments: updatedComments };
          this.commentsChangeInResponseEvent.emit(updatedResponse);
        });
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(commentText: string, response: any): void {
    // TODO set visibility options from user input
    const showCommentTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];
    const showGiverNameTo: FeedbackVisibilityType[] = [FeedbackVisibilityType.GIVER, FeedbackVisibilityType.RECIPIENT];

    this.commentService.saveComment(response.allResponses[0].responseId, commentText,
        Intent.INSTRUCTOR_RESULT, showCommentTo, showGiverNameTo)
        .subscribe((comment: FeedbackResponseComment) => {
          const updatedComments: any[] = response.allResponses[0].allComments.slice();
          updatedComments.push({
            commentId: comment.feedbackResponseCommentId,
            createdAt: comment.createdAt,
            updatedAt: comment.updatedAt,
            timezone: this.timeZone,
            commentGiver: comment.commentGiver,
            commentText: comment.commentText,
            isFromFeedbackParticipant: true,
          });
          const updatedResponse: any = { ...response.allResponses[0], allComments: updatedComments };
          this.commentsChangeInResponseEvent.emit(updatedResponse);
        });
  }
}
