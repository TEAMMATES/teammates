import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { default as templateSessions } from '../data/template-sessions.json';
import {
  FeedbackQuestion,
  FeedbackSession,
  FeedbackSessions, FeedbackSessionSubmittedGiverSet,
  HasResponses,
  MessageOutput,
  OngoingSessions,
} from '../types/api-output';
import {
  FeedbackSessionCreateRequest,
  FeedbackSessionStudentRemindRequest,
  FeedbackSessionUpdateRequest,
} from '../types/api-request';
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

  constructor(private httpRequestService: HttpRequestService) {
  }

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
  updateFeedbackSession(courseId: string, feedbackSessionName: string, request: FeedbackSessionUpdateRequest):
      Observable<FeedbackSession> {
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

  /**
   * Gets all sessions for the instructor by calling API.
   */
  getFeedbackSessionsForInstructor(courseId?: string): Observable<FeedbackSessions> {

    let paramMap: { [key: string]: string };
    if (courseId != null) {
      paramMap = {
        entitytype: 'instructor',
        courseid: courseId,
      };
    } else {
      paramMap = {
        entitytype: 'instructor',
        isinrecyclebin: 'false',
      };
    }

    return this.httpRequestService.get('/sessions', paramMap);
  }

  /**
   * Gets all sessions in the recycle bin for the instructor by calling API.
   */
  getFeedbackSessionsInRecycleBinForInstructor(): Observable<FeedbackSessions> {

    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      isinrecyclebin: 'true',
    };

    return this.httpRequestService.get('/sessions', paramMap);
  }

  /**
   * Gets all sessions for the student by calling API.
   */
  getFeedbackSessionsForStudent(courseId?: string): Observable<FeedbackSessions> {

    let paramMap: { [key: string]: string };
    if (courseId != null) {
      paramMap = {
        entitytype: 'student',
        courseid: courseId,
      };
    } else {
      paramMap = {
        entitytype: 'student',
      };
    }

    return this.httpRequestService.get('/sessions', paramMap);
  }

  /**
   * Checks if there are responses for a specific question in a feedback session.
   */
  hasResponsesForQuestion(questionId: string): Observable<HasResponses> {
    const paramMap: { [key: string]: string } = {
      questionid: questionId,
    };
    return this.httpRequestService.get('/hasResponses', paramMap);
  }

  /**
   * Sends e-mails to remind students who have not submitted their feedback.
   */
  remindFeedbackSessionSubmissionForStudent(
      courseId: string, feedbackSessionName: string, request: FeedbackSessionStudentRemindRequest)
      : Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post('/session/remind/submission', paramMap, request);
  }

  /**
   * Sends e-mails to remind students on the published results link.
   */
  remindResultsLinkToStudents(
      courseId: string, feedbackSessionName: string, request: FeedbackSessionStudentRemindRequest)
      : Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post('/session/remind/result', paramMap, request);
  }

  /**
   * Gets a set of givers that has given at least one response in the feedback session.
   */
  getFeedbackSessionSubmittedGiverSet(
      courseId: string, feedbackSessionName: string): Observable<FeedbackSessionSubmittedGiverSet> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.get('/session/submitted/giverset', paramMap);
  }
}
