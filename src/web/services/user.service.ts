import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { RegenerateKey } from '../types/api-output';

/**
 * Handles user-related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Regenerates the registration key for a user in a course.
   */
  regenerateUserKey(userId: string): Observable<RegenerateKey> {
    const paramsMap: Record<string, string> = {
      userid: userId,
    };
    return this.httpRequestService.post(ResourceEndpoints.USER_KEY, paramsMap);
  }
}
