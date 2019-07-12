import { Component, Input, OnInit } from '@angular/core';
import { ResponseCommentOutput } from '../../../../types/api-output';
import { CommentTableMode, FeedbackResponseCommentModel } from '../../comment-box/comment-table/comment-table-model';

/**
 * Feedback response in student results page view.
 */
@Component({
  selector: 'tm-student-view-responses',
  templateUrl: './student-view-responses.component.html',
  styleUrls: ['./student-view-responses.component.scss'],
})
export class StudentViewResponsesComponent implements OnInit {
  private _responses: any[] = [];

  @Input() questionDetails: any = {};
  @Input('responses')
  set responses(value: any[]) {
    this._responses = value;
    this._responses.forEach((response: any) => {
      if (!response.allComments) {
        return;
      }
      response.allComments = response.allComments.map((comment: any) => {
        return this.mapComments(comment)
      });
    });
  }

  get responses(): any[] {
    return this._responses;
  }

  @Input() isSelfResponses: boolean = false;

  // enum
  CommentTableMode: typeof CommentTableMode = CommentTableMode;

  recipient: string = '';

  constructor() { }

  ngOnInit(): void {
    this.recipient = this.responses.length ? this.responses[0].recipient : '';
  }

  /**
   * Maps a comments from ResponseCommentOutput to FeedbackResponseCommentModel
   * @param comment
   */
  mapComments(comment: ResponseCommentOutput): FeedbackResponseCommentModel {
    return {
      commentId: comment.commentId,
      createdAt: comment.createdAt,
      editedAt: comment.updatedAt,
      timeZone: comment.timezone,
      commentGiver: comment.commentGiver,
      commentText: comment.commentText,
      isEditable: true,
    };
  }
}
