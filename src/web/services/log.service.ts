import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LogType, ResourceEndpoints } from '../types/api-const';
import { ActionClasses, FeedbackSessionLogs, GeneralLogs } from '../types/api-output';
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

  searchLogs(queryParams: {
    searchFrom: string,
    searchUntil: string,
    severity?: string,
    minSeverity?: string,
    logEvent?: string,
    nextPageToken?: string,
    actionClass?: string,
    traceId?: string,
    googleId?: string,
    regkey?: string,
    email?: string,
    sourceLocationFile?: string,
    sourceLocationFunction?: string,
    exceptionClass?: string,
  }): Observable<GeneralLogs> {
    const paramMap: Record<string, string> = {
      starttime: queryParams.searchFrom,
      endtime: queryParams.searchUntil,
    };

    if (queryParams.severity) {
      paramMap.severity = queryParams.severity;
    }

    if (queryParams.minSeverity) {
      paramMap.minseverity = queryParams.minSeverity;
    }

    if (queryParams.logEvent) {
      paramMap.logevent = queryParams.logEvent;
    }

    if (queryParams.nextPageToken) {
      paramMap.nextpagetoken = queryParams.nextPageToken;
    }

    if (queryParams.actionClass) {
      paramMap.actionclass = queryParams.actionClass;
    }

    if (queryParams.traceId) {
      paramMap.traceid = queryParams.traceId;
    }

    if (queryParams.googleId) {
      paramMap.googleid = queryParams.googleId;
    }

    if (queryParams.regkey) {
      paramMap.regkey = queryParams.regkey;
    }

    if (queryParams.email) {
      paramMap.email = queryParams.email;
    }

    if (queryParams.sourceLocationFile) {
      paramMap.sourcelocationfile = queryParams.sourceLocationFile;
    }

    if (queryParams.sourceLocationFunction) {
      paramMap.sourcelocationfunction = queryParams.sourceLocationFunction;
    }

    if (queryParams.exceptionClass) {
      paramMap.exceptionclass = queryParams.exceptionClass;
    }

    return this.httpRequestService.get(ResourceEndpoints.LOGS, paramMap);
  }

  getActionClassList(): Observable<ActionClasses> {
    return this.httpRequestService.get(ResourceEndpoints.ACTION_CLASS);
  }
}
