import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject, Injectable } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { environment } from '../environments/environment';
import { map } from 'rxjs/operators';

/**
 * Guards routes based on user roles.
 * Redirects to login page if user is not authenticated or does not have the required role.
 */
@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate, CanActivateChild {
  private authService = inject(AuthService);
  private backendUrl: string = environment.backendUrl;
  private router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const expectedRole: string = route.data['role'];

    return this.authService.getAuthUser(state.url).pipe(
      map((authInfo: AuthInfo) => {
        if (!authInfo.user) {
          return this.redirectToLogin(authInfo, this.backendUrl);
        }

        const isRoleMatch = this.matchRole(authInfo, expectedRole);
        if (!isRoleMatch) {
          return this.redirectToUnauthorized(expectedRole);
        }
        return true;
      }),
    );
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    let route: ActivatedRouteSnapshot | null = childRoute;
    while (route && !route.data['role']) {
      route = route.parent;
    }
    return this.canActivate(route ?? childRoute, state);
  }

  private matchRole(authInfo: AuthInfo, expectedRole: string): boolean {
    const user = authInfo.user;

    return (
      (expectedRole === 'instructor' && !!user?.isInstructor) ||
      (expectedRole === 'student' && !!user?.isStudent) ||
      (expectedRole === 'admin' && !!user?.isAdmin) ||
      (expectedRole === 'maintainer' && !!user?.isMaintainer)
    );
  }

  private redirectToLogin(authInfo: AuthInfo, backendUrl: string) {
    globalThis.location.href = `${backendUrl}${authInfo.loginUrl}`;
    return false;
  }

  private redirectToUnauthorized(expectedRole: string) {
    return this.router.parseUrl(`/web/unauthorized?role=${expectedRole}`);
  }
}
