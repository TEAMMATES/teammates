import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { FeedbackSessionLogs, FeedbackSessionLogType } from '../types/api-output';

/**
 * Handles logging related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class LogService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Creates a log for feedback session by calling API.
   */
  createFeedbackSessionLog(queryParams: {
    logType: FeedbackSessionLogType;
    feedbackSessionId: string;
    key?: string;
  }): Observable<string> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_LOG_TYPE]: queryParams.logType.toString(),
      [QueryParamKeys.FEEDBACK_SESSION_ID]: queryParams.feedbackSessionId,
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
    courseId: string;
    searchFrom: number;
    searchUntil: number;
    logTypes: FeedbackSessionLogType[];
    userId?: string;
    sessionId?: string;
  }): Observable<FeedbackSessionLogs> {
    const paramMap: Record<string, string | string[]> = {
      courseid: queryParams.courseId,
      [QueryParamKeys.FEEDBACK_SESSION_LOG_START_TIME]: queryParams.searchFrom.toString(),
      [QueryParamKeys.FEEDBACK_SESSION_LOG_END_TIME]: queryParams.searchUntil.toString(),
      [QueryParamKeys.FEEDBACK_SESSION_LOG_TYPE]: queryParams.logTypes.map((type) => type.toString()),
    };

    if (queryParams.userId) {
      paramMap['userid'] = queryParams.userId;
    }

    if (queryParams.sessionId) {
      paramMap[QueryParamKeys.FEEDBACK_SESSION_ID] = queryParams.sessionId;
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSION_LOGS, paramMap);
  }
}
