import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { default as templateSessions } from '../data/template-sessions.json';
import { ResourceEndpoints } from '../types/api-endpoints';
import {
  FeedbackQuestion,
  FeedbackSession,
  FeedbackSessions, FeedbackSessionStats, FeedbackSessionSubmittedGiverSet,
  HasResponses,
  MessageOutput,
  OngoingSessions,
} from '../types/api-output';
import {
  FeedbackSessionCreateRequest,
  FeedbackSessionStudentRemindRequest,
  FeedbackSessionUpdateRequest, Intent,
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
   * Retrieves a feedback session by calling API.
   */
  getFeedbackSession(courseId: string, feedbackSessionName: string, intent: Intent): Observable<FeedbackSession> {
    // load feedback session
    const paramMap: { [key: string]: string } = {
      intent,
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.get('/session', paramMap);
  }

  /**
   * Creates a feedback session by calling API.
   */
  createFeedbackSession(courseId: string, request: FeedbackSessionCreateRequest): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = { courseid: courseId };
    return this.httpRequestService.post(ResourceEndpoints.SESSION, paramMap, request);
  }

  /**
   * Updates a feedback session by calling API.
   */
  updateFeedbackSession(courseId: string, feedbackSessionName: string, request: FeedbackSessionUpdateRequest):
      Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = { courseid: courseId, fsname: feedbackSessionName };
    return this.httpRequestService.put(ResourceEndpoints.SESSION, paramMap, request);
  }

  /**
   * Deletes a feedback session by calling API.
   */
  deleteFeedbackSession(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = { courseid: courseId, fsname: feedbackSessionName };
    return this.httpRequestService.delete(ResourceEndpoints.SESSION, paramMap);
  }

  /**
   * Gets all ongoing session by calling API.
   */
  getOngoingSessions(startTime: number, endTime: number): Observable<OngoingSessions> {
    const paramMap: { [key: string]: string } = {
      starttime: String(startTime),
      endtime: String(endTime),
    };
    return this.httpRequestService.get(ResourceEndpoints.SESSIONS_ONGOING, paramMap);
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

    return this.httpRequestService.get(ResourceEndpoints.SESSIONS, paramMap);
  }

  /**
   * Gets all sessions in the recycle bin for the instructor by calling API.
   */
  getFeedbackSessionsInRecycleBinForInstructor(): Observable<FeedbackSessions> {

    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      isinrecyclebin: 'true',
    };

    return this.httpRequestService.get(ResourceEndpoints.SESSIONS, paramMap);
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

    return this.httpRequestService.get(ResourceEndpoints.SESSIONS, paramMap);
  }

  /**
   * Checks if there are responses for a specific question in a feedback session (request sent by instructor).
   */
  hasResponsesForQuestion(questionId: string): Observable<HasResponses> {
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      questionid: questionId,
    };
    return this.httpRequestService.get(ResourceEndpoints.HAS_RESPONSES, paramMap);
  }

  /**
   * Checks if there is response of a student for a feedback session (request sent by student).
   */
  hasStudentResponseForFeedbackSession(courseId: string, feedbackSessionName: string): Observable<HasResponses> {
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
      courseid: courseId,
      fsname: feedbackSessionName,

    };
    return this.httpRequestService.get(ResourceEndpoints.HAS_RESPONSES, paramMap);
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

    return this.httpRequestService.post(ResourceEndpoints.SESSION_REMIND_SUBMISSION, paramMap, request);
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

    return this.httpRequestService.post(ResourceEndpoints.SESSION_REMIND_RESULT, paramMap, request);
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

    return this.httpRequestService.get(ResourceEndpoints.SESSION_SUBMITTED_GIVER_SET, paramMap);
  }

  /**
   * publishes a feedback session.
   */
  publishFeedbackSession(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishFeedbackSession(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.delete(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  }

  /**
   * Load session statistics.
   */
  loadSessionStatistics(courseId: string, feedbackSessionName: string): Observable<FeedbackSessionStats> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.get(ResourceEndpoints.SESSION_STATS, paramMap);
  }

  moveSessionToRecycleBin(courseId: string, feedbackSessionName: string): Observable<any> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.put(ResourceEndpoints.BIN_SESSION, paramMap);
  }

  deleteSessionFromRecycleBin(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.delete(ResourceEndpoints.BIN_SESSION, paramMap);
  }
}
