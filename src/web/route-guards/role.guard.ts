import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { environment } from '../environments/environment.prod';
import { map } from 'rxjs/operators';
import { NavigationService } from '../services/navigation.service';

/**
 * Guards routes based on user roles.
 * Redirects to login page if user is not authenticated or does not have the required role.
 */
export class RoleGuard implements CanActivate, CanActivateChild {
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const authService = inject(AuthService);
    const navigationService = inject(NavigationService);
    const backendUrl: string = environment.backendUrl;
    const expectedRole: string = route.data['role'];

    return authService.getAuthUser(state.url).pipe(
      map((authInfo: AuthInfo) => {
        console.log(`[roleGuard] Checking role '${expectedRole}' for: ${state.url}`);
        if (!authInfo.user) {
          console.log(`[roleGuard] No authenticated user — redirecting to login`);
          this.redirectToLogin(authInfo, backendUrl);
          return false;
        }

        const isRoleMatch = this.matchRole(authInfo, expectedRole);
        if (!isRoleMatch) {
          console.log(`[roleGuard] User '${authInfo.user.id}' does not have required role '${expectedRole}'`);
          if (expectedRole === 'admin') {
            // User not authorized to view admin page.
            navigationService.navigateWithErrorMessage('/web', 'You are not authorized to view the page.');
          } else {
            this.redirectToLogin(authInfo, backendUrl);
          }
          return false;
        }
        console.log(`[roleGuard] User '${authInfo.user.id}' has role '${expectedRole}' — access granted`);
        return true;
      }),
    );
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.canActivate(childRoute, state);
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
    window.location.href = `${backendUrl}${authInfo.loginUrl}`;
  }
}
