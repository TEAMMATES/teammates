import { CanActivateChildFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { environment } from '../environments/environment.prod';
import { map } from 'rxjs/operators';

export const roleGuard: CanActivateChildFn = (childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const backendUrl: string = environment.backendUrl;
  const expectedRole: string = childRoute.data['role'];

  const isAuthorized = authService.getAuthUser(state.url).pipe(
    map((authInfo: AuthInfo) => {
      if (!authInfo.user) {
        redirectToLogin(authInfo, backendUrl);
        return false;
      }

      const isRoleMatch = matchRole(authInfo, expectedRole);
      if (!isRoleMatch) {
        redirectToLogin(authInfo, backendUrl);
        return false;
      }
      return true;
    }),
  );

  return isAuthorized;
};

const matchRole = (authInfo: AuthInfo, expectedRole: string): boolean => {
  const user = authInfo.user;

  return (
    (expectedRole === 'instructor' && !!user?.isInstructor) ||
    (expectedRole === 'student' && !!user?.isStudent) ||
    (expectedRole === 'admin' && !!user?.isAdmin) ||
    (expectedRole === 'maintainer' && !!user?.isMaintainer)
  );
};

const redirectToLogin = (authInfo: AuthInfo, backendUrl: string) => {
  window.location.href = `${backendUrl}${authInfo.loginUrl}`;
};
