import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { Email } from '../types/api-output';
import { EmailType } from '../types/api-request';

/**
 * Handles email generation.
 */
@Injectable({
  providedIn: 'root',
})
export class EmailGenerationService {

  constructor(private httpRequestService: HttpRequestService) { }

  getCourseJoinEmail(courseid: string, studentid: string): Observable<Email> {
    return this.getEmail({
      courseid,
      studentid,
      emailtype: EmailType.STUDENT_COURSE_JOIN,
    });
  }

  getFeedbackSessionReminderEmail(studentid: string, fsid: string): Observable<Email> {
    return this.getEmail({
      studentid,
      emailtype: EmailType.FEEDBACK_SESSION_REMINDER,
      fsid,
    });
  }

  /**
   * Get email contents by calling API.
   */
  private getEmail(params: {
    studentid: string,
    emailtype: EmailType,
    courseid?: string,
    fsid?: string,
  }): Observable<Email> {
    const paramsMap: Record<string, string> = {
      studentid: params.studentid,
      emailtype: params.emailtype,
      ...(params.courseid && { courseid: params.courseid }),
      ...(params.fsid && { fsid: params.fsid }),
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL, paramsMap);
  }
}
