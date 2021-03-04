import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LogTypes, ResourceEndpoints } from '../types/api-const';
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
    studentEmail: string,
    logType: LogTypes }): Observable<string> {
    const paramMap: Record<string, string> = {
      courseId: queryParams.courseId,
      studentEmail: queryParams.studentEmail,
      fsltype: queryParams.logType.toString(),
    };

    return this.httpRequestService.get(ResourceEndpoints.TRACK_SESSION, paramMap);
  }

}
