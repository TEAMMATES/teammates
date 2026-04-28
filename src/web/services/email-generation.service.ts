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

  getCourseJoinEmail(studentId: string): Observable<Email> {
    return this.getEmail({
      studentId,
      emailType: EmailType.STUDENT_COURSE_JOIN,
    });
  }

  getFeedbackSessionReminderEmail(studentId: string, fsId: string): Observable<Email> {
    return this.getEmail({
      studentId,
      emailType: EmailType.FEEDBACK_SESSION_REMINDER,
      fsId,
    });
  }

  /**
   * Get email contents by calling API.
   */
  private getEmail(params: {
    studentId: string,
    emailType: EmailType,
    fsId?: string,
  }): Observable<Email> {
    const paramsMap: Record<string, string> = {
      studentid: params.studentId,
      emailtype: params.emailType,
      ...(params.fsId && { fsid: params.fsId }),
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL, paramsMap);
  }
}
