import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { FeedbackResponseComment, FeedbackVisibilityType } from '../types/api-output';
import {
  FeedbackResponseCommentCreateRequest,
  FeedbackResponseCommentUpdateRequest, Intent,
} from '../types/api-request';
import { CommentVisibilityStateMachine } from './comment-visibility-state-machine';
import { HttpRequestService } from './http-request.service';

/**
 * Handles requests to the back-end related to response comments.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackResponseCommentService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Create a comment by calling API.
   */
  createComment(createRequest: FeedbackResponseCommentCreateRequest,
                responseId: string, intent: Intent,
                additionalParams: { [key: string]: string } = {}): Observable<FeedbackResponseComment> {
    return this.httpRequestService.post(ResourceEndpoints.RESPONSE_COMMENT, {
      intent,
      responseid: responseId,
      ...additionalParams,
    }, createRequest);
  }

  /**
   * Updates a comment by calling API.
   */
  updateComment(updateRequest: FeedbackResponseCommentUpdateRequest,
                commentId: number, intent: Intent,
                additionalParams: { [key: string]: string } = {}): Observable<FeedbackResponseComment> {
    return this.httpRequestService.put(ResourceEndpoints.RESPONSE_COMMENT, {
      intent,
      responsecommentid: commentId.toString(),
      ...additionalParams,
    }, updateRequest);
  }

  /**
   * Deletes a comment by calling API.
   */
  deleteComment(commentId: number, intent: Intent, additionalParams: { [key: string]: string } = {}): Observable<any> {
    return this.httpRequestService.delete(ResourceEndpoints.RESPONSE_COMMENT, {
      intent,
      responsecommentid: commentId.toString(),
      ...additionalParams,
    });
  }

  /**
   * Gets a state machine of comment visibility settings for a certain question.
   */
  getNewVisibilityStateMachine(questionShowResponsesTo: FeedbackVisibilityType[]): CommentVisibilityStateMachine {
    return new CommentVisibilityStateMachine(questionShowResponsesTo);
  }
}
