import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { default as templateSessions } from '../data/template-sessions.json';
import { FeedbackQuestion, FeedbackSession, OngoingSessions } from '../types/api-output';
import { FeedbackSessionCreateRequest, FeedbackSessionSaveRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * A template session.
 */
export interface TemplateSession {
  name: string;
  questions: FeedbackQuestion[];
}

/**
 * Handles sessions related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackSessionsService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Gets template sessions.
   */
  getTemplateSessions(): TemplateSession[] {
    return templateSessions;
  }

  /**
   * Creates a feedback session by calling API.
   */
  createFeedbackSession(courseId: string, request: FeedbackSessionCreateRequest): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = { courseid: courseId };
    return this.httpRequestService.post('/session', paramMap, request);
  }

  /**
   * Updates a feedback session by calling API.
   */
  updateFeedbackSession(
      courseId: string, feedbackSessionName: string, request: FeedbackSessionSaveRequest): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = { courseid: courseId, fsname: feedbackSessionName };
    return this.httpRequestService.put('/session', paramMap, request);
  }

  /**
   * Gets all ongoing session by calling API.
   */
  getOngoingSessions(startTime: number, endTime: number): Observable<OngoingSessions> {
    const paramMap: { [key: string]: string } = {
      starttime: String(startTime),
      endtime: String(endTime),
    };
    return this.httpRequestService.get('/sessions/ongoing', paramMap);
  }
}
