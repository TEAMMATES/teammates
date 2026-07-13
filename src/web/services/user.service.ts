import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { MessageOutput, SessionLinks } from '../types/api-output';

/**
 * Handles user-related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Regenerates the key for a user in a course.
   */
  regenerateUserKey(userId: string): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: userId,
    };
    return this.httpRequestService.post(ResourceEndpoints.USER_KEY, paramsMap);
  }

  /**
   * Retrieves all session links for a user.
   */
  getSessionLinks(userId: string): Observable<SessionLinks> {
    const paramsMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: userId,
    };
    return this.httpRequestService.get(ResourceEndpoints.SESSION_LINKS, paramsMap);
  }
}
