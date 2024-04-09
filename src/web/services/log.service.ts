import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import {
  ActionClasses,
  FeedbackSessionLogs,
  FeedbackSessionLogType,
  GeneralLogs,
  QueryLogsParams,
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

  searchLogs(queryParams: Partial<QueryLogsParams>): Observable<GeneralLogs> {
    const paramMap: Record<string, string> = {
      starttime: `${queryParams.startTime || -1}`,
      endtime: `${queryParams.endTime || -1}`,
    };

    if (queryParams.order) {
      paramMap['order'] = queryParams.order;
    }

    if (queryParams.severity) {
      paramMap['severity'] = queryParams.severity;
    }

    if (queryParams.minSeverity) {
      paramMap['minseverity'] = queryParams.minSeverity;
    }

    if (queryParams.logEvent) {
      paramMap['logevent'] = queryParams.logEvent;
    }

    if (queryParams.actionClass) {
      paramMap['actionclass'] = queryParams.actionClass;
    }

    if (queryParams.traceId) {
      paramMap['traceid'] = queryParams.traceId;
    }

    if (queryParams.userInfoParams) {
      if (queryParams.userInfoParams.googleId) {
        paramMap['googleid'] = queryParams.userInfoParams.googleId;
      }

      if (queryParams.userInfoParams.regkey) {
        paramMap['key'] = queryParams.userInfoParams.regkey;
      }

      if (queryParams.userInfoParams.email) {
        paramMap['email'] = queryParams.userInfoParams.email;
      }
    }

    if (queryParams.sourceLocation) {
      if (queryParams.sourceLocation.file) {
        paramMap['sourcelocationfile'] = queryParams.sourceLocation.file;
      }

      if (queryParams.sourceLocation.function) {
        paramMap['sourcelocationfunction'] = queryParams.sourceLocation.function;
      }
    }

    if (queryParams.exceptionClass) {
      paramMap['exceptionclass'] = queryParams.exceptionClass;
    }

    if (queryParams.latency) {
      paramMap['latency'] = queryParams.latency;
    }

    if (queryParams.status) {
      paramMap['status'] = queryParams.status;
    }

    if (queryParams.version) {
      paramMap['version'] = queryParams.version;
    }

    if (queryParams.extraFilters) {
      paramMap['extrafilters'] = queryParams.extraFilters;
    }

    return this.httpRequestService.get(ResourceEndpoints.LOGS, paramMap);
  }

  getActionClassList(): Observable<ActionClasses> {
    return this.httpRequestService.get(ResourceEndpoints.ACTION_CLASS);
  }
}
