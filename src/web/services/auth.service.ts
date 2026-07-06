import { Injectable, inject, signal } from '@angular/core';
import { Observable, of, tap } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { AuthInfo } from '../types/api-output';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private httpRequestService = inject(HttpRequestService);

  private authInfo = signal<AuthInfo>({ masquerade: false });

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
    this.authInfo.set({ masquerade: false });
  }
}
