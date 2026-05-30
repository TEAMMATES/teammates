import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentVisibilityStateMachine } from './comment-visibility-state-machine';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { FeedbackResponseComment, FeedbackVisibilityType, MessageOutput } from '../types/api-output';
import { FeedbackResponseCommentCreateRequest, FeedbackResponseCommentUpdateRequest } from '../types/api-request';

/**
 * Handles requests to the back-end related to response comments.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackResponseCommentService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Create a comment by calling API.
   */
  createComment(
    createRequest: FeedbackResponseCommentCreateRequest,
    responseId: string,
  ): Observable<FeedbackResponseComment> {
    return this.httpRequestService.post(
      ResourceEndpoints.RESPONSE_COMMENT,
      {
        responseid: responseId,
      },
      createRequest,
    );
  }

  /**
   * Updates a comment by calling API.
   */
  updateComment(
    updateRequest: FeedbackResponseCommentUpdateRequest,
    commentId: string,
  ): Observable<FeedbackResponseComment> {
    return this.httpRequestService.put(
      ResourceEndpoints.RESPONSE_COMMENT,
      {
        responsecommentid: commentId,
      },
      updateRequest,
    );
  }

  /**
   * Deletes a comment by calling API.
   */
  deleteComment(commentId: string): Observable<MessageOutput> {
    return this.httpRequestService.delete(ResourceEndpoints.RESPONSE_COMMENT, {
      responsecommentid: commentId,
    });
  }

  /**
   * Gets a state machine of comment visibility settings for a certain question.
   */
  getNewVisibilityStateMachine(questionShowResponsesTo: FeedbackVisibilityType[]): CommentVisibilityStateMachine {
    return new CommentVisibilityStateMachine(questionShowResponsesTo);
  }
}
