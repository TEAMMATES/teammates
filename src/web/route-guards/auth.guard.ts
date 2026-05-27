import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject, Injectable } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { environment } from '../environments/environment.prod';
import { map } from 'rxjs/operators';

/**
 * Guards routes based on user authentication status.
 * Redirects to login page if user is not authenticated.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate, CanActivateChild {
  private authService = inject(AuthService);
  private backendUrl: string = environment.backendUrl;

  canActivate(_: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authService.getAuthUser(state.url).pipe(
      map((authInfo: AuthInfo) => {
        console.log(`[authGuard] Checking auth for: ${state.url}`);
        if (!authInfo.user) {
          console.log(`[authGuard] No authenticated user — redirecting to login`);
          this.redirectToLogin(authInfo, this.backendUrl);
          return false;
        }
        console.log(`[authGuard] Authenticated as ${authInfo.user.id} — access granted`);
        return true;
      }),
    );
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.canActivate(childRoute, state);
  }

  private redirectToLogin(authInfo: AuthInfo, backendUrl: string) {
    window.location.href = `${backendUrl}${authInfo.loginUrl}`;
  }
}
