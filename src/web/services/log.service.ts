import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LogType, ResourceEndpoints } from '../types/api-const';
import { ActionClasses, FeedbackSessionLogs, GeneralLogs } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Advanced filters model for searching of logs.
 */
export interface AdvancedFilters {
  actionClass?: string;
  traceId?: string;
  googleId?: string;
  regkey?: string;
  email?: string;
  sourceLocationFile?: string;
  sourceLocationFunction?: string;
  exceptionClass?: string;
}

/**
 * Query parameters for logs endpoint.
 */
export interface LogsEndpointQueryParams {
  searchFrom: string;
  searchUntil: string;
  severity?: string;
  minSeverity?: string;
  logEvent?: string;
  order?: string;
  advancedFilters: AdvancedFilters;
}

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
    logType: LogType }): Observable<string> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      fsname: queryParams.feedbackSessionName,
      studentemail: queryParams.studentEmail,
      fsltype: queryParams.logType.toString(),
    };

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
  }): Observable<FeedbackSessionLogs> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      fslstarttime: queryParams.searchFrom,
      fslendtime: queryParams.searchUntil,
    };

    if (queryParams.studentEmail) {
      paramMap.studentemail = queryParams.studentEmail;
    }

    if (queryParams.sessionName) {
      paramMap.fsname = queryParams.sessionName;
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSION_LOGS, paramMap);
  }

  searchLogs(queryParams: LogsEndpointQueryParams): Observable<GeneralLogs> {
    const paramMap: Record<string, string> = {
      starttime: queryParams.searchFrom,
      endtime: queryParams.searchUntil,
    };

    if (queryParams.order) {
      paramMap.order = queryParams.order;
    }

    if (queryParams.severity) {
      paramMap.severity = queryParams.severity;
    }

    if (queryParams.minSeverity) {
      paramMap.minseverity = queryParams.minSeverity;
    }

    if (queryParams.logEvent) {
      paramMap.logevent = queryParams.logEvent;
    }

    if (queryParams.advancedFilters.actionClass) {
      paramMap.actionclass = queryParams.advancedFilters.actionClass;
    }

    if (queryParams.advancedFilters.traceId) {
      paramMap.traceid = queryParams.advancedFilters.traceId;
    }

    if (queryParams.advancedFilters.googleId) {
      paramMap.googleid = queryParams.advancedFilters.googleId;
    }

    if (queryParams.advancedFilters.regkey) {
      paramMap.key = queryParams.advancedFilters.regkey;
    }

    if (queryParams.advancedFilters.email) {
      paramMap.email = queryParams.advancedFilters.email;
    }

    if (queryParams.advancedFilters.sourceLocationFile) {
      paramMap.sourcelocationfile = queryParams.advancedFilters.sourceLocationFile;
    }

    if (queryParams.advancedFilters.sourceLocationFunction) {
      paramMap.sourcelocationfunction = queryParams.advancedFilters.sourceLocationFunction;
    }

    if (queryParams.advancedFilters.exceptionClass) {
      paramMap.exceptionclass = queryParams.advancedFilters.exceptionClass;
    }

    return this.httpRequestService.get(ResourceEndpoints.LOGS, paramMap);
  }

  getActionClassList(): Observable<ActionClasses> {
    return this.httpRequestService.get(ResourceEndpoints.ACTION_CLASS);
  }
}
