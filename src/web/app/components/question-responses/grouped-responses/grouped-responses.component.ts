import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { CommentVisibilityType, FeedbackResponseComment } from '../../../../types/api-output';
import { FeedbackVisibilityType, Intent } from '../../../../types/api-request';
import { CommentVisibilityControl } from '../../../../types/visibility-control';
// tslint:disable-next-line:max-line-length
import { InstructorSessionResultSectionType } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { CommentEditFormModel } from '../../comment-box/comment-edit-form/comment-edit-form.component';
import { CommentRowModel } from '../../comment-box/comment-row/comment-row.component';

/**
 * A list of responses grouped in GRQ/RGQ mode.
 */
@Component({
  selector: 'tm-grouped-responses',
  templateUrl: './grouped-responses.component.html',
  styleUrls: ['./grouped-responses.component.scss'],
})
export class GroupedResponsesComponent implements OnInit {

  @Input() responses: any = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;

  @Input() isGrq: boolean = true;
  @Input() header: string = '';
  @Input() session: any = {};
  @Input() relatedGiverEmail: string = '';

  @Output() commentsChangeInResponse: EventEmitter<any> = new EventEmitter();

  showResponsesToInCommentVisibilityType: CommentVisibilityType[][] = [];
  commentsTablesModel: CommentRowModel[][] = [];
  isNewCommentRowExpanded: boolean = false;

  constructor(private commentService: FeedbackResponseCommentService) { }

  ngOnInit(): void {
    this.responses.forEach((response: any, i: number) => {
      const comments: FeedbackResponseComment[] = response.allResponses[0].allComments;

      this.showResponsesToInCommentVisibilityType[i] =
          response.showResponsesTo.map((visibilityType: FeedbackVisibilityType) => {
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

      this.showResponsesToInCommentVisibilityType[i].push(CommentVisibilityType.GIVER);

      // push the new comment edit form
      this.commentsTablesModel[i] = [({
        commentEditFormModel: {
          commentText: '',
          commentVisibility:
              this.getCommentVisibilityMap(this.showResponsesToInCommentVisibilityType[i],
                  this.showResponsesToInCommentVisibilityType[i]),
        },
        timezone: this.session.timeZone,
        isEditing: true,
      })];

      comments.forEach((comment: FeedbackResponseComment) => {
        this.commentsTablesModel[i].push({
          originalComment: comment,
          timezone: this.session.timeZone,
          commentEditFormModel: {
            commentText: comment.commentText,
            commentVisibility: this.getCommentVisibilityMap(comment.showCommentTo, comment.showGiverNameTo),
          },
          isEditing: false,
        });
      });
    });
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(commentId: number, i: number): void {
    this.commentService.deleteComment(commentId, Intent.INSTRUCTOR_RESULT).subscribe(() => {
      this.commentsTablesModel[i] = this.commentsTablesModel[i].filter((commentRow: CommentRowModel) =>
          !commentRow.originalComment || commentRow.originalComment.feedbackResponseCommentId !== commentId);
      const updatedResponse: any = {
        ...this.responses[i].allResponses[0],
        allComments: this.responses[i].allResponses[0].allComments.filter((comment: FeedbackResponseComment) =>
                comment.feedbackResponseCommentId !== commentId),
      };
      this.commentsChangeInResponse.emit(updatedResponse);
    });
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(index: number, i: number): void {

    const commentData: CommentRowModel = this.commentsTablesModel[i][index];

    this.commentService.updateComment({
      commentText: commentData.commentEditFormModel.commentText,
      showCommentTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          commentData.commentEditFormModel.commentVisibility, CommentVisibilityControl.SHOW_COMMENT),
      showGiverNameTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          commentData.commentEditFormModel.commentVisibility, CommentVisibilityControl.SHOW_GIVER_NAME),
      // tslint:disable-next-line:no-non-null-assertion
    }, commentData.originalComment!.feedbackResponseCommentId, Intent.INSTRUCTOR_RESULT)
        .subscribe((commentResponse: FeedbackResponseComment) => {

          this.commentsTablesModel[i][index] = {
            originalComment: commentResponse,
            commentEditFormModel: {
              commentText: commentResponse.commentText,
              commentVisibility: this.getCommentVisibilityMap(commentResponse.showCommentTo,
                  commentResponse.showGiverNameTo),
            },
            timezone: this.session.timeZone,
            isEditing: false,
          };

          const updatedComments: FeedbackResponseComment[] = this.responses[i].allResponses[0].allComments.slice();
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
          const updatedResponse: any = { ...this.responses[i].allResponses[0], allComments: updatedComments };
          this.commentsChangeInResponse.emit(updatedResponse);
        });
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(i: number): void {
    const newCommentEditForm: CommentEditFormModel = this.commentsTablesModel[i][0].commentEditFormModel;
    this.commentService.createComment({
      commentText: newCommentEditForm.commentText,
      showCommentTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          newCommentEditForm.commentVisibility, CommentVisibilityControl.SHOW_COMMENT),
      showGiverNameTo: this.getCommentVisibilityTypesUnderVisibilityControl(
          newCommentEditForm.commentVisibility, CommentVisibilityControl.SHOW_GIVER_NAME),
    }, this.responses[i].allResponses[0].responseId, Intent.INSTRUCTOR_RESULT)
        .subscribe((commentResponse: FeedbackResponseComment) => {
          this.commentsTablesModel[i].push({
            originalComment: commentResponse,
            timezone: this.session.timeZone,
            commentEditFormModel: {
              commentText: commentResponse.commentText,
              commentVisibility: this.getCommentVisibilityMap(commentResponse.showCommentTo,
                  commentResponse.showGiverNameTo),
            },
            isEditing: false,
          });

          this.commentsTablesModel[i][0]  = {
            commentEditFormModel: {
              commentText: '',
              commentVisibility: this.getCommentVisibilityMap(this.showResponsesToInCommentVisibilityType[i],
                  this.showResponsesToInCommentVisibilityType[i]),
            },
            timezone: this.session.timeZone,
            isEditing: true,
          };

          const updatedComments: FeedbackResponseComment[] = this.responses[i].allResponses[0].allComments.slice();
          updatedComments.push(commentResponse);

          const updatedResponse: any = { ...this.responses[i].allResponses[0], allComments: updatedComments };
          this.commentsChangeInResponse.emit(updatedResponse);
        });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(data: any, i: number): void {
    this.commentsTablesModel[i][data.index] = data.commentRow;
  }

  /**
   * Triggers the close editing event.
   */
  triggerCloseEditingEvent(index: number, i: number): void {
    // tslint:disable-next-line:no-non-null-assertion
    const originalComment: FeedbackResponseComment = this.commentsTablesModel[i][index].originalComment!;
    if (originalComment) {
      this.commentsTablesModel[i][index].commentEditFormModel = {
        commentText: originalComment.commentText,
        commentVisibility: this.getCommentVisibilityMap(originalComment.showCommentTo,
            originalComment.showGiverNameTo),
      };
    } else {
      this.commentsTablesModel[i][index].commentEditFormModel = {
        commentText: '',
        commentVisibility: this.getCommentVisibilityMap(this.showResponsesToInCommentVisibilityType[i],
            this.showResponsesToInCommentVisibilityType[i]),
      };
    }
    this.commentsTablesModel[i][index].isEditing = false;
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

  /**
   * Toggles the add new comment row.
   */
  triggerNewCommentRow(): void {
    this.isNewCommentRowExpanded = !this.isNewCommentRowExpanded;
  }
}
