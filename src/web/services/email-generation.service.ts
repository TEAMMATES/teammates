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
  getEmail(courseId: string, studentemail: string, emailtype: string, fsname?: string): Observable<Email> {
    const paramsMap: Record<string, string> = {
      studentemail,
      emailtype,
      courseid: courseId,
      ...(fsname && { fsname }),
    };
    return this.httpRequestService.get(ResourceEndpoints.EMAIL, paramsMap);
  }
}
