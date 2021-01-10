import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ResourceEndpoints } from '../types/api-const';
import { AuthInfo, RegkeyValidity } from '../types/api-output';
import { Intent } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private frontendUrl: string = environment.frontendUrl;

  constructor(private httpRequestService: HttpRequestService) {}

  /**
   * Gets the user authentication information.
   */
  getAuthUser(user?: string, nextUrl?: string): Observable<AuthInfo> {
    const params: Record<string, string> = { frontendUrl: this.frontendUrl };
    if (user) {
      params.user = user;
    }
    if (nextUrl) {
      params.nextUrl = nextUrl;
    }
    return this.httpRequestService.get(ResourceEndpoints.AUTH, params);
  }

  /**
   * Gets the validity of the given registration key for user.
   */
  getAuthRegkeyValidity(key: string, intent: Intent): Observable<RegkeyValidity> {
    const params: Record<string, string> = { key, intent };
    return this.httpRequestService.get(ResourceEndpoints.AUTH_REGKEY, params);
  }

}
