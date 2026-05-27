import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { environment } from '../environments/environment.prod';
import { map } from 'rxjs/operators';

/**
 * Guards routes based on user authentication status.
 * Redirects to login page if user is not authenticated.
 */
export class AuthGuard implements CanActivate, CanActivateChild {
  canActivate(_: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const authService = inject(AuthService);
    const backendUrl: string = environment.backendUrl;

    return authService.getAuthUser(state.url).pipe(
      map((authInfo: AuthInfo) => {
        console.log(`[authGuard] Checking auth for: ${state.url}`);
        if (!authInfo.user) {
          console.log(`[authGuard] No authenticated user — redirecting to login`);
          this.redirectToLogin(authInfo, backendUrl);
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
