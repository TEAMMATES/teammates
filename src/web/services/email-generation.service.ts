import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-endpoints';
import { Email } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles email generation.
 */
@Injectable({
  providedIn: 'root',
})
export class EmailGenerationService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Get email contents by calling API.
   */
  getEmail(queryParams: {
    courseId: string, studentemail: string, emailtype: string, fsname?: string,
  }): Observable<Email> {
    const paramsMap: Record<string, string> = {
      courseid: queryParams.courseId,
      studentemail: queryParams.studentemail,
      emailtype: queryParams.emailtype,
      ...(queryParams.fsname && { fsname: queryParams.fsname }),
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL, paramsMap);
  }
}
