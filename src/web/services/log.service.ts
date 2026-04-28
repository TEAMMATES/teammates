import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import {
  FeedbackSessionLogs,
  FeedbackSessionLogType,
} from '../types/api-output';

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
    logType: FeedbackSessionLogType,
    feedbackSessionId: string,
    key?: string,
  }): Observable<string> {
    const paramMap: Record<string, string> = {
      fsltype: queryParams.logType.toString(),
      fsid: queryParams.feedbackSessionId,
    };

    if (queryParams.key) {
      paramMap['key'] = queryParams.key;
    }

    return this.httpRequestService.post(ResourceEndpoints.SESSION_LOGS, paramMap);
  }

  /**
   * Searches for feedback session logs.
   */
  searchFeedbackSessionLog(queryParams: {
    courseId: string,
    searchFrom: number,
    searchUntil: number,
    logTypes: FeedbackSessionLogType[],
    studentId?: string,
    sessionId?: string,
  }): Observable<FeedbackSessionLogs> {
    const paramMap: Record<string, string | string[]> = {
      courseid: queryParams.courseId,
      fslstarttime: queryParams.searchFrom.toString(),
      fslendtime: queryParams.searchUntil.toString(),
      fsltype: queryParams.logTypes.map((type) => type.toString()),
    };

    if (queryParams.studentId) {
      paramMap['studentid'] = queryParams.studentId;
    }

    if (queryParams.sessionId) {
      paramMap['fsid'] = queryParams.sessionId;
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSION_LOGS, paramMap);
  }
}
