import { Injectable, inject, signal } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { AuthInfo, RegkeyValidity } from '../types/api-output';
import { Intent } from '../types/api-request';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private httpRequestService = inject(HttpRequestService);

  private authInfo = signal<AuthInfo>({ loginUrl: '/', masquerade: false });

  /**
   * Gets the user authentication information.
   * Returns the cached value if the user is already authenticated, otherwise fetches from the server.
   */
  getAuthUser(nextUrl?: string): Observable<AuthInfo> {
    const cached = this.authInfo();
    if (cached?.user) {
      return of(cached);
    }

    const params: Record<string, string> = {};
    if (nextUrl) {
      params[QueryParamKeys.NEXT_URL] = nextUrl;
    }

    return this.httpRequestService
      .get<AuthInfo>(ResourceEndpoints.AUTH, params)
      .pipe(tap((authInfo: AuthInfo) => this.authInfo.set(authInfo)));
  }

  /**
   * Clears the cached authentication information.
   */
  clearAuthCache(): void {
    this.authInfo.set({ loginUrl: '/', masquerade: false });
  }

  /**
   * Gets the validity of the given registration key for user.
   */
  getAuthRegkeyValidity(key: string, intent: Intent): Observable<RegkeyValidity> {
    const params: Record<string, string> = { key, intent };
    return this.httpRequestService.get(ResourceEndpoints.AUTH_REGKEY, params);
  }
}
