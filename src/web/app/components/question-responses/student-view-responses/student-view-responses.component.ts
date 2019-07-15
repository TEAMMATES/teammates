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
  @Input() questionDetails: any = {};
  @Input() isSelfResponses: boolean = false;

  @Input('responses')
  set responses(value: any[]) {
    this._responses = value;
    this._responses.forEach((response: any) => {
      if (!response.allComments) {
        return;
      }
      response.allComments = response.allComments.map((comment: any) => {
        return this.mapComments(comment);
      });
    });
  }

  get responses(): any[] {
    return this._responses;
  }

  // enum
  CommentTableMode: typeof CommentTableMode = CommentTableMode;

  recipient: string = '';

  private _responses: any[] = [];

  constructor() { }

  ngOnInit(): void {
    this.recipient = this.responses.length ? this.responses[0].recipient : '';
  }

  /**
   * Maps a comments from {@link ResponseCommentOutput} to {@link FeedbackResponseCommentModel}.
   */
  mapComments(comment: ResponseCommentOutput): FeedbackResponseCommentModel {
    return {
      commentId: comment.commentId,
      createdAt: comment.createdAt,
      editedAt: comment.updatedAt,
      timeZone: comment.timezone,
      commentGiver: comment.commentGiver,
      commentText: comment.commentText,
      showCommentTo: [],
      showGiverNameTo: [],
    };
  }
}
