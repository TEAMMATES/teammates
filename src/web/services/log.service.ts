import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import {
  FeedbackSessionLogs,
  FeedbackSessionLogType,
} from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles logging related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class LogService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Creates a log for feedback session by calling API.
   */
  createFeedbackSessionLog(queryParams: {
    courseId: string,
    feedbackSessionName: string,
    studentEmail: string,
    logType: FeedbackSessionLogType,
    feedbackSessionId?: string,
    studentId?: string,
  }): Observable<string> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      fsname: queryParams.feedbackSessionName,
      studentemail: queryParams.studentEmail,
      fsltype: queryParams.logType.toString(),
    };

    if (queryParams.feedbackSessionId) {
      paramMap['fsid'] = queryParams.feedbackSessionId;
    }

    if (queryParams.studentId) {
      paramMap['studentid'] = queryParams.studentId;
    }

    return this.httpRequestService.post(ResourceEndpoints.SESSION_LOGS, paramMap);
  }

  /**
   * Searches for feedback session logs.
   */
  searchFeedbackSessionLog(queryParams: {
    courseId: string,
    searchFrom: string,
    searchUntil: string,
    studentEmail?: string,
    sessionName?: string,
    logType?: string,
    studentId?: string,
    sessionId?: string,
  }): Observable<FeedbackSessionLogs> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      fslstarttime: queryParams.searchFrom,
      fslendtime: queryParams.searchUntil,
    };

    if (queryParams.studentEmail) {
      paramMap['studentemail'] = queryParams.studentEmail;
    }

    if (queryParams.sessionName) {
      paramMap['fsname'] = queryParams.sessionName;
    }

    if (queryParams.logType) {
      paramMap['fsltype'] = queryParams.logType;
    }

    if (queryParams.studentId) {
      paramMap['studentid'] = queryParams.studentId;
    }

    if (queryParams.sessionId) {
      paramMap['fsid'] = queryParams.sessionId;
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSION_LOGS, paramMap);
  }
}
