import { Injectable, inject } from '@angular/core';
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
  private httpRequestService = inject(HttpRequestService);

  getCourseJoinEmail(userId: string): Observable<Email> {
    return this.getEmail({
      userId,
      emailType: EmailType.STUDENT_COURSE_JOIN,
    });
  }

  getFeedbackSessionReminderEmail(userId: string, fsId: string): Observable<Email> {
    return this.getEmail({
      userId,
      emailType: EmailType.FEEDBACK_SESSION_REMINDER,
      fsId,
    });
  }

  /**
   * Get email contents by calling API.
   */
  private getEmail(params: { userId: string; emailType: EmailType; fsId?: string }): Observable<Email> {
    const paramsMap: Record<string, string> = {
      userid: params.userId,
      emailtype: params.emailType,
      ...(params.fsId && { fsid: params.fsId }),
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL, paramsMap);
  }
}
