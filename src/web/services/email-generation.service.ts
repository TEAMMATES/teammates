import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { Email } from '../types/api-output';
import { EmailType } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles email generation.
 */
@Injectable({
  providedIn: 'root',
})
export class EmailGenerationService {

  constructor(private httpRequestService: HttpRequestService) { }

  getCourseJoinEmail(courseId: string, studentEmail: string): Observable<Email> {
    return this.getEmail(courseId, studentEmail, EmailType.STUDENT_COURSE_JOIN);
  }

  getFeedbackSessionReminderEmail(courseId: string, studentEmail: string, fsname: string): Observable<Email> {
    return this.getEmail(courseId, studentEmail, EmailType.FEEDBACK_SESSION_REMINDER, fsname);
  }

  /**
   * Get email contents by calling API.
   */
  private getEmail(courseId: string, studentemail: string, emailtype: EmailType, fsname?: string): Observable<Email> {
    const paramsMap: Record<string, string> = {
      studentemail,
      emailtype,
      courseid: courseId,
      ...(fsname && { fsname }),
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL, paramsMap);
  }
}
