import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Intent } from '../app/Intent';
import { FeedbackVisibilityType } from '../types/api-request';
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
  saveComment(responseId: string, commentText: string, intent: Intent,
              showCommentTo?: FeedbackVisibilityType[], showGiverNameTo?: FeedbackVisibilityType[]): Observable<any> {
    return this.httpRequestService.post('/responsecomment', {
      intent,
      responseid: responseId,
    }, {
      commentText,
      showCommentTo: showCommentTo ? showCommentTo : [],
      showGiverNameTo: showGiverNameTo ? showGiverNameTo : [],
    });
  }

  /**
   * Updates a comment by calling API.
   */
  updateComment(commentId: number, commentText: string, intent: Intent,
                showCommentTo?: FeedbackVisibilityType[], showGiverNameTo?: FeedbackVisibilityType[]): Observable<any> {
    return this.httpRequestService.put('/responsecomment', {
      intent,
      responsecommentid: commentId.toString(),
    }, {
      commentText,
      showCommentTo: showCommentTo ? showCommentTo : [],
      showGiverNameTo: showGiverNameTo ? showGiverNameTo : [],
    });
  }

  /**
   * Deletes a comment by calling API.
   */
  deleteComment(commentId: any): Observable<any> {
    return this.httpRequestService.delete('/responsecomment', {
      responsecommentid: commentId,
    });
  }

  /**
   * Loads comments for a feedback response by calling API.
   */
  loadCommentsForResponse(responseId: string, intent: Intent): Observable<any> {
    return this.httpRequestService.get('/responsecomment', {
      intent,
      responseid: responseId,
    });
  }
}
