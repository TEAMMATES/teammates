import { CanActivateChildFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { environment } from '../environments/environment.prod';
import { map } from 'rxjs/operators';

/**
 * Guards routes based on user authentication status.
 * Redirects to login page if user is not authenticated.
 */
export const authGuard: CanActivateChildFn = (_: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const backendUrl: string = environment.backendUrl;

  return authService.getAuthUser(state.url).pipe(
    map((authInfo: AuthInfo) => {
      if (!authInfo.user) {
        redirectToLogin(authInfo, backendUrl);
        return false;
      }
      return true;
    }),
  );
};

const redirectToLogin = (authInfo: AuthInfo, backendUrl: string) => {
  window.location.href = `${backendUrl}${authInfo.loginUrl}`;
}
