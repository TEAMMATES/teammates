import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject, Injectable } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { map } from 'rxjs/operators';

export enum UserRole {
  STUDENT = 'student',
  INSTRUCTOR = 'instructor',
  ADMIN = 'admin',
  MAINTAINER = 'maintainer',
}

/**
 * Guards routes based on user roles.
 * If no expected role is specified in the route data, it only checks for authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate, CanActivateChild {
  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const expectedRole: UserRole = route.data['role'];

    return this.authService.getAuthUser().pipe(
      map((authInfo: AuthInfo) => {
        if (!authInfo.user) {
          return this.redirectToLogin(state.url);
        }

        // Authenticated user without role requirement.
        if (!expectedRole) {
          return true;
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

  private matchRole(authInfo: AuthInfo, expectedRole: UserRole): boolean {
    const user = authInfo.user;

    return (
      (expectedRole === UserRole.INSTRUCTOR && !!user?.isInstructor) ||
      (expectedRole === UserRole.STUDENT && !!user?.isStudent) ||
      (expectedRole === UserRole.ADMIN && !!user?.isAdmin) ||
      (expectedRole === UserRole.MAINTAINER && !!user?.isMaintainer)
    );
  }

  private redirectToLogin(nextUrl: string) {
    return this.router.createUrlTree(['/web/login'], { queryParams: { nextUrl: nextUrl } });
  }

  private redirectToUnauthorized(expectedRole: UserRole) {
    return this.router.createUrlTree(['/web/unauthorized'], { queryParams: { role: expectedRole } });
  }
}
